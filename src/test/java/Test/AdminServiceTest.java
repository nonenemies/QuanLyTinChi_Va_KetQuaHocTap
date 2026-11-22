package Test;

import Model.Nganh;
import Model.SinhVien;
import Services.AdminService;
import Services.ServiceException;
import org.junit.Test;
import static org.junit.Assert.*;

public class AdminServiceTest extends LopCoSoKiemThu {
    @Test public void taoTaiKhoanSinhVienKhiHoSoVaNganhHopLe() throws Exception {
        String maSo = duLieu.tienTo + "NS";
        SinhVien sinhVien = new SinhVien(null, "Sinh viên mới", duLieu.tienTo + "-moi@test.local", "matkhau-thu",
                "SINHVIEN", maSo, new Nganh(duLieu.maNganh, "Ngành kiểm thử"), null,
                "HOAT_DONG", maSo, null, "Nữ", "Kiểm thử", 2024, "Chính quy");
        assertTrue(new AdminService().taoTaiKhoan(duLieu.taoQuanTriVien(), sinhVien));
        assertTrue(duLieu.coDongKetQua("SELECT 1 FROM SinhVien WHERE MaSinhVien = ?", maSo));
    }

    @Test public void tuChoiTaoTaiKhoanKhiMaNganhKhongTonTai() throws Exception {
        String maSo = duLieu.tienTo + "NS";
        SinhVien sinhVien = new SinhVien(null, "Sinh viên mới", duLieu.tienTo + "-moi@test.local", "matkhau-thu",
                "SINHVIEN", maSo, new Nganh(duLieu.tienTo + "KHONGCO", "Không tồn tại"), null,
                "HOAT_DONG", maSo, null, "Nữ", "Kiểm thử", 2024, "Chính quy");
        try {
            new AdminService().taoTaiKhoan(duLieu.taoQuanTriVien(), sinhVien);
            fail("Cần từ chối mã ngành không tồn tại.");
        } catch (ServiceException loiDuKien) {
            assertFalse(duLieu.coDongKetQua("SELECT 1 FROM Users WHERE Email = ?", sinhVien.getEmail()));
        }
    }

    @Test public void ngungTaiKhoanSinhVienDangHoatDong() throws Exception {
        assertTrue(new AdminService().ngungTaiKhoan(duLieu.taoQuanTriVien(), duLieu.idNguoiDungSinhVien));
        assertEquals("NGUNG_HOAT_DONG", duLieu.layGiaTriChuoi(
                "SELECT TrangThai FROM Users WHERE UserID = ?", duLieu.idNguoiDungSinhVien));
    }

    @Test public void tuChoiQuanTriVienTuNgungTaiKhoanMinh() {
        try {
            new AdminService().ngungTaiKhoan(duLieu.taoQuanTriVien(), duLieu.idNguoiDungQuanTriVien);
            fail("Cần từ chối quản trị viên tự ngừng tài khoản.");
        } catch (ServiceException loiDuKien) {
            assertNotNull(loiDuKien.getMessage());
        }
    }

}
