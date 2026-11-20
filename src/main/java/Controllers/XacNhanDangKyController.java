package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class XacNhanDangKyController {
    @FXML private Label lblTieuDe;
    @FXML private Label lblChiTiet;
    @FXML private Button nutXacNhan;

    private boolean daXacNhan;

    public void datChiTiet(String tieuDe, String chiTiet) {
        lblTieuDe.setText(tieuDe);
        lblChiTiet.setText(chiTiet);
    }

    public boolean daXacNhan() {
        return daXacNhan;
    }

    @FXML
    private void xacNhan() {
        daXacNhan = true;
        dongHopThoai();
    }

    @FXML
    private void huyBo() {
        dongHopThoai();
    }

    private void dongHopThoai() {
        nutXacNhan.getScene().getWindow().hide();
    }
}
