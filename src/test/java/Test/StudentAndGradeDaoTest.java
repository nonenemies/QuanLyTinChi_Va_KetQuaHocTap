package Test;

import DAO.DiemDAO;
import DAO.SinhVienDAO;
import Model.SinhVien;
import java.math.BigDecimal;
import org.junit.Test;

import static org.junit.Assert.*;

public class StudentAndGradeDaoTest extends DatabaseTestBase {
    @Test public void timSinhVienTheoMaTonTai() throws Exception {
        SinhVien student = new SinhVienDAO().layTheoMaSinhVien(db.studentCode);
        assertNotNull(student);
        assertEquals(db.studentCode, student.getMaSinhVien());
        assertEquals("Nam", student.getGioiTinh());
    }

    @Test public void timSinhVienTheoMaKhongTonTai() throws Exception {
        assertNull(new SinhVienDAO().layTheoMaSinhVien(db.tag + "MISSING"));
    }

    @Test public void nhapDiemThanhPhanMoiTrongHan() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        int registration = db.register(db.studentCode, classCode, "DANG_HOC", null);
        int component = db.addComponent(db.courseCode, "Test component", new BigDecimal("1.00"), 1);
        assertTrue(new DiemDAO().nhapDiem(db.studentCode, classCode, component, new BigDecimal("7.50")));
        assertEquals("7.50", db.stringQuery(
                "SELECT DiemDatDuoc FROM DiemThanhPhan WHERE MaDangKy = ? AND MaThanhPhan = ?",
                registration, component));
    }

    @Test public void capNhatDiemThanhPhanDaNhapTrongHan() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        int registration = db.register(db.studentCode, classCode, "DANG_HOC", null);
        int component = db.addComponent(db.courseCode, "Test component", new BigDecimal("1.00"), 1);
        db.addScore(registration, component, new BigDecimal("5.00"));
        assertTrue(new DiemDAO().nhapDiem(db.studentCode, classCode, component, new BigDecimal("8.25")));
        assertEquals("8.25", db.stringQuery(
                "SELECT DiemDatDuoc FROM DiemThanhPhan WHERE MaDangKy = ? AND MaThanhPhan = ?",
                registration, component));
    }

    @Test public void tinhTongKetKhiDaNhapDuTatCaDiemVaDatTuBon() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        int registration = db.register(db.studentCode, classCode, "DANG_HOC", null);
        int midterm = db.addComponent(db.courseCode, "Midterm", new BigDecimal("0.30"), 1);
        int finalExam = db.addComponent(db.courseCode, "Final", new BigDecimal("0.70"), 2);
        db.addScore(registration, midterm, new BigDecimal("8.00"));
        db.addScore(registration, finalExam, new BigDecimal("6.00"));
        assertEquals(1, new DiemDAO().capNhatDiemTongKet(db.studentCode, classCode));
        assertEquals("6.60", db.stringQuery("SELECT DiemTongKet FROM DangKyHoc WHERE MaDangKy = ?", registration));
        assertEquals("DA_HOAN_THANH", db.stringQuery("SELECT TrangThai FROM DangKyHoc WHERE MaDangKy = ?", registration));
    }

    @Test public void khongTinhTongKetKhiThieuDiemThanhPhan() throws Exception {
        String classCode = db.tag + "L1";
        db.addClass(classCode, db.courseCode, 20, 1, "HOAT_DONG");
        int registration = db.register(db.studentCode, classCode, "DANG_HOC", null);
        int midterm = db.addComponent(db.courseCode, "Midterm", new BigDecimal("0.30"), 1);
        db.addComponent(db.courseCode, "Final", new BigDecimal("0.70"), 2);
        db.addScore(registration, midterm, new BigDecimal("8.00"));
        assertEquals(0, new DiemDAO().capNhatDiemTongKet(db.studentCode, classCode));
        assertNull(db.stringQuery("SELECT DiemTongKet FROM DangKyHoc WHERE MaDangKy = ?", registration));
    }
}
