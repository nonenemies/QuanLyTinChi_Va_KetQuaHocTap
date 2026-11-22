package Test;

import Model.Admin;
import Model.Nganh;
import Model.SinhVien;
import Util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Tạo và xóa những dòng dữ liệu mang tiền tố kiểm thử riêng. */
final class DuLieuKiemThu implements AutoCloseable {
    final String tienTo = "JT" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    final String maNganh = tienTo + "N";
    final String maSinhVien = tienTo + "S";
    final String maGiangVien = tienTo + "T";
    final String maQuanTriVien = tienTo + "A";
    final String maMon = tienTo + "C";
    final String hocKy = tienTo + "H";
    final String emailSinhVien = tienTo + "-sinhvien@test.local";
    final String emailGiangVien = tienTo + "-giangvien@test.local";
    final String emailQuanTriVien = tienTo + "-quantrivien@test.local";
    final int idNguoiDungSinhVien;
    final int idNguoiDungGiangVien;
    final int idNguoiDungQuanTriVien;

    DuLieuKiemThu() throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO Nganh (MaNganh, TenNganh) VALUES (?, ?)", maNganh, "Ngành kiểm thử");
            try (PreparedStatement lenh = ketNoi.prepareStatement(
                    "INSERT INTO CauHinhHocKy (HocKy, KetThucHocKy, HanNhapDiem) VALUES (?, ?, ?)")) {
                lenh.setString(1, hocKy);
                lenh.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
                lenh.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusDays(30)));
                lenh.executeUpdate();
            }
            idNguoiDungQuanTriVien = themNguoiDung(ketNoi, "Quản trị viên kiểm thử", emailQuanTriVien, maQuanTriVien, "ADMIN");
            thucThi(ketNoi, "INSERT INTO Admin (UserID, ChucVu) VALUES (?, ?)", idNguoiDungQuanTriVien, "Kiểm thử");
            idNguoiDungGiangVien = themNguoiDung(ketNoi, "Giảng viên kiểm thử", emailGiangVien, maGiangVien, "GIAOVIEN");
            thucThi(ketNoi, "INSERT INTO GiangVien (UserID, MaGiangVien) VALUES (?, ?)", idNguoiDungGiangVien, maGiangVien);
            idNguoiDungSinhVien = themSinhVien(ketNoi, maSinhVien, emailSinhVien, "Sinh viên kiểm thử");
            thucThi(ketNoi, "INSERT INTO DotDangKyNganh (MaNganh, MoDangKy, DongDangKy) "
                    + "VALUES (?, CURRENT_TIMESTAMP - INTERVAL 1 DAY, CURRENT_TIMESTAMP + INTERVAL 1 DAY)", maNganh);
            themMonHoc(ketNoi, maMon, 3);
            thucThi(ketNoi, "INSERT INTO ChuongTrinhDaoTao (MaNganh, MaMon) VALUES (?, ?)", maNganh, maMon);
        }
    }

    Admin taoQuanTriVien() {
        return new Admin(idNguoiDungQuanTriVien, "Quản trị viên kiểm thử", emailQuanTriVien, "matkhau-thu", "ADMIN", maQuanTriVien,
                null, "HOAT_DONG", "Kiểm thử");
    }

    SinhVien sinhVien() {
        return new SinhVien(idNguoiDungSinhVien, "Sinh viên kiểm thử", emailSinhVien, "matkhau-thu", "SINHVIEN", maSinhVien,
                new Nganh(maNganh, "Ngành kiểm thử"), null, "HOAT_DONG", maSinhVien,
                null, "Nam", "Kiểm thử", 2024, "Chính quy");
    }

    String themSinhVien(String hauTo) throws SQLException {
        String maSo = tienTo + hauTo;
        try (Connection ketNoi = DBConnection.getConnection()) {
            themSinhVien(ketNoi, maSo, tienTo + "-" + hauTo.toLowerCase() + "@test.local", "Kiểm thử " + hauTo);
        }
        return maSo;
    }

    String themGiangVien(String hauTo) throws SQLException {
        String maSo = tienTo + hauTo;
        try (Connection ketNoi = DBConnection.getConnection()) {
            int maNguoiDung = themNguoiDung(ketNoi, "Kiểm thử " + hauTo, tienTo + "-" + hauTo.toLowerCase() + "@test.local",
                    maSo, "GIAOVIEN");
            thucThi(ketNoi, "INSERT INTO GiangVien (UserID, MaGiangVien) VALUES (?, ?)", maNguoiDung, maSo);
        }
        return maSo;
    }

    void capNhatTrangThaiNguoiDung(String email, String trangThai) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "UPDATE Users SET TrangThai = ? WHERE Email = ?", trangThai, email);
        }
    }

    String layGiaTriChuoi(String cauLenhSql, Object... thamSo) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(cauLenhSql)) {
            ganThamSo(lenh, thamSo);
            try (ResultSet ketQua = lenh.executeQuery()) {
                return ketQua.next() ? ketQua.getString(1) : null;
            }
        }
    }

    void themMonHoc(String maSo, int soTinChi) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            themMonHoc(ketNoi, maSo, soTinChi);
        }
    }

    void themMonTienQuyet(String monHoc, String monTienQuyet) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO MonTienQuyet (MaMon, MaMonTienQuyet) VALUES (?, ?)", monHoc, monTienQuyet);
        }
    }

    void themMonVaoChuongTrinh(String monHoc) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO ChuongTrinhDaoTao (MaNganh, MaMon) VALUES (?, ?)", maNganh, monHoc);
        }
    }

    void themLopHocPhan(String maLop, String monHoc, int siSoToiDa, int siSoHienTai, String trangThai) throws SQLException {
        themLopHocPhan(maLop, monHoc, maGiangVien, siSoToiDa, siSoHienTai, trangThai);
    }

    void themLopHocPhan(String maLop, String monHoc, String giangVienPhuTrach,
                  int siSoToiDa, int siSoHienTai, String trangThai) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO LopHocPhan "
                    + "(MaLop, MaMon, MaGiangVien, HocKy, SiSoToiDa, SiSoHienTai, TrangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)", maLop, monHoc, giangVienPhuTrach, hocKy,
                    siSoToiDa, siSoHienTai, trangThai);
        }
    }

    void themLichHoc(String maLop, String thu, String gioBatDau, String gioKetThuc) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO LichHoc (MaLop, Thu, GioBatDau, GioKetThuc) VALUES (?, ?, ?, ?)",
                    maLop, thu, gioBatDau, gioKetThuc);
        }
    }

    int themDangKyHoc(String sinhVien, String maLop, String trangThai, BigDecimal diemTongKet) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(
                     "INSERT INTO DangKyHoc (MaLop, MaSinhVien, DiemTongKet, TrangThai) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            lenh.setString(1, maLop);
            lenh.setString(2, sinhVien);
            lenh.setBigDecimal(3, diemTongKet);
            lenh.setString(4, trangThai);
            lenh.executeUpdate();
            try (ResultSet khoaPhatSinh = lenh.getGeneratedKeys()) {
                if (!khoaPhatSinh.next()) throw new SQLException("Không tạo được mã đăng ký kiểm thử.");
                return khoaPhatSinh.getInt(1);
            }
        }
    }

    int themThanhPhanDanhGia(String monHoc, String ten, BigDecimal trongSo, int thuTu) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(
                     "INSERT INTO ThanhPhanDanhGia (MaMon, TenThanhPhan, TrongSo, ThuTu) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            lenh.setString(1, monHoc);
            lenh.setString(2, ten);
            lenh.setBigDecimal(3, trongSo);
            lenh.setInt(4, thuTu);
            lenh.executeUpdate();
            try (ResultSet khoaPhatSinh = lenh.getGeneratedKeys()) {
                if (!khoaPhatSinh.next()) throw new SQLException("Không tạo được mã thành phần kiểm thử.");
                return khoaPhatSinh.getInt(1);
            }
        }
    }

    void themDiemThanhPhan(int maDangKy, int maThanhPhan, BigDecimal diem) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "INSERT INTO DiemThanhPhan (MaDangKy, MaThanhPhan, DiemDatDuoc) VALUES (?, ?, ?)",
                    maDangKy, maThanhPhan, diem);
        }
    }

    boolean coDongKetQua(String cauLenhSql, Object... thamSo) throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(cauLenhSql)) {
            ganThamSo(lenh, thamSo);
            try (ResultSet ketQua = lenh.executeQuery()) {
                return ketQua.next();
            }
        }
    }

    private int themSinhVien(Connection ketNoi, String maSo, String email, String ten) throws SQLException {
        int maNguoiDung = themNguoiDung(ketNoi, ten, email, maSo, "SINHVIEN");
        thucThi(ketNoi, "INSERT INTO SinhVien (UserID, MaSinhVien, MaNganh, GioiTinh, Lop, KhoaHoc, HeDaoTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)", maNguoiDung, maSo, maNganh, "Nam", "Kiểm thử", 2024, "Chính quy");
        return maNguoiDung;
    }

    private int themNguoiDung(Connection ketNoi, String ten, String email, String maSo, String vaiTro) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "INSERT INTO Users (HoTen, Email, Password, VaiTro, MaSo, TrangThai) VALUES (?, ?, ?, ?, ?, 'HOAT_DONG')",
                Statement.RETURN_GENERATED_KEYS)) {
            lenh.setString(1, ten);
            lenh.setString(2, email);
            lenh.setString(3, "matkhau-thu");
            lenh.setString(4, vaiTro);
            lenh.setString(5, maSo);
            lenh.executeUpdate();
            try (ResultSet khoaPhatSinh = lenh.getGeneratedKeys()) {
                if (!khoaPhatSinh.next()) throw new SQLException("Không tạo được mã người dùng kiểm thử.");
                return khoaPhatSinh.getInt(1);
            }
        }
    }

    private void themMonHoc(Connection ketNoi, String maSo, int soTinChi) throws SQLException {
        thucThi(ketNoi, "INSERT INTO MonHoc (MaMon, TenMon, SoTinChi, LoaiMon, MoTa) VALUES (?, ?, ?, ?, ?)",
                maSo, "Kiểm thử " + maSo, soTinChi, "BATBUOC", "Dữ liệu kiểm thử");
    }

    private static void thucThi(Connection ketNoi, String cauLenhSql, Object... thamSo) throws SQLException {
        try (PreparedStatement lenh = ketNoi.prepareStatement(cauLenhSql)) {
            ganThamSo(lenh, thamSo);
            lenh.executeUpdate();
        }
    }

    private static void ganThamSo(PreparedStatement lenh, Object... thamSo) throws SQLException {
        for (int chiSo = 0; chiSo < thamSo.length; chiSo++) lenh.setObject(chiSo + 1, thamSo[chiSo]);
    }

    @Override public void close() throws SQLException {
        try (Connection ketNoi = DBConnection.getConnection()) {
            thucThi(ketNoi, "DELETE d FROM DiemThanhPhan d JOIN DangKyHoc dk ON dk.MaDangKy = d.MaDangKy "
                    + "WHERE dk.MaSinhVien LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM DangKyHoc WHERE MaSinhVien LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM LichHoc WHERE MaLop LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM LopHocPhan WHERE MaLop LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM ChuongTrinhDaoTao WHERE MaNganh LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM MonTienQuyet WHERE MaMon LIKE ? OR MaMonTienQuyet LIKE ?", tienTo + "%", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM ThanhPhanDanhGia WHERE MaMon LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM DotDangKyNganh WHERE MaNganh LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM SinhVien WHERE MaSinhVien LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE ad FROM Admin ad JOIN Users u ON u.UserID = ad.UserID WHERE u.Email LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE gv FROM GiangVien gv JOIN Users u ON u.UserID = gv.UserID WHERE u.Email LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM Users WHERE Email LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM MonHoc WHERE MaMon LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM CauHinhHocKy WHERE HocKy LIKE ?", tienTo + "%");
            thucThi(ketNoi, "DELETE FROM Nganh WHERE MaNganh LIKE ?", tienTo + "%");
        }
    }
}
