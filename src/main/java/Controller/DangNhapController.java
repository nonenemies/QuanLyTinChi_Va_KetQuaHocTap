package Controllers;

import Model.Users;
import Services.AuthService;
import Services.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class DangNhapController {

    @FXML private TextField txtDiaChiEmail;
    @FXML private PasswordField txtMatKhau;
    @FXML private Label lblLoi;

    private final AuthService dichVuXacThuc = new AuthService();

    @FXML
    protected void onAnNutDangNhap(ActionEvent suKien) {
        String diaChiEmail = txtDiaChiEmail.getText();
        String matKhau = txtMatKhau.getText();

        if (diaChiEmail.isBlank() || matKhau.isBlank()) {
            lblLoi.setText("Vui lòng nhập đầy đủ Email và Mật khẩu!");
            lblLoi.setVisible(true);
            lblLoi.setManaged(true);
            return;
        }

        Users nguoiDung;
        try {
            nguoiDung = dichVuXacThuc.xacThucDangNhap(diaChiEmail, matKhau);
        } catch (ServiceException loiPhatSinh) {
            lblLoi.setText("Không thể đăng nhập do lỗi hệ thống. Vui lòng thử lại sau.");
            lblLoi.setVisible(true);
            lblLoi.setManaged(true);
            return;
        }

        if (nguoiDung != null) {
            lblLoi.setVisible(false);
            lblLoi.setManaged(false);

            try {
                FXMLLoader boTai = new FXMLLoader(getClass().getResource("/views/ThanhDieuHuongView.fxml"));
                Scene canhUngDung = new Scene(boTai.load(), 1200, 760);
                ThanhDieuHuongController thanhDieuHuong = boTai.getController();
                thanhDieuHuong.batDau(nguoiDung);

                Stage cuaSoHienTai = (Stage) ((Node) suKien.getSource()).getScene().getWindow();
                cuaSoHienTai.setScene(canhUngDung);
                cuaSoHienTai.setTitle("Phần mềm Quản lý Tín chỉ - " + nguoiDung.getHoTen());
                cuaSoHienTai.setMinWidth(1000);
                cuaSoHienTai.setMinHeight(650);
                cuaSoHienTai.centerOnScreen();

            } catch (IOException | RuntimeException loiPhatSinh) {
                lblLoi.setText("Không thể mở màn hình tài khoản. Vui lòng thử lại.");
                lblLoi.setVisible(true);
                lblLoi.setManaged(true);
            }
        } else {
            lblLoi.setText("Email hoặc Mật khẩu không chính xác!");
            lblLoi.setVisible(true);
            lblLoi.setManaged(true);
        }
    }
}