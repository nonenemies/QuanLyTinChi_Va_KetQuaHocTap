package Test;

import DAO.DangKyHocDAO;
import DAO.LopHocPhanDAO;
import java.math.BigDecimal;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegistrationDaoTest extends DatabaseTestBase {
    @Test public void phatHienTrungCungThuVaKhungGio() throws Exception {
        String oldClass = db.tag + "L1";
        String newClass = db.tag + "L2";
        db.addClass(oldClass, db.courseCode, 20, 1, "HOAT_DONG");
        db.addClass(newClass, db.courseCode, 20, 0, "HOAT_DONG");
        db.addSchedule(oldClass, "Thứ 2", "08:00:00", "10:00:00");
        db.addSchedule(newClass, "Thứ 2", "09:00:00", "11:00:00");
        db.register(db.studentCode, oldClass, "DANG_HOC", null);
        assertTrue(new DangKyHocDAO().coLopBiTrungLich(db.studentCode, newClass));
    }

    @Test public void khongTrungLichNeuKhacThuHoacKhungGio() throws Exception {
        String oldClass = db.tag + "L1";
        String differentDay = db.tag + "L2";
        String differentTime = db.tag + "L3";
        db.addClass(oldClass, db.courseCode, 20, 1, "HOAT_DONG");
        db.addClass(differentDay, db.courseCode, 20, 0, "HOAT_DONG");
        db.addClass(differentTime, db.courseCode, 20, 0, "HOAT_DONG");
        db.addSchedule(oldClass, "Thứ 2", "08:00:00", "10:00:00");
        db.addSchedule(differentDay, "Thứ 3", "08:00:00", "10:00:00");
        db.addSchedule(differentTime, "Thứ 2", "10:00:00", "12:00:00");
        db.register(db.studentCode, oldClass, "DANG_HOC", null);
        DangKyHocDAO dao = new DangKyHocDAO();
        assertFalse(dao.coLopBiTrungLich(db.studentCode, differentDay));
        assertFalse(dao.coLopBiTrungLich(db.studentCode, differentTime));
    }

    @Test public void sinhVienDaDatMonVoiDiemTuBon() throws Exception {
        String completedClass = db.tag + "L1";
        db.addClass(completedClass, db.courseCode, 20, 1, "NGUNG_HOAT_DONG");
        db.register(db.studentCode, completedClass, "DA_HOAN_THANH", new BigDecimal("4.00"));
        assertTrue(new DangKyHocDAO().kiemTraDaQuaMon(db.studentCode, db.courseCode));
    }

    @Test public void sinhVienChuaHocHoacDiemDuoiBonChuaDatMon() throws Exception {
        assertFalse(new DangKyHocDAO().kiemTraDaQuaMon(db.studentCode, db.courseCode));
        String failedClass = db.tag + "L1";
        db.addClass(failedClass, db.courseCode, 20, 1, "NGUNG_HOAT_DONG");
        db.register(db.studentCode, failedClass, "KHONG_DAT", new BigDecimal("3.99"));
        assertFalse(new DangKyHocDAO().kiemTraDaQuaMon(db.studentCode, db.courseCode));
    }

    @Test public void tangSiSoKhiLopHoatDongVaConCho() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 3, 2, "HOAT_DONG");
        assertTrue(new LopHocPhanDAO().tangSiSo(classCode));
        assertEquals("3", db.stringQuery("SELECT SiSoHienTai FROM LopHocPhan WHERE MaLop = ?", classCode));
    }

    @Test public void khongTangSiSoKhiLopDayHoacDaNgung() throws Exception {
        String fullClass = db.tag + "L1";
        String inactiveClass = db.tag + "L2";
        db.addClass(fullClass, db.courseCode, 1, 1, "HOAT_DONG");
        db.addClass(inactiveClass, db.courseCode, 5, 1, "NGUNG_HOAT_DONG");
        LopHocPhanDAO dao = new LopHocPhanDAO();
        assertFalse(dao.tangSiSo(fullClass));
        assertFalse(dao.tangSiSo(inactiveClass));
    }
}
