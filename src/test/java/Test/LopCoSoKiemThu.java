package Test;

import org.junit.After;
import org.junit.Before;

abstract class LopCoSoKiemThu {
    protected DuLieuKiemThu duLieu;

    @Before public void taoDuLieuKiemThu() throws Exception {
        duLieu = new DuLieuKiemThu();
    }

    @After public void donDepDuLieuKiemThu() throws Exception {
        if (duLieu != null) duLieu.close();
    }
}
