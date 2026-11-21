package Test;

import DAO.UsersDAO;
import Model.Users;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsersDAOTest extends DatabaseTestBase {
    @Test public void emailTonTaiTaiKhoanDangHoatDong() throws Exception {
        Users user = new UsersDAO().layTheoEmailDangHoatDong(db.studentEmail);
        assertNotNull(user);
        assertEquals(db.studentCode, user.getMaSo());
        assertEquals("HOAT_DONG", user.getTrangThai());
    }

    @Test public void emailKhongTonTai() throws Exception {
        assertNull(new UsersDAO().layTheoEmailDangHoatDong(db.tag + "-missing@test.local"));
    }

    @Test public void emailTonTaiNhungTaiKhoanDaNgung() throws Exception {
        db.setUserStatus(db.studentEmail, "NGUNG_HOAT_DONG");
        assertNull(new UsersDAO().layTheoEmailDangHoatDong(db.studentEmail));
    }
}
