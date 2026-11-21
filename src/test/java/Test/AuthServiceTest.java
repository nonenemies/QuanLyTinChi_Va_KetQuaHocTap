package Test;

import Model.Users;
import Services.AuthService;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthServiceTest extends DatabaseTestBase {
    private final AuthService service = new AuthService();

    @Test public void dangNhapDungEmailVaMatKhauTaiKhoanHoatDong() {
        Users user = service.xacThucDangNhap(db.studentEmail, "secret");
        assertNotNull(user);
        assertEquals(db.studentCode, user.getMaSo());
    }

    @Test public void dangNhapSaiMatKhau() {
        assertNull(service.xacThucDangNhap(db.studentEmail, "sai"));
    }

    @Test public void dangNhapEmailKhongTonTaiHoacTaiKhoanNgung() throws Exception {
        assertNull(service.xacThucDangNhap(db.tag + "-missing@test.local", "secret"));
        db.setUserStatus(db.studentEmail, "NGUNG_HOAT_DONG");
        assertNull(service.xacThucDangNhap(db.studentEmail, "secret"));
    }
}
