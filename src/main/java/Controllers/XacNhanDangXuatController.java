package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class XacNhanDangXuatController {
    @FXML private Button nutXacNhan;

    private boolean daXacNhan;

    public boolean daXacNhan() {
        return daXacNhan;
    }

    @FXML
    private void onAnNutXacNhan() {
        daXacNhan = true;
        dongHopThoai();
    }

    @FXML
    private void onAnNutHuyBo() {
        dongHopThoai();
    }

    private void dongHopThoai() {
        nutXacNhan.getScene().getWindow().hide();
    }
}
