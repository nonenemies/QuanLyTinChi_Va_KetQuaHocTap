package Test;

import DAO.LopHocPhanDAO;
import org.junit.Test;
import static org.junit.Assert.*;

public class LopHocPhanDAOTest extends LopCoSoKiemThu {
    @Test public void tangSiSoKhiLopHoatDongVaConCho() throws Exception {
        String maLop = duLieu.tienTo + "L1";
        duLieu.themLopHocPhan(maLop, duLieu.maMon, 3, 2, "HOAT_DONG");
        assertTrue(new LopHocPhanDAO().tangSiSo(maLop));
        assertEquals("3", duLieu.layGiaTriChuoi("SELECT SiSoHienTai FROM LopHocPhan WHERE MaLop = ?", maLop));
    }

    @Test public void khongTangSiSoKhiLopDayHoacDaNgung() throws Exception {
        String lopDay = duLieu.tienTo + "L1";
        String lopNgung = duLieu.tienTo + "L2";
        duLieu.themLopHocPhan(lopDay, duLieu.maMon, 1, 1, "HOAT_DONG");
        duLieu.themLopHocPhan(lopNgung, duLieu.maMon, 5, 1, "NGUNG_HOAT_DONG");
        LopHocPhanDAO dao = new LopHocPhanDAO();
        assertFalse(dao.tangSiSo(lopDay));
        assertFalse(dao.tangSiSo(lopNgung));
    }

}
