package Test;

import Model.GiangVien;
import Model.ThanhPhanDanhGia;
import Services.DiemService;
import Services.ServiceException;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiemServiceTest extends LopCoSoKiemThu {
    @Test public void giangVienPhuTrachLuuDiemHopLe() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 20, 1, "HOAT_DONG");
        duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        int maThanhPhan = duLieu.themThanhPhanDanhGia(duLieu.maMon, "Thành phần kiểm thử", new BigDecimal("1.00"), 1);
        GiangVien giangVien = giangVien(duLieu.maGiangVien);
        ThanhPhanDanhGia thanhPhan = new ThanhPhanDanhGia(maThanhPhan, duLieu.maMon, "Thành phần kiểm thử",
                new BigDecimal("1.00"), 1, true);
        assertTrue(new DiemService().nhapDiemVaTinhTongKet(giangVien, duLieu.maSinhVien, maLop,
                thanhPhan, new BigDecimal("8.50")));
        assertEquals("DA_HOAN_THANH", duLieu.layGiaTriChuoi("SELECT TrangThai FROM DangKyHoc "
                + "WHERE MaSinhVien = ? AND MaLop = ?", duLieu.maSinhVien, maLop));
    }

    @Test public void diemNhoHonKhongHoacLonHonMuoiBiTuChoi() {
        DiemService dichVu = new DiemService();
        ThanhPhanDanhGia thanhPhan = new ThanhPhanDanhGia(1, duLieu.maMon, "Kiểm thử", new BigDecimal("1.00"), 1, true);
        assertFalse(dichVu.nhapDiemVaTinhTongKet(giangVien(duLieu.maGiangVien), duLieu.maSinhVien,
                duLieu.tienTo + "L1", thanhPhan, new BigDecimal("-0.01")));
        assertFalse(dichVu.nhapDiemVaTinhTongKet(giangVien(duLieu.maGiangVien), duLieu.maSinhVien,
                duLieu.tienTo + "L1", thanhPhan, new BigDecimal("10.01")));
    }

    @Test public void giangVienKhongPhuTrachKhongDuocNhapDiem() throws Exception {
        String giangVienKhac = duLieu.themGiangVien("T2");
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, giangVienKhac, 20, 1, "HOAT_DONG");
        duLieu.themDangKyHoc(duLieu.maSinhVien, maLop, "DANG_HOC", null);
        ThanhPhanDanhGia thanhPhan = new ThanhPhanDanhGia(1, duLieu.maMon, "Kiểm thử", new BigDecimal("1.00"), 1, true);
        assertNotEquals(duLieu.maGiangVien, giangVienKhac);
        try {
            new DiemService().nhapDiemVaTinhTongKet(giangVien(duLieu.maGiangVien), duLieu.maSinhVien,
                    maLop, thanhPhan, new BigDecimal("8.00"));
            fail("Cần báo lỗi khi giảng viên khác được phân công lớp này.");
        } catch (ServiceException loiDuKien) {
            assertNotNull(loiDuKien.getMessage());
        }
    }

    private GiangVien giangVien(String maSo) {
        int idNguoiDung = maSo.equals(duLieu.maGiangVien) ? duLieu.idNguoiDungGiangVien : -1;
        return new GiangVien(idNguoiDung, "Giảng viên kiểm thử", duLieu.emailGiangVien, "matkhau-thu", "GIAOVIEN", maSo,
                null, "HOAT_DONG", maSo, null, null);
    }

}
