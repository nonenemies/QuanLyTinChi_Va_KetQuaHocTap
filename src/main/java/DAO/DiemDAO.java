package DAO;

import Model.BangDiemSinhVien;
import Model.KetQuaHocTap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Truy cập cơ sở dữ liệu để lưu điểm thành phần của sinh viên.
public class DiemDAO extends BaseDAO {

    /** Lấy các học phần sinh viên đã đăng ký, không gồm các đăng ký đã hủy. */
    public List<KetQuaHocTap> layKetQuaHocTap(String maSinhVien) throws SQLException {
        String sql = "SELECT lh.MaLop, mh.MaMon, mh.TenMon, lh.HocKy, mh.SoTinChi, "
                + "dk.DiemTongKet, dk.TrangThai, "
                + "COALESCE((SELECT GROUP_CONCAT(CONCAT(tp.TenThanhPhan, ': ', d.DiemDatDuoc) "
                + "ORDER BY tp.ThuTu, tp.MaThanhPhan SEPARATOR ' | ') "
                + "FROM DiemThanhPhan d JOIN ThanhPhanDanhGia tp ON tp.MaThanhPhan = d.MaThanhPhan "
                + "WHERE d.MaDangKy = dk.MaDangKy), '') AS DiemThanhPhan, "
                + "COALESCE((SELECT GROUP_CONCAT(CONCAT('Thứ ', lich.Thu, ' ', "
                + "TIME_FORMAT(lich.GioBatDau, '%H:%i'), '-', TIME_FORMAT(lich.GioKetThuc, '%H:%i'), "
                + "' · ', COALESCE(NULLIF(TRIM(lich.PhongHoc), ''), 'Chưa cập nhật phòng')) "
                + "ORDER BY lich.Thu, lich.GioBatDau SEPARATOR ' | ') "
                + "FROM LichHoc lich WHERE lich.MaLop = lh.MaLop), 'Chưa xếp lịch') AS LichHoc "
                + "FROM DangKyHoc dk "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "JOIN MonHoc mh ON mh.MaMon = lh.MaMon "
                + "WHERE dk.MaSinhVien = ? AND dk.TrangThai <> 'HUY' "
                + "ORDER BY lh.HocKy, lh.MaLop, mh.MaMon";
        List<KetQuaHocTap> results = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.math.BigDecimal finalGrade = rs.getBigDecimal("DiemTongKet");
                    results.add(new KetQuaHocTap(
                            rs.getString("MaLop"),
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getString("HocKy"),
                            rs.getString("LichHoc"),
                            rs.getObject("SoTinChi", Integer.class),
                            finalGrade == null ? null : finalGrade.floatValue(),
                            rs.getString("TrangThai"),
                            rs.getString("DiemThanhPhan")));
                }
            }
        }
        return results;
    }

    /** Chỉ cập nhật khi mỗi thành phần đang hoạt động có đúng một điểm. */
    public int capNhatDiemTongKet(String maSinhVien, String maLop) throws SQLException {
        String sql = "UPDATE DangKyHoc dk JOIN ("
                + "SELECT dk2.MaDangKy, ROUND(SUM(tp.TrongSo * d.DiemDatDuoc), 2) AS TongDiem "
                + "FROM DangKyHoc dk2 "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk2.MaLop "
                + "JOIN ThanhPhanDanhGia tp ON tp.MaMon = lh.MaMon AND tp.TrangThai = 1 "
                + "LEFT JOIN DiemThanhPhan d ON d.MaDangKy = dk2.MaDangKy "
                + "AND d.MaThanhPhan = tp.MaThanhPhan "
                + "WHERE dk2.MaSinhVien = ? AND dk2.MaLop = ? "
                + "GROUP BY dk2.MaDangKy "
                + "HAVING COUNT(*) = COUNT(DISTINCT tp.MaThanhPhan) "
                + "AND COUNT(d.DiemDatDuoc) = COUNT(*)"
                + ") ketQua ON ketQua.MaDangKy = dk.MaDangKy "
                + "SET dk.DiemTongKet = ketQua.TongDiem, "
                + "dk.TrangThai = IF(ketQua.TongDiem >= 4.0, 'DA_HOAN_THANH', 'KHONG_DAT')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            return ps.executeUpdate();
        }
    }

    /** Tải danh sách sinh viên và điểm của các thành phần đang hoạt động trong lớp. */
    public List<BangDiemSinhVien> layBangDiemTheoLop(String maLop) throws SQLException {
        String sql = "SELECT dk.MaDangKy, dk.MaSinhVien, u.HoTen, dk.DiemTongKet, dk.TrangThai, "
                + "tp.MaThanhPhan, d.DiemDatDuoc "
                + "FROM DangKyHoc dk "
                + "JOIN SinhVien sv ON sv.MaSinhVien = dk.MaSinhVien "
                + "JOIN Users u ON u.UserID = sv.UserID "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "LEFT JOIN ThanhPhanDanhGia tp ON tp.MaMon = lh.MaMon AND tp.TrangThai = 1 "
                + "LEFT JOIN DiemThanhPhan d ON d.MaDangKy = dk.MaDangKy "
                + "AND d.MaThanhPhan = tp.MaThanhPhan "
                + "WHERE dk.MaLop = ? AND dk.TrangThai <> 'HUY' "
                + "ORDER BY u.HoTen, dk.MaSinhVien, tp.ThuTu, tp.MaThanhPhan";
        Map<Integer, BangDiemSinhVien> rows = new LinkedHashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int maDangKy = rs.getInt("MaDangKy");
                    BangDiemSinhVien row = rows.get(maDangKy);
                    if (row == null) {
                        java.math.BigDecimal total = rs.getBigDecimal("DiemTongKet");
                        row = new BangDiemSinhVien(maDangKy, rs.getString("MaSinhVien"),
                                rs.getString("HoTen"), total == null ? null : total.floatValue(),
                                rs.getString("TrangThai"));
                        rows.put(maDangKy, row);
                    }
                    int maThanhPhan = rs.getInt("MaThanhPhan");
                    if (!rs.wasNull()) {
                        java.math.BigDecimal score = rs.getBigDecimal("DiemDatDuoc");
                        row.setDiemThanhPhan(maThanhPhan, score == null ? null : score.floatValue());
                    }
                }
            }
        }
        return new ArrayList<>(rows.values());
    }

    /** Thêm hoặc cập nhật điểm theo mã thành phần. */
    public boolean nhapDiem(String maSinhVien, String maLop,
                            int maThanhPhan, java.math.BigDecimal diemDatDuoc) throws SQLException {
        try (Connection conn = getConnection()) {
        int maDangKy;
        String registrationSql = "SELECT MaDangKy FROM DangKyHoc "
                + "WHERE MaSinhVien = ? AND MaLop = ? "
                + "AND TrangThai IN ('DANG_HOC', 'DA_HOAN_THANH', 'KHONG_DAT')";
        try (PreparedStatement ps = conn.prepareStatement(registrationSql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                maDangKy = rs.getInt("MaDangKy");
                if (rs.next()) {
                    throw new SQLException("Multiple active registrations found for the same student and class.");
                }
            }
        }

        String termSql = "SELECT lh.HocKy FROM LopHocPhan lh WHERE lh.MaLop = ?";
        String hocKy;
        try (PreparedStatement ps = conn.prepareStatement(termSql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                hocKy = rs.getString("HocKy");
            }
        }

        if (new CauHinhHocKyDAO().layCauHinhTrongDotNhapDiem(hocKy) == null) return false;

        String componentSql = "SELECT 1 FROM ThanhPhanDanhGia tp "
                + "JOIN LopHocPhan lh ON lh.MaMon = tp.MaMon "
                + "WHERE tp.MaThanhPhan = ? AND lh.MaLop = ? AND tp.TrangThai = 1";
        try (PreparedStatement ps = conn.prepareStatement(componentSql)) {
            ps.setInt(1, maThanhPhan);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
            }
        }

        int maDiemThanhPhan;
        String existingSql = "SELECT MaDiemThanhPhan FROM DiemThanhPhan "
                + "WHERE MaDangKy = ? AND MaThanhPhan = ?";
        try (PreparedStatement ps = conn.prepareStatement(existingSql)) {
            ps.setInt(1, maDangKy);
            ps.setInt(2, maThanhPhan);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO DiemThanhPhan (MaDangKy, MaThanhPhan, DiemDatDuoc) VALUES (?, ?, ?)")) {
                        insert.setInt(1, maDangKy);
                        insert.setInt(2, maThanhPhan);
                        insert.setBigDecimal(3, diemDatDuoc);
                        return insert.executeUpdate() == 1;
                    }
                }
                maDiemThanhPhan = rs.getInt("MaDiemThanhPhan");
                if (rs.next()) {
                    throw new SQLException("Duplicate scores exist for the same registration and component.");
                }
            }
        }
        try (PreparedStatement update = conn.prepareStatement(
                "UPDATE DiemThanhPhan SET DiemDatDuoc = ?, NgayNhap = CURRENT_TIMESTAMP "
                        + "WHERE MaDiemThanhPhan = ?")) {
            update.setBigDecimal(1, diemDatDuoc);
            update.setInt(2, maDiemThanhPhan);
            return update.executeUpdate() == 1;
        }
        }
    }

    public boolean coDiemTrongMon(String maMon) throws SQLException {
        String sql = "SELECT 1 FROM DiemThanhPhan d "
                + "JOIN DangKyHoc dk ON dk.MaDangKy = d.MaDangKy "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "WHERE lh.MaMon = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean giangVienConHoatDong(String maGiangVien) throws SQLException {
        String sql = "SELECT 1 FROM GiangVien gv JOIN Users u ON u.UserID = gv.UserID "
                + "WHERE gv.MaGiangVien = ? AND u.VaiTro = 'GIAOVIEN' AND u.TrangThai = 'HOAT_DONG'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGiangVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
