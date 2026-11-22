package Test;

import Services.DangKyHocService;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class DangKyHocServiceTest extends LopCoSoKiemThu {
    @Test public void dangKyHocThanhCongKhiThoaManDieuKien() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLichHoc(maLop, "Thứ 2", "08:00:00", "10:00:00");
        assertEquals("THANH_CONG", new DangKyHocService().dangKyHoc(duLieu.maSinhVien, maLop));
        assertTrue(duLieu.coDongKetQua("SELECT 1 FROM DangKyHoc WHERE MaSinhVien = ? AND MaLop = ? AND TrangThai = 'DANG_HOC'",
                duLieu.maSinhVien, maLop));
    }

    @Test public void dangKyHocTuChoiLopBiTrungLich() throws Exception {
        String lopDangHoc = duLieu.tienTo + "L1";
        String lopDuocChon = duLieu.tienTo + "L2";
        String monKhac = duLieu.tienTo + "OC";
        duLieu.themMonHoc(monKhac, 3);
        duLieu.themLopHocPhan(lopDangHoc, monKhac, 20, 1, "HOAT_DONG");
        duLieu.themLopHocPhan(lopDuocChon, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLichHoc(lopDangHoc, "Thứ 2", "08:00:00", "10:00:00");
        duLieu.themLichHoc(lopDuocChon, "Thứ 2", "09:00:00", "11:00:00");
        duLieu.themDangKyHoc(duLieu.maSinhVien, lopDangHoc, "DANG_HOC", null);
        assertEquals("TRUNG_LICH", new DangKyHocService().dangKyHoc(duLieu.maSinhVien, lopDuocChon));
    }

    @Test public void dangKyHocTuChoiNeuChuaDatMonTienQuyet() throws Exception {
        String monTienQuyet = duLieu.tienTo + "P";
        String maLop = duLieu.tienTo + "L1";
        duLieu.themMonHoc(monTienQuyet, 3);
        duLieu.themMonTienQuyet(duLieu.maMon, monTienQuyet);
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 0, "HOAT_DONG");
        duLieu.themLichHoc(maLop, "Thứ 2", "08:00:00", "10:00:00");
        assertEquals("THIEU_MON_TIEN_QUYET", new DangKyHocService().dangKyHoc(duLieu.maSinhVien, maLop));
    }

    @Test public void tuChoiDangKyKhiVuotQua26TinChi() throws Exception {
        String maLop = duLieu.tienTo + "L0";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 30, 0, "HOAT_DONG");
        duLieu.themLichHoc(maLop, "Thứ 2", "08:00:00", "10:00:00");
        for (int chiSo = 0; chiSo < 8; chiSo++) {
            String monHoc = duLieu.tienTo + "X" + chiSo;
            String lopDaDangKy = duLieu.tienTo + "E" + chiSo;
            duLieu.themMonHoc(monHoc, 3);
            duLieu.themLopHocPhan(lopDaDangKy, monHoc, 30, 1, "HOAT_DONG");
            duLieu.themDangKyHoc(duLieu.maSinhVien, lopDaDangKy, "DANG_HOC", null);
        }
        assertEquals("VUOT_QUA_TIN_CHI", new DangKyHocService().dangKyHoc(duLieu.maSinhVien, maLop));
    }

    @Test public void huyDangKyTrongDotKhiChuaCoDiem() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        assertTrue(new DangKyHocService().huyDangKyHoc(duLieu.maSinhVien, maLop));
        assertEquals("HUY", duLieu.layGiaTriChuoi("SELECT TrangThai FROM DangKyHoc WHERE MaSinhVien = ? AND MaLop = ?",
                duLieu.maSinhVien, maLop));
    }

    @Test public void khongHuyDangKyKhiDaCoDiemThanhPhan() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        int maDangKy = duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int thanhPhan = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Thành phần kiểm thử", new BigDecimal("1.00"), 1);
        duLieu.themDiemThanhPhan(maDangKy, thanhPhan, new BigDecimal("6.00"));
        assertFalse(new DangKyHocService().huyDangKyHoc(duLieu.maSinhVien, maLop));
        assertEquals("DANG_HOC", duLieu.layGiaTriChuoi("SELECT TrangThai FROM DangKyHoc WHERE MaDangKy = ?", maDangKy));
    }

}
