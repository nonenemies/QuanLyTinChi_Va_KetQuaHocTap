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

/** Creates and removes only rows whose unique test prefix belongs to this fixture. */
final class DatabaseFixture implements AutoCloseable {
    final String tag = "JT" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    final String majorCode = tag + "N";
    final String studentCode = tag + "S";
    final String teacherCode = tag + "T";
    final String adminCode = tag + "A";
    final String courseCode = tag + "C";
    final String termCode = tag + "H";
    final String studentEmail = tag + "-student@test.local";
    final String teacherEmail = tag + "-teacher@test.local";
    final String adminEmail = tag + "-admin@test.local";
    final int studentUserId;
    final int teacherUserId;
    final int adminUserId;

    DatabaseFixture() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO Nganh (MaNganh, TenNganh) VALUES (?, ?)", majorCode, "JUnit ngành");
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO CauHinhHocKy (HocKy, KetThucHocKy, HanNhapDiem) VALUES (?, ?, ?)")) {
                ps.setString(1, termCode);
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusDays(30)));
                ps.executeUpdate();
            }
            adminUserId = addUser(connection, "JUnit admin", adminEmail, adminCode, "ADMIN");
            execute(connection, "INSERT INTO Admin (UserID, ChucVu) VALUES (?, ?)", adminUserId, "JUnit");
            teacherUserId = addUser(connection, "JUnit teacher", teacherEmail, teacherCode, "GIAOVIEN");
            execute(connection, "INSERT INTO GiangVien (UserID, MaGiangVien) VALUES (?, ?)", teacherUserId, teacherCode);
            studentUserId = addStudent(connection, studentCode, studentEmail, "JUnit student");
            execute(connection, "INSERT INTO DotDangKyNganh (MaNganh, MoDangKy, DongDangKy) "
                    + "VALUES (?, CURRENT_TIMESTAMP - INTERVAL 1 DAY, CURRENT_TIMESTAMP + INTERVAL 1 DAY)", majorCode);
            addCourse(connection, courseCode, 3);
            execute(connection, "INSERT INTO ChuongTrinhDaoTao (MaNganh, MaMon) VALUES (?, ?)", majorCode, courseCode);
        }
    }

    Admin admin() {
        return new Admin(adminUserId, "JUnit admin", adminEmail, "secret", "ADMIN", adminCode,
                null, "HOAT_DONG", "JUnit");
    }

    SinhVien student() {
        return new SinhVien(studentUserId, "JUnit student", studentEmail, "secret", "SINHVIEN", studentCode,
                new Nganh(majorCode, "JUnit ngành"), null, "HOAT_DONG", studentCode,
                null, "Nam", "JUnit", 2024, "Chính quy");
    }

    String addStudent(String suffix) throws SQLException {
        String code = tag + suffix;
        try (Connection connection = DBConnection.getConnection()) {
            addStudent(connection, code, tag + "-" + suffix.toLowerCase() + "@test.local", "JUnit " + suffix);
        }
        return code;
    }

    String addTeacher(String suffix) throws SQLException {
        String code = tag + suffix;
        try (Connection connection = DBConnection.getConnection()) {
            int id = addUser(connection, "JUnit " + suffix, tag + "-" + suffix.toLowerCase() + "@test.local",
                    code, "GIAOVIEN");
            execute(connection, "INSERT INTO GiangVien (UserID, MaGiangVien) VALUES (?, ?)", id, code);
        }
        return code;
    }

    void setUserStatus(String email, String status) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "UPDATE Users SET TrangThai = ? WHERE Email = ?", status, email);
        }
    }

    String stringQuery(String sql, Object... params) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    void addCourse(String code, int credits) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            addCourse(connection, code, credits);
        }
    }

    void addPrerequisite(String course, String prerequisite) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO MonTienQuyet (MaMon, MaMonTienQuyet) VALUES (?, ?)", course, prerequisite);
        }
    }

    void addCurriculumCourse(String course) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO ChuongTrinhDaoTao (MaNganh, MaMon) VALUES (?, ?)", majorCode, course);
        }
    }

    void addClass(String classCode, String course, int capacity, int current, String status) throws SQLException {
        addClass(classCode, course, teacherCode, capacity, current, status);
    }

    void addClass(String classCode, String course, String assignedTeacher,
                  int capacity, int current, String status) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO LopHocPhan "
                    + "(MaLop, MaMon, MaGiangVien, HocKy, SiSoToiDa, SiSoHienTai, TrangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)", classCode, course, assignedTeacher, termCode,
                    capacity, current, status);
        }
    }

    void addSchedule(String classCode, String day, String start, String end) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO LichHoc (MaLop, Thu, GioBatDau, GioKetThuc) VALUES (?, ?, ?, ?)",
                    classCode, day, start, end);
        }
    }

    int register(String student, String classCode, String status, BigDecimal finalGrade) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO DangKyHoc (MaLop, MaSinhVien, DiemTongKet, TrangThai) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, classCode);
            ps.setString(2, student);
            ps.setBigDecimal(3, finalGrade);
            ps.setString(4, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Test fixture registration ID was not generated.");
                return keys.getInt(1);
            }
        }
    }

    int addComponent(String course, String name, BigDecimal weight, int order) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO ThanhPhanDanhGia (MaMon, TenThanhPhan, TrongSo, ThuTu) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course);
            ps.setString(2, name);
            ps.setBigDecimal(3, weight);
            ps.setInt(4, order);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Test fixture component ID was not generated.");
                return keys.getInt(1);
            }
        }
    }

    void addScore(int registrationId, int componentId, BigDecimal score) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "INSERT INTO DiemThanhPhan (MaDangKy, MaThanhPhan, DiemDatDuoc) VALUES (?, ?, ?)",
                    registrationId, componentId, score);
        }
    }

    boolean booleanQuery(String sql, Object... params) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int addStudent(Connection connection, String code, String email, String name) throws SQLException {
        int id = addUser(connection, name, email, code, "SINHVIEN");
        execute(connection, "INSERT INTO SinhVien (UserID, MaSinhVien, MaNganh, GioiTinh, Lop, KhoaHoc, HeDaoTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)", id, code, majorCode, "Nam", "JUnit", 2024, "Chính quy");
        return id;
    }

    private int addUser(Connection connection, String name, String email, String code, String role) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Users (HoTen, Email, Password, VaiTro, MaSo, TrangThai) VALUES (?, ?, ?, ?, ?, 'HOAT_DONG')",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, "secret");
            ps.setString(4, role);
            ps.setString(5, code);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Test fixture user ID was not generated.");
                return keys.getInt(1);
            }
        }
    }

    private void addCourse(Connection connection, String code, int credits) throws SQLException {
        execute(connection, "INSERT INTO MonHoc (MaMon, TenMon, SoTinChi, LoaiMon, MoTa) VALUES (?, ?, ?, ?, ?)",
                code, "JUnit " + code, credits, "BATBUOC", "Test fixture");
    }

    private static void execute(Connection connection, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bind(ps, params);
            ps.executeUpdate();
        }
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
    }

    @Override public void close() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            execute(connection, "DELETE d FROM DiemThanhPhan d JOIN DangKyHoc dk ON dk.MaDangKy = d.MaDangKy "
                    + "WHERE dk.MaSinhVien LIKE ?", tag + "%");
            execute(connection, "DELETE FROM DangKyHoc WHERE MaSinhVien LIKE ?", tag + "%");
            execute(connection, "DELETE FROM LichHoc WHERE MaLop LIKE ?", tag + "%");
            execute(connection, "DELETE FROM LopHocPhan WHERE MaLop LIKE ?", tag + "%");
            execute(connection, "DELETE FROM ChuongTrinhDaoTao WHERE MaNganh LIKE ?", tag + "%");
            execute(connection, "DELETE FROM MonTienQuyet WHERE MaMon LIKE ? OR MaMonTienQuyet LIKE ?", tag + "%", tag + "%");
            execute(connection, "DELETE FROM ThanhPhanDanhGia WHERE MaMon LIKE ?", tag + "%");
            execute(connection, "DELETE FROM DotDangKyNganh WHERE MaNganh LIKE ?", tag + "%");
            execute(connection, "DELETE FROM SinhVien WHERE MaSinhVien LIKE ?", tag + "%");
            execute(connection, "DELETE ad FROM Admin ad JOIN Users u ON u.UserID = ad.UserID WHERE u.Email LIKE ?", tag + "%");
            execute(connection, "DELETE gv FROM GiangVien gv JOIN Users u ON u.UserID = gv.UserID WHERE u.Email LIKE ?", tag + "%");
            execute(connection, "DELETE FROM Users WHERE Email LIKE ?", tag + "%");
            execute(connection, "DELETE FROM MonHoc WHERE MaMon LIKE ?", tag + "%");
            execute(connection, "DELETE FROM CauHinhHocKy WHERE HocKy LIKE ?", tag + "%");
            execute(connection, "DELETE FROM Nganh WHERE MaNganh LIKE ?", tag + "%");
        }
    }
}
