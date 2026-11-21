package Test;

import DAO.DiemDAO;
import DAO.DangKyHocDAO;
import Model.GiangVien;
import Model.LopHocPhan;
import Model.Nganh;
import Model.SinhVien;
import Model.ThanhPhanDanhGia;
import Services.AdminService;
import Services.DangKyHocService;
import Services.DiemService;
import Services.ServiceException;
import java.math.BigDecimal;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceIntegrationTest extends DatabaseTestBase {
    @Test public void dangKyHocThanhCongKhiThoaManDieuKien() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 0, "HOAT_DONG");
        db.addSchedule(classCode, "Thứ 2", "08:00:00", "10:00:00");
        assertEquals("THANH_CONG", new DangKyHocService().dangKyHoc(db.studentCode, classCode));
        assertTrue(db.booleanQuery("SELECT 1 FROM DangKyHoc WHERE MaSinhVien = ? AND MaLop = ? AND TrangThai = 'DANG_HOC'",
                db.studentCode, classCode));
    }

    @Test public void dangKyHocTuChoiLopBiTrungLich() throws Exception {
        String currentClass = db.tag + "L1";
        String requestedClass = db.tag + "L2";
        String otherCourse = db.tag + "OC";
        db.addCourse(otherCourse, 3);
        db.addClass(currentClass, otherCourse, 20, 1, "HOAT_DONG");
        db.addClass(requestedClass, db.courseCode, 20, 0, "HOAT_DONG");
        db.addSchedule(currentClass, "Thứ 2", "08:00:00", "10:00:00");
        db.addSchedule(requestedClass, "Thứ 2", "09:00:00", "11:00:00");
        db.register(db.studentCode, currentClass, "DANG_HOC", null);
        assertEquals("TRUNG_LICH", new DangKyHocService().dangKyHoc(db.studentCode, requestedClass));
    }

    @Test public void dangKyHocTuChoiNeuChuaDatMonTienQuyet() throws Exception {
        String prerequisite = db.tag + "P";
        String classCode = db.tag + "L1";
        db.addCourse(prerequisite, 3);
        db.addPrerequisite(db.courseCode, prerequisite);
        db.addClass(classCode, db.courseCode, 20, 0, "HOAT_DONG");
        db.addSchedule(classCode, "Thứ 2", "08:00:00", "10:00:00");
        assertEquals("THIEU_MON_TIEN_QUYET", new DangKyHocService().dangKyHoc(db.studentCode, classCode));
    }

    @Test public void dangKyHocTuChoiNeuVuotQuaHaiSauTinChi() throws Exception {
        String classCode = db.tag + "L0";
        db.addClass(classCode, db.courseCode, 30, 0, "HOAT_DONG");
        db.addSchedule(classCode, "Thứ 2", "08:00:00", "10:00:00");
        for (int i = 0; i < 8; i++) {
            String course = db.tag + "X" + i;
            String existingClass = db.tag + "E" + i;
            db.addCourse(course, 3);
            db.addClass(existingClass, course, 30, 1, "HOAT_DONG");
            db.register(db.studentCode, existingClass, "DANG_HOC", null);
        }
        assertEquals("VUOT_QUA_TIN_CHI", new DangKyHocService().dangKyHoc(db.studentCode, classCode));
    }

    @Test public void huyDangKyTrongDotKhiChuaCoDiem() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        db.register(db.studentCode, classCode, "DANG_HOC", null);
        assertTrue(new DangKyHocService().huyDangKyHoc(db.studentCode, classCode));
        assertEquals("HUY", db.stringQuery("SELECT TrangThai FROM DangKyHoc WHERE MaSinhVien = ? AND MaLop = ?",
                db.studentCode, classCode));
    }

    @Test public void khongHuyDangKyKhiDaCoDiemThanhPhan() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        int registration = db.register(db.studentCode, classCode, "DANG_HOC", null);
        int component = db.addComponent(db.courseCode, "Test component", new BigDecimal("1.00"), 1);
        db.addScore(registration, component, new BigDecimal("6.00"));
        assertFalse(new DangKyHocService().huyDangKyHoc(db.studentCode, classCode));
        assertEquals("DANG_HOC", db.stringQuery("SELECT TrangThai FROM DangKyHoc WHERE MaDangKy = ?", registration));
    }

    @Test public void giangVienPhuTrachLuuDiemHopLe() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        db.register(db.studentCode, classCode, "DANG_HOC", null);
        int componentId = db.addComponent(db.courseCode, "Test component", new BigDecimal("1.00"), 1);
        GiangVien teacher = teacher(db.teacherCode);
        ThanhPhanDanhGia component = new ThanhPhanDanhGia(componentId, db.courseCode, "Test component",
                new BigDecimal("1.00"), 1, true);
        assertTrue(new DiemService().nhapDiemVaTinhTongKet(teacher, db.studentCode, classCode,
                component, new BigDecimal("8.50")));
        assertEquals("DA_HOAN_THANH", db.stringQuery("SELECT TrangThai FROM DangKyHoc "
                + "WHERE MaSinhVien = ? AND MaLop = ?", db.studentCode, classCode));
    }

    @Test public void diemNhoHonKhongHoacLonHonMuoiBiTuChoi() {
        DiemService service = new DiemService();
        ThanhPhanDanhGia component = new ThanhPhanDanhGia(1, db.courseCode, "Test", new BigDecimal("1.00"), 1, true);
        assertFalse(service.nhapDiemVaTinhTongKet(teacher(db.teacherCode), db.studentCode,
                db.tag + "L1", component, new BigDecimal("-0.01")));
        assertFalse(service.nhapDiemVaTinhTongKet(teacher(db.teacherCode), db.studentCode,
                db.tag + "L1", component, new BigDecimal("10.01")));
    }

    @Test public void giangVienKhongPhuTrachKhongDuocNhapDiem() throws Exception {
        String otherTeacher = db.addTeacher("T2");
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, otherTeacher, 20, 1, "HOAT_DONG");
        db.register(db.studentCode, classCode, "DANG_HOC", null);
        ThanhPhanDanhGia component = new ThanhPhanDanhGia(1, db.courseCode, "Test", new BigDecimal("1.00"), 1, true);
        assertNotEquals(db.teacherCode, otherTeacher);
        try {
            new DiemService().nhapDiemVaTinhTongKet(teacher(db.teacherCode), db.studentCode,
                    classCode, component, new BigDecimal("8.00"));
            fail("Expected an exception for a class assigned to another teacher.");
        } catch (ServiceException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    @Test public void adminTaoTaiKhoanSinhVienKhiHoSoVaNganhHopLe() throws Exception {
        String code = db.tag + "NS";
        SinhVien student = new SinhVien(null, "New Student", db.tag + "-new@test.local", "secret",
                "SINHVIEN", code, new Nganh(db.majorCode, "JUnit ngành"), null,
                "HOAT_DONG", code, null, "Nữ", "JUnit", 2024, "Chính quy");
        assertTrue(new AdminService().taoTaiKhoan(db.admin(), student));
        assertTrue(db.booleanQuery("SELECT 1 FROM SinhVien WHERE MaSinhVien = ?", code));
    }

    @Test public void adminKhongTaoTaiKhoanVoiMaNganhKhongTonTai() throws Exception {
        String code = db.tag + "NS";
        SinhVien student = new SinhVien(null, "New Student", db.tag + "-new@test.local", "secret",
                "SINHVIEN", code, new Nganh(db.tag + "NO", "Missing"), null,
                "HOAT_DONG", code, null, "Nữ", "JUnit", 2024, "Chính quy");
        try {
            new AdminService().taoTaiKhoan(db.admin(), student);
            fail("Expected the unknown major to be rejected.");
        } catch (ServiceException expected) {
            assertFalse(db.booleanQuery("SELECT 1 FROM Users WHERE Email = ?", student.getEmail()));
        }
    }

    @Test public void adminNgungTaiKhoanSinhVienDangHoatDong() throws Exception {
        assertTrue(new AdminService().ngungTaiKhoan(db.admin(), db.studentUserId));
        assertEquals("NGUNG_HOAT_DONG", db.stringQuery(
                "SELECT TrangThai FROM Users WHERE UserID = ?", db.studentUserId));
    }

    @Test public void adminKhongTheTuNgungTaiKhoanMinh() {
        try {
            new AdminService().ngungTaiKhoan(db.admin(), db.adminUserId);
            fail("Expected an admin self-suspension to be rejected.");
        } catch (ServiceException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private GiangVien teacher(String code) {
        int userId = code.equals(db.teacherCode) ? db.teacherUserId : -1;
        return new GiangVien(userId, "JUnit teacher", db.teacherEmail, "secret", "GIAOVIEN", code,
                null, "HOAT_DONG", code, null, null);
    }
}
