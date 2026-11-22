package Test;

import DAO.SinhVienDAO;
import Model.SinhVien;
import org.junit.Test;
import static org.junit.Assert.*;

public class SinhVienDAOTest extends LopCoSoKiemThu {
    @Test public void timSinhVienTheoMaTonTai() throws Exception {
        SinhVien sinhVien = new SinhVienDAO().layTheoMaSinhVien(duLieu.maSinhVien);
        assertNotNull(sinhVien);
        assertEquals(duLieu.maSinhVien, sinhVien.getMaSinhVien());
        assertEquals("Nam", sinhVien.getGioiTinh());
    }

    @Test public void timSinhVienTheoMaKhongTonTai() throws Exception {
        assertNull(new SinhVienDAO().layTheoMaSinhVien(duLieu.tienTo + "KHONGTONTAI"));
    }

}
