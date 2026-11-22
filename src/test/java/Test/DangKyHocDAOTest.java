package Test;

import DAO.DangKyHocDAO;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class DangKyHocDAOTest extends LopCoSoKiemThu {
    @Test public void phatHienTrungCungThuVaKhungGio() throws Exception {
        String lopCu = duLieu.tienTo + "L1";
        String lopMoi = duLieu.tienTo + "L2";
        duLieu.themLopHocPhan(lopCu, duLieu.maMon, 20, 1, "HOAT_DONG");
        duLieu.themLopHocPhan(lopMoi, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLichHoc(lopCu, "Thứ 2", "08:00:00", "10:00:00");
        duLieu.themLichHoc(lopMoi, "Thứ 2", "09:00:00", "11:00:00");
        duLieu.themDangKyHoc(duLieu.maSinhVien, lopCu, "DANG_HOC", null);
        assertTrue(new DangKyHocDAO().coLopBiTrungLich(duLieu.maSinhVien, lopMoi));
    }

    @Test public void khongTrungLichNeuKhacThuHoacKhungGio() throws Exception {
        String lopCu = duLieu.tienTo + "L1";
        String lopKhacThu = duLieu.tienTo + "L2";
        String lopKhacGio = duLieu.tienTo + "L3";
        duLieu.themLopHocPhan(lopCu, duLieu.maMon, 20, 1, "HOAT_DONG");
        duLieu.themLopHocPhan(lopKhacThu, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLopHocPhan(lopKhacGio, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLichHoc(lopCu, "Thứ 2", "08:00:00", "10:00:00");
        duLieu.themLichHoc(lopKhacThu, "Thứ 3", "08:00:00", "10:00:00");
        duLieu.themLichHoc(lopKhacGio, "Thứ 2", "10:00:00", "12:00:00");
        duLieu.themDangKyHoc(duLieu.maSinhVien, lopCu, "DANG_HOC", null);
        DangKyHocDAO dao = new DangKyHocDAO();
        assertFalse(dao.coLopBiTrungLich(duLieu.maSinhVien, lopKhacThu));
        assertFalse(dao.coLopBiTrungLich(duLieu.maSinhVien, lopKhacGio));
    }

    @Test public void sinhVienDaDatMonVoiDiemTuBon() throws Exception {
        String lopDaHoanThanh = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(lopDaHoanThanh, duLieu.maMon, 20, 1, "NGUNG_HOAT_DONG");
        duLieu.themDangKyHoc(duLieu.maSinhVien, lopDaHoanThanh, "DA_HOAN_THANH", new BigDecimal("4.00"));
        assertTrue(new DangKyHocDAO().kiemTraDaQuaMon(duLieu.maSinhVien, duLieu.maMon));
    }

    @Test public void sinhVienChuaHocHoacDiemDuoiBonChuaDatMon() throws Exception {
        assertFalse(new DangKyHocDAO().kiemTraDaQuaMon(duLieu.maSinhVien, duLieu.maMon));
        String lopKhongDat = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(lopKhongDat, duLieu.maMon, 20, 1, "NGUNG_HOAT_DONG");
        duLieu.themDangKyHoc(duLieu.maSinhVien, lopKhongDat, "KHONG_DAT", new BigDecimal("3.99"));
        assertFalse(new DangKyHocDAO().kiemTraDaQuaMon(duLieu.maSinhVien, duLieu.maMon));
    }

}
