package Controllers;

import Model.GiangVien;
import Model.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThanhDieuHuongController {
    private static final Logger NHAT_KY = Logger.getLogger(ThanhDieuHuongController.class.getName());
    private static final String MAN_HINH_TONG_QUAN = "overview";
    private static final String MAN_HINH_THOI_KHOA_BIEU = "timetable";
    private static final String MAN_HINH_KET_QUA_HOC_TAP = "student-grades";
    private static final String MAN_HINH_DANG_KY_TIN_CHI = "registration";
    private static final String MAN_HINH_BANG_DIEM = "gradebook";
    private static final String MAN_HINH_QUAN_LY_SINH_VIEN = "admin-students";
    private static final String MAN_HINH_QUAN_LY_GIANG_VIEN = "admin-lecturers";
    private static final String MAN_HINH_QUAN_LY_MON_HOC = "admin-courses";

    @FXML private Label lblTenNguoiDung;
    @FXML private Label lblVaiTroNguoiDung;
    @FXML private Label lblTieuDeNoiDung;
    @FXML private VBox cacMucDieuHuong;
    @FXML private VBox vungNoiDung;

    private Users nguoiDung;
    private String manHinhHienTai;
    private final List<Button> cacNutDieuHuong = new ArrayList<>();

    public void batDau(Users nguoiDungXacThuc) throws IOException {
        if (nguoiDungXacThuc == null) {
            throw new IllegalArgumentException("Authenticated user is required.");
        }
        nguoiDung = nguoiDungXacThuc;
        lblTenNguoiDung.setText(nguoiDung.getHoTen());
        lblVaiTroNguoiDung.setText(nhanVaiTro(nguoiDung));
        cauHinhDieuHuong();

        if ("SINHVIEN".equalsIgnoreCase(nguoiDung.getVaiTro())
                || nguoiDung instanceof GiangVien
                || "ADMIN".equalsIgnoreCase(nguoiDung.getVaiTro())) {
            moManHinh(MAN_HINH_TONG_QUAN);
        } else {
            hienThiLoi("Vai trò tài khoản không được hỗ trợ.");
        }
    }

    public void moThoiKhoaBieu() {
        moManHinh(MAN_HINH_THOI_KHOA_BIEU);
    }

    @FXML
    private void onAnNutDangXuat() {
        if (!xacNhanDangXuat()) return;

        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource("/views/DangNhapView.fxml"));
            Scene canh = new Scene(boTai.load(), 1080, 680);
            Stage cuaSo = (Stage) cacMucDieuHuong.getScene().getWindow();
            cuaSo.setScene(canh);
            cuaSo.setTitle("Đăng nhập - Credit Hub");
            cuaSo.setMinWidth(900);
            cuaSo.setMinHeight(600);
            cuaSo.centerOnScreen();
            nguoiDung = null;
        } catch (IOException | RuntimeException loiPhatSinh) {
            hienThiLoi("Không thể đăng xuất lúc này. Vui lòng thử lại.");
        }
    }

    private boolean xacNhanDangXuat() {
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource("/views/XacNhanDangXuatView.fxml"));
            Parent giaoDien = boTai.load();
            XacNhanDangXuatController hopThoai = boTai.getController();
            Stage cuaSoHopThoai = new Stage();
            cuaSoHopThoai.initOwner(cacMucDieuHuong.getScene().getWindow());
            cuaSoHopThoai.initModality(Modality.WINDOW_MODAL);
            cuaSoHopThoai.setTitle("Xác nhận đăng xuất");
            cuaSoHopThoai.setResizable(false);
            cuaSoHopThoai.setScene(new Scene(giaoDien));
            cuaSoHopThoai.showAndWait();
            return hopThoai.daXacNhan();
        } catch (IOException | RuntimeException loiPhatSinh) {
            NHAT_KY.log(Level.SEVERE, "Unable to show logout confirmation.", loiPhatSinh);
            hienThiLoi("Không thể mở hộp thoại xác nhận đăng xuất.");
            return false;
        }
    }

    private void cauHinhDieuHuong() {
        cacMucDieuHuong.getChildren().clear();
        cacNutDieuHuong.clear();
        if ("SINHVIEN".equalsIgnoreCase(nguoiDung.getVaiTro())) {
            themNutDieuHuong("Tổng quan", MAN_HINH_TONG_QUAN);
            themNutDieuHuong("Thời khóa biểu", MAN_HINH_THOI_KHOA_BIEU);
            themNutDieuHuong("Đăng ký tín chỉ", MAN_HINH_DANG_KY_TIN_CHI);
            themNutDieuHuong("Điểm tổng kết", MAN_HINH_KET_QUA_HOC_TAP);
        } else if (nguoiDung instanceof GiangVien) {
            themNutDieuHuong("Tổng quan", MAN_HINH_TONG_QUAN);
            themNutDieuHuong("Lớp phụ trách và bảng điểm", MAN_HINH_BANG_DIEM);
        } else if ("ADMIN".equalsIgnoreCase(nguoiDung.getVaiTro())) {
            themNutDieuHuong("Tổng quan", MAN_HINH_TONG_QUAN);
            themNutDieuHuong("Quản lý sinh viên", MAN_HINH_QUAN_LY_SINH_VIEN);
            themNutDieuHuong("Quản lý giảng viên", MAN_HINH_QUAN_LY_GIANG_VIEN);
            themNutDieuHuong("Quản lý môn học", MAN_HINH_QUAN_LY_MON_HOC);
        }
    }

    private void themNutDieuHuong(String tieuDe, String manHinh) {
        Button nutDieuHuong = new Button(tieuDe);
        nutDieuHuong.setMaxWidth(Double.MAX_VALUE);
        nutDieuHuong.getStyleClass().add("nav-item");
        nutDieuHuong.setOnAction(suKien -> moManHinh(manHinh));
        cacMucDieuHuong.getChildren().add(nutDieuHuong);
        cacNutDieuHuong.add(nutDieuHuong);
    }

    private void moManHinh(String manHinh) {
        String taiNguyen = switch (manHinh) {
            case MAN_HINH_TONG_QUAN -> "/views/DashBoardView.fxml";
            case MAN_HINH_THOI_KHOA_BIEU -> "/views/ThoiKhoaBieuView.fxml";
            case MAN_HINH_KET_QUA_HOC_TAP -> "/views/KetQuaHocTapView.fxml";
            case MAN_HINH_DANG_KY_TIN_CHI -> "/views/DangKyTinChiView.fxml";
            case MAN_HINH_BANG_DIEM -> "/views/GiangVienView.fxml";
            case MAN_HINH_QUAN_LY_SINH_VIEN, MAN_HINH_QUAN_LY_GIANG_VIEN, MAN_HINH_QUAN_LY_MON_HOC -> "/views/AdminView.fxml";
            default -> throw new IllegalArgumentException("Unknown application screen.");
        };
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource(taiNguyen));
            Parent trang = boTai.load();
            Object boDieuKhien = boTai.getController();
            if (boDieuKhien instanceof DashBoardController tongQuan) {
                tongQuan.ganNguoiDungDangNhap(nguoiDung);
                tongQuan.ganThanhDieuHuong(this);
            } else if (boDieuKhien instanceof DangKyTinChiController dangKy) {
                dangKy.ganSinhVien(nguoiDung);
            } else if (boDieuKhien instanceof ThoiKhoaBieuController thoiKhoaBieu) {
                thoiKhoaBieu.ganSinhVien(nguoiDung);
            } else if (boDieuKhien instanceof KetQuaHocTapController ketQuaHocTap) {
                ketQuaHocTap.ganSinhVien(nguoiDung);
            } else if (boDieuKhien instanceof GiangVienController giangVien) {
                giangVien.ganGiangVien(nguoiDung);
            } else if (boDieuKhien instanceof AdminController quanTriVien) {
                quanTriVien.ganQuanTriVien(nguoiDung, manHinh);
            }
            VBox.setVgrow(trang, Priority.ALWAYS);
            if (trang instanceof Region vungCoTheKeoDai) {
                vungCoTheKeoDai.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
            vungNoiDung.getChildren().setAll(trang);
            manHinhHienTai = manHinh;
            lblTieuDeNoiDung.setText(tieuDeManHinh(manHinh));
            capNhatLuaChonDieuHuong();
        } catch (IOException | RuntimeException loiPhatSinh) {
            NHAT_KY.log(Level.SEVERE, "Unable to open screen: " + manHinh, loiPhatSinh);
            hienThiLoi("Không thể mở chức năng này: " + thongBaoGoc(loiPhatSinh));
        }
    }

    private String thongBaoGoc(Throwable loi) {
        Throwable nguyenNhan = loi;
        while (nguyenNhan.getCause() != null) {
            nguyenNhan = nguyenNhan.getCause();
        }
        String thongBao = nguyenNhan.getMessage();
        return thongBao == null || thongBao.isBlank() ? "lỗi không xác định" : thongBao;
    }

    private void capNhatLuaChonDieuHuong() {
        for (Button nutDieuHuong : cacNutDieuHuong) {
            nutDieuHuong.getStyleClass().remove("nav-item-active");
            if (nutDieuHuong.getOnAction() != null
                    && nutDieuHuong.getText().equals(tieuDeDieuHuongTheoManHinh(manHinhHienTai))) {
                nutDieuHuong.getStyleClass().add("nav-item-active");
            }
        }
    }

    private String tieuDeDieuHuongTheoManHinh(String manHinh) {
        return switch (manHinh) {
            case MAN_HINH_TONG_QUAN -> "Tổng quan";
            case MAN_HINH_THOI_KHOA_BIEU -> "Thời khóa biểu";
            case MAN_HINH_KET_QUA_HOC_TAP -> "Điểm tổng kết";
            case MAN_HINH_DANG_KY_TIN_CHI -> "Đăng ký tín chỉ";
            case MAN_HINH_BANG_DIEM -> "Lớp phụ trách và bảng điểm";
            case MAN_HINH_QUAN_LY_SINH_VIEN -> "Quản lý sinh viên";
            case MAN_HINH_QUAN_LY_GIANG_VIEN -> "Quản lý giảng viên";
            case MAN_HINH_QUAN_LY_MON_HOC -> "Quản lý môn học";
            default -> "";
        };
    }

    private String tieuDeManHinh(String manHinh) {
        return switch (manHinh) {
            case MAN_HINH_TONG_QUAN -> "Tổng quan";
            case MAN_HINH_THOI_KHOA_BIEU -> "Thời khóa biểu";
            case MAN_HINH_KET_QUA_HOC_TAP -> "Điểm tổng kết";
            case MAN_HINH_DANG_KY_TIN_CHI -> "Đăng ký tín chỉ";
            case MAN_HINH_BANG_DIEM -> "Lớp phụ trách và bảng điểm";
            case MAN_HINH_QUAN_LY_SINH_VIEN -> "Quản lý sinh viên & tài khoản";
            case MAN_HINH_QUAN_LY_GIANG_VIEN -> "Quản lý giảng viên & tài khoản";
            case MAN_HINH_QUAN_LY_MON_HOC -> "Quản lý môn học";
            default -> "";
        };
    }

    private String nhanVaiTro(Users nguoiDungXacThuc) {
        if (nguoiDungXacThuc instanceof GiangVien) return "GIẢNG VIÊN";
        if ("SINHVIEN".equalsIgnoreCase(nguoiDungXacThuc.getVaiTro())) return "SINH VIÊN";
        if ("ADMIN".equalsIgnoreCase(nguoiDungXacThuc.getVaiTro())) return "QUẢN TRỊ VIÊN";
        return nguoiDungXacThuc.getVaiTro();
    }

    private void hienThiLoi(String thongBao) {
        Alert hopThoai = new Alert(Alert.AlertType.ERROR, thongBao);
        hopThoai.setHeaderText("Không thể tiếp tục");
        hopThoai.showAndWait();
    }
}
