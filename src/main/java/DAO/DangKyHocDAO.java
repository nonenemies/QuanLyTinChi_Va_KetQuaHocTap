package DAO;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.DangKyHoc;
import Model.LopHocThoiKhoaBieu;

public class DangKyHocDAO extends BaseDAO {

    /** Khóa dòng sinh viên để các đăng ký đồng thời của cùng một sinh viên được tuần tự hóa. */
    public boolean sinhVienConHoatDong(String maSinhVien) throws SQLException {
        String sql = "SELECT sv.MaSinhVien FROM SinhVien sv "
                + "JOIN Users u ON u.UserID = sv.UserID "
                + "WHERE sv.MaSinhVien = ? AND u.VaiTro = 'SINHVIEN' AND u.TrangThai = 'HOAT_DONG'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean coLichHoc(String maLop) throws SQLException {
        String sql = "SELECT 1 FROM LichHoc WHERE MaLop = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Kiểm tra lớp đăng ký mới có trùng lịch với các lớp đang học trong cùng học kỳ không. */
    public boolean coLopBiTrungLich(String maSinhVien, String maLopMoi) throws SQLException {
        String sql = "SELECT 1 "
                + "FROM DangKyHoc dk "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "JOIN LichHoc lich ON lich.MaLop = lh.MaLop "
                + "JOIN LopHocPhan lopMoi ON lopMoi.MaLop = ? "
                + "JOIN LichHoc lichMoi ON lichMoi.MaLop = lopMoi.MaLop "
                + "WHERE dk.MaSinhVien = ? AND dk.TrangThai <> 'HUY' "
                + "AND lh.HocKy = lopMoi.HocKy "
                + "AND lich.Thu = lichMoi.Thu "
                + "AND lich.GioBatDau < lichMoi.GioKetThuc "
                + "AND lich.GioKetThuc > lichMoi.GioBatDau "
                + "ORDER BY lich.Thu, lich.GioBatDau LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLopMoi);
            ps.setString(2, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Ném SQLException, dùng Connection truyền vào
    public boolean kiemTraDaDangKy(String maSinhVien, String maLop) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc "
                + "WHERE MaSinhVien = ? AND MaLop = ? AND TrangThai <> 'HUY'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean daDangKyMonTrongHocKy(String maSinhVien, String maMon, String hocKy)
            throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc dk "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "WHERE dk.MaSinhVien = ? AND lh.MaMon = ? AND lh.HocKy = ? "
                + "AND dk.TrangThai = 'DANG_HOC' LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maMon);
            ps.setString(3, hocKy);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int tongTinChiDangHoc(String maSinhVien, String hocKy) throws SQLException {
        String sql = "SELECT COALESCE(SUM(mh.SoTinChi), 0) AS TongTinChi "
                + "FROM DangKyHoc dk JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "JOIN MonHoc mh ON mh.MaMon = lh.MaMon "
                + "WHERE dk.MaSinhVien = ? AND lh.HocKy = ? AND dk.TrangThai = 'DANG_HOC'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, hocKy);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("TongTinChi") : 0;
            }
        }
    }

    public int laySoTinChi(String maMon) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT SoTinChi FROM MonHoc WHERE MaMon = ? AND TrangThai = 'HOAT_DONG'")) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("SoTinChi") : 0;
            }
        }
    }

    public boolean daDatMon(String maSinhVien, String maMon) throws SQLException {
        return kiemTraDaQuaMon(maSinhVien, maMon);
    }

    public boolean coDiem(String maSinhVien, String maLop) throws SQLException {
        String sql = "SELECT 1 FROM DiemThanhPhan d "
                + "JOIN DangKyHoc dk ON dk.MaDangKy = d.MaDangKy "
                + "WHERE dk.MaSinhVien = ? AND dk.MaLop = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String layTrangThaiDangKy(String maSinhVien, String maLop)
            throws SQLException {
        String sql = "SELECT TrangThai FROM DangKyHoc WHERE MaSinhVien = ? AND MaLop = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("TrangThai") : null;
            }
        }
    }

    public boolean huyDangKy(String maSinhVien, String maLop) throws SQLException {
        String sql = "UPDATE DangKyHoc SET TrangThai = 'HUY' "
                + "WHERE MaSinhVien = ? AND MaLop = ? AND TrangThai = 'DANG_HOC'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maLop);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean kiemTraDaQuaMon(String maSinhVien, String maMonHoc) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc d " +
                "JOIN LopHocPhan l ON d.MaLop = l.MaLop " +
                "WHERE d.MaSinhVien = ? AND l.MaMon = ? " +
                "AND d.DiemTongKet >= 4.0 AND d.TrangThai = 'DA_HOAN_THANH'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            ps.setString(2, maMonHoc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean themDangKyMoi(DangKyHoc dk) throws SQLException {
        String reactivateSql = "UPDATE DangKyHoc SET TrangThai = 'DANG_HOC' "
                + "WHERE MaSinhVien = ? AND MaLop = ? AND TrangThai = 'HUY' "
                + "AND NOT EXISTS (SELECT 1 FROM DiemThanhPhan d WHERE d.MaDangKy = DangKyHoc.MaDangKy)";
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(reactivateSql)) {
                ps.setString(1, dk.getMaSinhVien());
                ps.setString(2, dk.getMaLop());
                if (ps.executeUpdate() == 1) {
                    return true;
                }
            }

            String sql = "INSERT INTO DangKyHoc (MaLop, MaSinhVien) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dk.getMaLop());
                ps.setString(2, dk.getMaSinhVien());
                return ps.executeUpdate() > 0;
            }
        }
    }

    public List<LopHocThoiKhoaBieu> layLichHocTheoTuan(String maSinhVien) throws SQLException {
        List<LopHocThoiKhoaBieu> lichHoc = new ArrayList<>();
        String sql = "SELECT lh.MaLop, mh.MaMon, mh.TenMon, lh.HocKy, lich.Thu, "
                + "TIME_FORMAT(lich.GioBatDau, '%H:%i') AS GioBatDau, "
                + "TIME_FORMAT(lich.GioKetThuc, '%H:%i') AS GioKetThuc, lich.PhongHoc "
                + "FROM DangKyHoc dk "
                + "JOIN LopHocPhan lh ON lh.MaLop = dk.MaLop "
                + "JOIN MonHoc mh ON mh.MaMon = lh.MaMon "
                + "JOIN LichHoc lich ON lich.MaLop = lh.MaLop "
                + "WHERE dk.MaSinhVien = ? AND dk.TrangThai <> 'HUY' "
                + "ORDER BY lich.Thu, lich.GioBatDau, lich.GioKetThuc, mh.TenMon";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lichHoc.add(new LopHocThoiKhoaBieu(
                            rs.getString("MaLop"),
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getString("HocKy"),
                            rs.getString("Thu"),
                            rs.getString("GioBatDau"),
                            rs.getString("GioKetThuc"),
                            rs.getString("PhongHoc")));
                }
            }
        }
        return lichHoc;
    }
}
