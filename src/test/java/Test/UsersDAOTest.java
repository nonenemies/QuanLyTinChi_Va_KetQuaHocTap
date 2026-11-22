package Test;

import DAO.UsersDAO;
import Model.Users;
import org.junit.Test;
import static org.junit.Assert.*;

public class UsersDAOTest extends LopCoSoKiemThu {
    @Test public void emailTonTaiTaiKhoanDangHoatDong() throws Exception {
        Users taiKhoan = new UsersDAO().layTheoEmailDangHoatDong(duLieu.emailSinhVien);
        assertNotNull(taiKhoan);
        assertEquals(duLieu.maSinhVien, taiKhoan.getMaSo());
        assertEquals("HOAT_DONG", taiKhoan.getTrangThai());
    }

    @Test public void emailKhongTonTai() throws Exception {
        assertNull(new UsersDAO().layTheoEmailDangHoatDong(duLieu.tienTo + "-khongtontai@test.local"));
    }

    @Test public void emailTonTaiNhungTaiKhoanDaNgung() throws Exception {
        duLieu.capNhatTrangThaiNguoiDung(duLieu.emailSinhVien, "NGUNG_HOAT_DONG");
        assertNull(new UsersDAO().layTheoEmailDangHoatDong(duLieu.emailSinhVien));
    }

}
