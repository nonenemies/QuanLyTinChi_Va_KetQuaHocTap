package Controllers;

import Model.LopHocDangKy;
import Model.Users;
import Services.DangKyHocService;
import Services.ServiceException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.css.PseudoClass;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DangKyTinChiController {
    private static final Logger NHAT_KY = Logger.getLogger(DangKyTinChiController.class.getName());
    private static final PseudoClass HANG_DA_DANG_KY = PseudoClass.getPseudoClass("registered");
    private final DangKyHocService dichVuDangKyHoc = new DangKyHocService();
    private final ObservableList<LopHocDangKy> danhSachLopHoc = FXCollections.observableArrayList();
    private final FilteredList<LopHocDangKy> danhSachLopDaLoc = new FilteredList<>(danhSachLopHoc, lopHoc -> true);
    private Users sinhVien;

    @FXML private Label lblTrangThai;
    @FXML private Label lblTrong;
    @FXML private Label lblTrangThaiTinChi;
    @FXML private Label lblChinhSachTinChi;
    @FXML private TextField txtTimKiem;
    @FXML private TableView<LopHocDangKy> tblLopHoc;
    @FXML private TableColumn<LopHocDangKy, String> colMaHocPhan;
    @FXML private TableColumn<LopHocDangKy, String> colTenHocPhan;
    @FXML private TableColumn<LopHocDangKy, String> colLichHocGiangVien;
    @FXML private TableColumn<LopHocDangKy, String> colSiSoLop;
    @FXML private TableColumn<LopHocDangKy, Integer> colSoTinChi;
    @FXML private TableView<LopHocDangKy> tblLopDaDangKy;
    @FXML private TableColumn<LopHocDangKy, String> colMaLopDaDangKy;
    @FXML private TableColumn<LopHocDangKy, String> colTenHocPhanDaDangKy;
    @FXML private TableColumn<LopHocDangKy, String> colHocKyDaDangKy;
    @FXML private TableColumn<LopHocDangKy, String> colLichHocDaDangKy;
    @FXML private TableColumn<LopHocDangKy, Integer> colSoTinChiDaDangKy;
    @FXML private TableColumn<LopHocDangKy, String> colTrangThaiDangKy;
    @FXML private Button btnXacNhanDangKy;
    @FXML private Button btnHuyDangKy;

    @FXML
    private void initialize() {
        lblChinhSachTinChi.setText("Mức tối thiểu " + DangKyHocService.MIN_TIN_CHI
                + " tín chỉ chỉ dùng để cảnh báo; vượt mức tối đa "
                + DangKyHocService.MAX_TIN_CHI + " tín chỉ sẽ bị từ chối.");
        colTenHocPhan.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colMaHocPhan.setCellValueFactory(o -> new ReadOnlyStringWrapper(
                o.getValue().getMaMon() + " · " + o.getValue().getMaLop()));
        colSiSoLop.setCellValueFactory(new PropertyValueFactory<>("siSo"));
        colLichHocGiangVien.setCellValueFactory(o -> new ReadOnlyStringWrapper(
                o.getValue().getLichHoc() + " · " + o.getValue().getGiangVien()));
        colSoTinChi.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        colMaLopDaDangKy.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        colTenHocPhanDaDangKy.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colHocKyDaDangKy.setCellValueFactory(new PropertyValueFactory<>("hocKy"));
        colLichHocDaDangKy.setCellValueFactory(new PropertyValueFactory<>("lichHoc"));
        colSoTinChiDaDangKy.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        colTrangThaiDangKy.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        tblLopHoc.setItems(danhSachLopDaLoc);
        tblLopHoc.setRowFactory(bang -> new TableRow<>() {
            @Override
            protected void updateItem(LopHocDangKy lopHoc, boolean rong) {
                super.updateItem(lopHoc, rong);
                pseudoClassStateChanged(HANG_DA_DANG_KY, !rong && lopHoc.isDaDangKy());
            }
        });
        tblLopHoc.setFixedCellSize(32);
        tblLopDaDangKy.setFixedCellSize(32);
        danhSachLopDaLoc.addListener((ListChangeListener<LopHocDangKy>)
                thayDoi -> dieuChinhChieuCaoBang(tblLopHoc));
        txtTimKiem.textProperty().addListener((thuocTinh, giaTriCu, giaTriMoi) -> timKiemLop());
        tblLopHoc.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriCu, lopDuocChon) -> capNhatNutXacNhan(lopDuocChon));
        tblLopDaDangKy.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriCu, lopDuocChon) ->
                        btnHuyDangKy.setDisable(lopDuocChon == null || !lopDuocChon.isCoTheHuy()));
    }

    public void ganSinhVien(Users sinhVien) {
        this.sinhVien = sinhVien;
        if (sinhVien == null || !"SINHVIEN".equalsIgnoreCase(sinhVien.getVaiTro())
                || sinhVien.getMaSo() == null || sinhVien.getMaSo().isBlank()) {
            datTrangThai("Không xác định được mã sinh viên. Vui lòng quay lại và đăng nhập lại.", true);
            return;
        }
        taiDanhSachLop();
    }

    @FXML
    private void taiLaiDanhSachLop(ActionEvent suKien) {
        taiDanhSachLop();
    }

    @FXML
    private void xacNhanDangKy(ActionEvent suKien) {
        LopHocDangKy lopDuocChon = tblLopHoc.getSelectionModel().getSelectedItem();
        if (sinhVien == null || lopDuocChon == null) return;
        if (!xacNhanDangKyLop(lopDuocChon)) return;

        try {
            String ketQua = dichVuDangKyHoc.dangKyHoc(sinhVien.getMaSo(), lopDuocChon.getMaLop());
            boolean thanhCong = "THANH_CONG".equals(ketQua);
            datTrangThai(thanhCong ? "Đăng ký lớp thành công." : "Không thể đăng ký lớp này.", !thanhCong);
            if (!thanhCong) {
                hienThongBaoDangKyThatBai(lopDuocChon, thongBaoThatBaiDangKy(ketQua));
            }
            taiDanhSachLop();
        } catch (ServiceException ngoaiLe) {
            datTrangThai("Không thể xử lý đăng ký do lỗi hệ thống. Vui lòng thử lại sau.", true);
        } catch (RuntimeException ngoaiLe) {
            datTrangThai("Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại sau.", true);
        }

    }

    private String thongBaoThatBaiDangKy(String ketQua) {
        return switch (ketQua) {
            case "SINH_VIEN_KHONG_HOAT_DONG" -> "Tài khoản sinh viên không còn hoạt động.";
            case "DOT_DANG_KY_DA_DONG" -> "Đợt đăng ký của ngành chưa mở hoặc đã kết thúc.";
            case "LOP_KHONG_HOAT_DONG" -> "Lớp đã ngừng hoạt động hoặc hết chỗ.";
            case "LOP_CHUA_XEP_LICH" -> "Lớp chưa được xếp lịch học.";
            case "DA_DANG_KY_MON_TRONG_HOC_KY" -> "Bạn đã đăng ký môn này trong học kỳ.";
            case "DA_QUA_MON" -> "Bạn đã đạt môn này nên không thể đăng ký học lại.";
            case "THIEU_MON_TIEN_QUYET" -> "Bạn chưa đạt đủ các môn tiên quyết.";
            case "TRUNG_LICH" -> "Lịch học bị trùng với một lớp bạn đã đăng ký.";
            case "VUOT_QUA_TIN_CHI" -> "Tổng tín chỉ học kỳ không được vượt quá "
                    + DangKyHocService.MAX_TIN_CHI + ".";
            case "DA_CO_DIEM" -> "Không thể hủy lớp đã có điểm thành phần.";
            case "THANH_CONG" -> "Thao tác thành công.";
            default -> "Thông tin đăng ký không còn hợp lệ. Hãy tải lại danh sách lớp.";
        };
    }

    @FXML
    private void huyDangKy(ActionEvent suKien) {
        LopHocDangKy lopDuocChon = tblLopDaDangKy.getSelectionModel().getSelectedItem();
        if (sinhVien == null || lopDuocChon == null || !lopDuocChon.isCoTheHuy()) {
            return;
        }

        if (!xacNhanHuyDangKyLop(lopDuocChon)) return;

        try {
            boolean thanhCong = dichVuDangKyHoc.huyDangKyHoc(sinhVien.getMaSo(), lopDuocChon.getMaLop());
            datTrangThai(thanhCong ? "Đã hủy đăng ký lớp." : "Không thể hủy đăng ký lớp.", !thanhCong);

            hienKetQuaHuyDangKy(lopDuocChon, thanhCong, thanhCong
                    ? "Lớp đã được xóa khỏi thời khóa biểu."
                    : "Lớp không còn đăng ký hoặc đã hoàn thành.");
            taiDanhSachLop();
        } catch (ServiceException ngoaiLe) {
            datTrangThai("Không thể hủy đăng ký do lỗi hệ thống. Vui lòng thử lại sau.", true);
            hienKetQuaHuyDangKy(lopDuocChon, false,
                    "Không thể hủy đăng ký do lỗi hệ thống. Vui lòng thử lại sau.");
        } catch (RuntimeException ngoaiLe) {
            datTrangThai("Đã xảy ra lỗi khi hủy đăng ký. Vui lòng thử lại sau.", true);
            hienKetQuaHuyDangKy(lopDuocChon, false,
                    "Đã xảy ra lỗi khi hủy đăng ký. Vui lòng thử lại sau.");
        }
    }

    private boolean xacNhanDangKyLop(LopHocDangKy lopDuocChon) {
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource(
                    "/views/XacNhanDangKyView.fxml"));
            Parent giaoDien = boTai.load();
            XacNhanDangKyController hopThoai = boTai.getController();
            hopThoai.datChiTiet("Đăng ký lớp " + lopDuocChon.getMaLop() + "?",
                    lopDuocChon.getMaMon() + " · " + lopDuocChon.getTenMon() + " · "
                            + lopDuocChon.getSoTinChi() + " tín chỉ · Học kỳ " + lopDuocChon.getHocKy()
                            + " · " + lopDuocChon.getLichHoc() + " · " + lopDuocChon.getGiangVien());
            hienHopThoai("Xác nhận đăng ký", giaoDien);
            return hopThoai.daXacNhan();
        } catch (IOException | RuntimeException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unable to show registration confirmation.", ngoaiLe);
            datTrangThai("Không thể mở hộp thoại xác nhận đăng ký.", true);
            return false;
        }
    }

    private boolean xacNhanHuyDangKyLop(LopHocDangKy lopDuocChon) {
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource(
                    "/views/XacNhanHuyDangKyView.fxml"));
            Parent giaoDien = boTai.load();
            XacNhanHuyDangKyController hopThoai = boTai.getController();
            hopThoai.datChiTiet("Hủy lớp " + lopDuocChon.getMaLop() + "?",
                    lopDuocChon.getTenMon() + " · Học kỳ " + lopDuocChon.getHocKy()
                            + " · " + lopDuocChon.getLichHoc()
                            + "\nLớp sẽ được cập nhật sĩ số và bạn có thể đăng ký lại nếu còn chỗ.");
            hienHopThoai("Xác nhận hủy đăng ký", giaoDien);
            return hopThoai.daXacNhan();
        } catch (IOException | RuntimeException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unable to show cancellation confirmation.", ngoaiLe);
            datTrangThai("Không thể mở hộp thoại xác nhận hủy đăng ký.", true);
            return false;
        }
    }

    private void hienThongBaoDangKyThatBai(LopHocDangKy lopDuocChon, String thongBao) {
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource(
                    "/views/ThongBaoDangKyThatBaiView.fxml"));
            Parent giaoDien = boTai.load();
            ThongBaoDangKyThatBaiController hopThoai = boTai.getController();
            hopThoai.datNoiDungThongBao("Không thể đăng ký lớp " + lopDuocChon.getMaLop(), thongBao);
            hienHopThoai("Đăng ký không thành công", giaoDien);
        } catch (IOException | RuntimeException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unable to show registration failure dialog.", ngoaiLe);
            datTrangThai("Không thể đăng ký lớp này. " + thongBao, true);
        }
    }

    private void hienKetQuaHuyDangKy(LopHocDangKy lopDuocChon, boolean thanhCong, String thongBao) {
        try {
            FXMLLoader boTai = new FXMLLoader(getClass().getResource(
                    "/views/KetQuaHuyDangKyView.fxml"));
            Parent giaoDien = boTai.load();
            KetQuaHuyDangKyController hopThoai = boTai.getController();
            hopThoai.datKetQua(lopDuocChon.getTenMon() + " · " + lopDuocChon.getMaLop(),
                    thanhCong ? "Đã hủy đăng ký" : "Không thể hủy đăng ký", thongBao);
            hienHopThoai(thanhCong ? "Đã hủy đăng ký" : "Không thể hủy đăng ký", giaoDien);
        } catch (IOException | RuntimeException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unable to show cancellation result dialog.", ngoaiLe);
            datTrangThai(thongBao, !thanhCong);
        }
    }

    private void hienHopThoai(String tieuDe, Parent giaoDien) {
        Stage hopThoai = new Stage();
        Window cuaSoChu = lblTrangThai.getScene().getWindow();
        hopThoai.initOwner(cuaSoChu);
        hopThoai.initModality(Modality.WINDOW_MODAL);
        hopThoai.setTitle(tieuDe);
        hopThoai.setResizable(false);
        hopThoai.setScene(new Scene(giaoDien));
        hopThoai.showAndWait();
    }

    private void taiDanhSachLop() {
        if (sinhVien == null || sinhVien.getMaSo() == null || sinhVien.getMaSo().isBlank()) return;
        try {
            List<LopHocDangKy> danhSachLopPhuHop = dichVuDangKyHoc.xemDanhSachLopDangKy(sinhVien.getMaSo());
            List<LopHocDangKy> danhSachLopDaDangKy =
                    dichVuDangKyHoc.xemDanhSachLopDaDangKy(sinhVien.getMaSo());
            danhSachLopHoc.setAll(danhSachLopPhuHop);
            timKiemLop();
            tblLopDaDangKy.getSelectionModel().clearSelection();
            btnHuyDangKy.setDisable(true);
            tblLopDaDangKy.setItems(FXCollections.observableArrayList(
                    danhSachLopDaDangKy));
            dieuChinhChieuCaoBang(tblLopDaDangKy);
            capNhatTrangThaiTinChi(java.util.stream.Stream.concat(
                    danhSachLopPhuHop.stream(), danhSachLopDaDangKy.stream()).toList());
            lblTrong.setText("Chưa có lớp phù hợp. Hãy kiểm tra đợt đăng ký, chương trình đào tạo và lớp học phần.");
            capNhatNutXacNhan(tblLopHoc.getSelectionModel().getSelectedItem());
            if (danhSachLopPhuHop.isEmpty()) {
                datTrangThai("Chưa có lớp học phần phù hợp với đợt đăng ký và chương trình đào tạo.", false);
            }
        } catch (ServiceException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unable to load course registration data.", ngoaiLe);
            danhSachLopHoc.clear();
            tblLopDaDangKy.setItems(FXCollections.observableArrayList());
            dieuChinhChieuCaoBang(tblLopDaDangKy);
            btnHuyDangKy.setDisable(true);
            lblTrangThaiTinChi.setText("");
            lblTrangThaiTinChi.setVisible(false);
            lblTrangThaiTinChi.setManaged(false);
            lblTrong.setText("Không thể tải danh sách lớp. Kiểm tra kết nối cơ sở dữ liệu rồi thử tải lại.");
            capNhatNutXacNhan(null);
            datTrangThai("Lỗi tải dữ liệu lớp học. Vui lòng thử lại.", true);
        } catch (RuntimeException ngoaiLe) {
            NHAT_KY.log(Level.SEVERE, "Unexpected error while loading course registration data.", ngoaiLe);
            danhSachLopHoc.clear();
            tblLopDaDangKy.setItems(FXCollections.observableArrayList());
            dieuChinhChieuCaoBang(tblLopDaDangKy);
            btnHuyDangKy.setDisable(true);
            lblTrangThaiTinChi.setText("");
            lblTrangThaiTinChi.setVisible(false);
            lblTrangThaiTinChi.setManaged(false);
            lblTrong.setText("Đã xảy ra lỗi khi tải danh sách lớp.");
            capNhatNutXacNhan(null);
            datTrangThai("Đã xảy ra lỗi khi tải dữ liệu.", true);
        }
    }

    private void timKiemLop() {
        String tuKhoa = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        danhSachLopDaLoc.setPredicate(lopHoc -> {
            String noiDungTimKiem = (lopHoc.getMaLop() + " " + lopHoc.getMaMon() + " " + lopHoc.getTenMon()
                    + " " + lopHoc.getGiangVien()).toLowerCase(Locale.ROOT);
            return noiDungTimKiem.contains(tuKhoa);
        });
        boolean coDongDuLieu = !danhSachLopDaLoc.isEmpty();
        lblTrong.setVisible(!coDongDuLieu);
        lblTrong.setManaged(!coDongDuLieu);
    }

    private void capNhatNutXacNhan(LopHocDangKy lopHoc) {
        btnXacNhanDangKy.setDisable(lopHoc == null || lopHoc.isDaDangKy()
                || !lopHoc.isCoLichHoc()
                || lopHoc.getSiSoHienTai() == null || lopHoc.getSiSoToiDa() == null
                || lopHoc.getSiSoHienTai() >= lopHoc.getSiSoToiDa());
    }

    private void dieuChinhChieuCaoBang(TableView<?> bang) {
        int soDongHienThi = Math.max(1, bang.getItems().size());
        double chieuCao = 34 + soDongHienThi * bang.getFixedCellSize();
        bang.setMinHeight(chieuCao);
        bang.setPrefHeight(chieuCao);
        bang.setMaxHeight(chieuCao);
    }

    private void capNhatTrangThaiTinChi(List<LopHocDangKy> danhSachLopPhuHop) {
        List<String> danhSachHocKy = danhSachLopPhuHop.stream()
                .map(LopHocDangKy::getHocKy)
                .filter(hocKy -> hocKy != null && !hocKy.isBlank())
                .distinct()
                .sorted()
                .toList();
        if (danhSachHocKy.isEmpty()) {
            lblTrangThaiTinChi.setText("");
            lblTrangThaiTinChi.setVisible(false);
            lblTrangThaiTinChi.setManaged(false);
            return;
        }

        List<Integer> danhSachTongTinChi = danhSachHocKy.stream()
                .map(hocKy -> dichVuDangKyHoc.layTongTinChiDangKy(sinhVien.getMaSo(), hocKy))
                .toList();
        List<String> danhSachThongKe = java.util.stream.IntStream.range(0, danhSachHocKy.size()).mapToObj(chiSo -> {
            String hocKy = danhSachHocKy.get(chiSo);
            int tongTinChi = danhSachTongTinChi.get(chiSo);
            if (tongTinChi < DangKyHocService.MIN_TIN_CHI) {
                return hocKy + ": Bạn mới đăng ký " + tongTinChi + "/"
                        + DangKyHocService.MIN_TIN_CHI + " tín chỉ tối thiểu.";
            }
            return hocKy + ": đã đăng ký " + tongTinChi
                    + " tín chỉ, đạt mức tối thiểu " + DangKyHocService.MIN_TIN_CHI + ".";
        }).toList();
        boolean duoiMucToiThieu = danhSachTongTinChi.stream().anyMatch(
                tongTinChi -> tongTinChi < DangKyHocService.MIN_TIN_CHI);
        lblTrangThaiTinChi.getStyleClass().removeAll(
                "registration-message-success", "registration-message-error");
        lblTrangThaiTinChi.getStyleClass().add(
                duoiMucToiThieu ? "registration-message-error" : "registration-message-success");
        lblTrangThaiTinChi.setText(String.join(System.lineSeparator(), danhSachThongKe));
        lblTrangThaiTinChi.setVisible(true);
        lblTrangThaiTinChi.setManaged(true);
    }

    private void datTrangThai(String thongBao, boolean loi) {
        lblTrangThai.setText(thongBao);
        lblTrangThai.getStyleClass().removeAll("registration-message-success", "registration-message-error");
        lblTrangThai.getStyleClass().add(loi ? "registration-message-error" : "registration-message-success");
        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
    }

}
