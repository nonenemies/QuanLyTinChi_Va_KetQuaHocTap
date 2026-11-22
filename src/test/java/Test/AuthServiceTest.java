package Test;

import Model.Users;
import Services.AuthService;
import org.junit.Test;
import static org.junit.Assert.*;

public class AuthServiceTest extends LopCoSoKiemThu {
    private final AuthService dichVu = new AuthService();

    @Test public void xacThucDangNhapKhiEmailVaMatKhauDung() {
        Users taiKhoan = dichVu.xacThucDangNhap(duLieu.emailSinhVien, "matkhau-thu");
        assertNotNull(taiKhoan);
        assertEquals(duLieu.maSinhVien, taiKhoan.getMaSo());
    }

    @Test public void tuChoiDangNhapKhiSaiMatKhau() {
        assertNull(dichVu.xacThucDangNhap(duLieu.emailSinhVien, "sai"));
    }

    @Test public void tuChoiDangNhapKhiEmailKhongTonTaiHoacTaiKhoanNgung() throws Exception {
        assertNull(dichVu.xacThucDangNhap(duLieu.tienTo + "-khongtontai@test.local", "matkhau-thu"));
        duLieu.capNhatTrangThaiNguoiDung(duLieu.emailSinhVien, "NGUNG_HOAT_DONG");
        assertNull(dichVu.xacThucDangNhap(duLieu.emailSinhVien, "matkhau-thu"));
    }

}
