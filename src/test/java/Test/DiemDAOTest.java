package Test;

import DAO.DiemDAO;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiemDAOTest extends LopCoSoKiemThu {
    @Test public void nhapDiemThanhPhanMoiTrongHan() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        int maDangKy = duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int thanhPhan = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Thành phần kiểm thử", new BigDecimal("1.00"), 1);
        assertTrue(new DiemDAO().nhapDiem(duLieu.maSinhVien, maLop, thanhPhan, new BigDecimal("7.50")));
        assertEquals("7.50", duLieu.layGiaTriChuoi(
                "SELECT DiemDatDuoc FROM DiemThanhPhan WHERE MaDangKy = ? AND MaThanhPhan = ?",
                maDangKy, thanhPhan));
    }

    @Test public void capNhatDiemThanhPhanDaNhapTrongHan() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        int maDangKy = duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int thanhPhan = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Thành phần kiểm thử", new BigDecimal("1.00"), 1);
        duLieu.themDiemThanhPhan(maDangKy, thanhPhan, new BigDecimal("5.00"));
        assertTrue(new DiemDAO().nhapDiem(duLieu.maSinhVien, maLop, thanhPhan, new BigDecimal("8.25")));
        assertEquals("8.25", duLieu.layGiaTriChuoi(
                "SELECT DiemDatDuoc FROM DiemThanhPhan WHERE MaDangKy = ? AND MaThanhPhan = ?",
                maDangKy, thanhPhan));
    }

    @Test public void tinhTongKetKhiDaNhapDuTatCaDiemVaDatTuBon() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        int maDangKy = duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int diemGiuaKy = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Giữa kỳ", new BigDecimal("0.30"), 1);
        int diemCuoiKy = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Cuối kỳ", new BigDecimal("0.70"), 2);
        duLieu.themDiemThanhPhan(maDangKy, diemGiuaKy, new BigDecimal("8.00"));
        duLieu.themDiemThanhPhan(maDangKy, diemCuoiKy, new BigDecimal("6.00"));
        assertEquals(1, new DiemDAO().capNhatDiemTongKet(duLieu.maSinhVien, maLop));
        assertEquals("6.60", duLieu.layGiaTriChuoi("SELECT DiemTongKet FROM DangKyHoc WHERE MaDangKy = ?", maDangKy));
        assertEquals("DA_HOAN_THANH", duLieu.layGiaTriChuoi("SELECT TrangThai FROM DangKyHoc WHERE MaDangKy = ?", maDangKy));
    }

    @Test public void khongTinhTongKetKhiThieuDiemThanhPhan() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        int maDangKy = duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int diemGiuaKy = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Giữa kỳ", new BigDecimal("0.30"), 1);
        duLieu.themThanhPhanDanhGia(duLieu.maMon, "Cuối kỳ", new BigDecimal("0.70"), 2);
        duLieu.themDiemThanhPhan(maDangKy, diemGiuaKy, new BigDecimal("8.00"));
        assertEquals(0, new DiemDAO().capNhatDiemTongKet(duLieu.maSinhVien, maLop));
        assertNull(duLieu.layGiaTriChuoi("SELECT DiemTongKet FROM DangKyHoc WHERE MaDangKy = ?", maDangKy));
    }

}
