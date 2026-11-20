package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class KetQuaHuyDangKyController {
    @FXML private Label lblTieuDe;
    @FXML private Label lblThongBao;
    @FXML private Button nutDong;

    public void datKetQua(String nguCanh, String tieuDe, String thongBao) {
        lblTieuDe.setText(tieuDe);
        lblThongBao.setText(nguCanh + System.lineSeparator() + thongBao);
    }

    @FXML
    private void dongCuaSo() {
        nutDong.getScene().getWindow().hide();
    }
}
