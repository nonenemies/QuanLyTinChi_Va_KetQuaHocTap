package Controllers;

import Model.KetQuaHocTap;
import Model.SinhVien;
import Model.Users;
import Services.DiemService;
import Services.ServiceException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KetQuaHocTapController {
    private static final Pattern DINH_DANG_NAM_HOC_KY = Pattern.compile("^(\\d{4})-(\\d+)$");
    private static final DecimalFormat DINH_DANG_DIEM = new DecimalFormat("0.##");

    private final DiemService dichVuDiem = new DiemService();
    private SinhVien sinhVien;

    @FXML private VBox vungThongKeGpa;
    @FXML private VBox ketQuaHocKy;
    @FXML private Label lblTrangThai;
    @FXML private Button nutTaiLai;

    public void ganSinhVien(Users nguoiDung) {
        if (!(nguoiDung instanceof SinhVien sinhVienXacThuc)) {
            hienThiLoi("Không xác định được tài khoản sinh viên. Vui lòng đăng nhập lại.");
            return;
        }
        this.sinhVien = sinhVienXacThuc;
        taiKetQua();
    }

    @FXML
    private void onAnNutTaiLaiKetQua() {
        taiKetQua();
    }

    private void taiKetQua() {
        if (sinhVien == null) return;
        nutTaiLai.setDisable(true);
        vungThongKeGpa.getChildren().clear();
        ketQuaHocKy.getChildren().clear();
        try {
            List<KetQuaHocTap> ketQuaHocTap = dichVuDiem.layKetQuaHocTap(sinhVien);
            hienThiThongKeGpa(ketQuaHocTap);
            if (ketQuaHocTap.isEmpty()) {
                hienThiThongTin("Chưa có học phần nào được ghi nhận trong bảng điểm.");
                return;
            }
            hienThiTheoHocKy(ketQuaHocTap);
        } catch (ServiceException loiPhatSinh) {
            hienThiLoi("Không thể tải bảng điểm tổng kết. Vui lòng thử lại.");
        } finally {
            nutTaiLai.setDisable(false);
        }
    }

    private void hienThiTheoHocKy(List<KetQuaHocTap> ketQuaHocTap) {
        Map<String, List<KetQuaHocTap>> nhomTheoHocKy = new LinkedHashMap<>();
        ketQuaHocTap.stream()
                .sorted(Comparator.comparing(KetQuaHocTap::getHocKy, this::soSanhHocKy)
                        .thenComparing(KetQuaHocTap::getMaMon)
                        .thenComparing(KetQuaHocTap::getMaLop))
                .forEach(ketQua -> nhomTheoHocKy.computeIfAbsent(ketQua.getHocKy(), khoa -> new ArrayList<>()).add(ketQua));

        List<String> thuTuHocKy = new ArrayList<>(nhomTheoHocKy.keySet());
        List<VBox> cacTheHocKy = new ArrayList<>();

        for (String hocKy : thuTuHocKy) {
            List<KetQuaHocTap> ketQuaHocKy = nhomTheoHocKy.get(hocKy);
            double tongDiemCoTrongSo = 0;
            long tinChiCoDiem = 0;
            long tinChiDat = 0;
            Set<String> monHocDaDat = new LinkedHashSet<>();

            for (KetQuaHocTap ketQua : ketQuaHocKy) {
                int tinChi = ketQua.getSoTinChi() == null ? 0 : ketQua.getSoTinChi();
                if (ketQua.getDiemTongKet() != null && tinChi > 0) {
                    tongDiemCoTrongSo += ketQua.getDiemTongKet() * tinChi;
                    tinChiCoDiem += tinChi;
                }
                if (daDat(ketQua)) {
                    if (monHocDaDat.add(ketQua.getMaMon()) && ketQua.getSoTinChi() != null) {
                        tinChiDat += ketQua.getSoTinChi();
                    }
                }
            }

            double diemTrungBinhHocKy = tinChiCoDiem == 0 ? Double.NaN
                    : tongDiemCoTrongSo / tinChiCoDiem;

            VBox theHocKy = taoTheHocKy(hocKy, ketQuaHocKy, diemTrungBinhHocKy, tinChiDat);
            cacTheHocKy.add(theHocKy);
        }
        for (int viTri = cacTheHocKy.size() - 1; viTri >= 0; viTri--) {
            ketQuaHocKy.getChildren().add(cacTheHocKy.get(viTri));
        }
        hienThiThongTin("Điểm trung bình học kỳ tính theo tín chỉ; GPA tích lũy lấy điểm cao nhất của mỗi môn. "
                + "Tín chỉ tích lũy chỉ tính các học phần đã hoàn thành và đạt từ 4,0.");
    }

    private void hienThiThongKeGpa(List<KetQuaHocTap> ketQuaHocTap) {
        Set<String> cacMonDaDat = new LinkedHashSet<>();
        long tinChiTichLuy = 0;
        for (KetQuaHocTap ketQua : ketQuaHocTap) {
            if (daDat(ketQua) && cacMonDaDat.add(ketQua.getMaMon()) && ketQua.getSoTinChi() != null) {
                tinChiTichLuy += ketQua.getSoTinChi();
            }
        }

        HBox thongKe = new HBox(12,
                taoChiSo("GPA TÍCH LŨY", dinhDangDiemTrungBinh(dichVuDiem.tinhGpaTichLuy(ketQuaHocTap, null))),
                taoChiSo("TÍN CHỈ TÍCH LŨY", tinChiTichLuy + " tín chỉ"));
        thongKe.getStyleClass().add("grades-summary");
        for (javafx.scene.Node thanhPhan : thongKe.getChildren()) HBox.setHgrow(thanhPhan, Priority.ALWAYS);
        vungThongKeGpa.getChildren().setAll(thongKe);
    }

    private boolean daDat(KetQuaHocTap ketQua) {
        return "DA_HOAN_THANH".equals(ketQua.getTrangThaiDangKy())
                && ketQua.getDiemTongKet() != null && ketQua.getDiemTongKet() >= 4.0f;
    }

    private VBox taoTheHocKy(String hocKy, List<KetQuaHocTap> ketQuaHocTap,
                                    double diemTrungBinhHocKy, long tinChiHocKy) {
        Label tieuDe = new Label("Học kỳ " + hocKy);
        tieuDe.getStyleClass().add("section-title");

        TableView<KetQuaHocTap> bangKetQua = new TableView<>(
                FXCollections.observableArrayList(ketQuaHocTap));
        bangKetQua.getStyleClass().add("registration-table");
        bangKetQua.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        bangKetQua.setPrefHeight(Math.min(320, 44 + ketQuaHocTap.size() * 32));
        bangKetQua.setMinHeight(Math.min(120, 44 + ketQuaHocTap.size() * 32));
        bangKetQua.setPlaceholder(new Label("Chưa có kết quả học tập."));
        bangKetQua.getColumns().add(taoCot("Mã lớp", 90, KetQuaHocTap::getMaLop));
        bangKetQua.getColumns().add(taoCot("Mã môn", 90, KetQuaHocTap::getMaMon));
        bangKetQua.getColumns().add(taoCot("Học phần", 220, KetQuaHocTap::getTenMon));
        bangKetQua.getColumns().add(taoCot("Tín chỉ", 70, ketQua ->
                ketQua.getSoTinChi() == null ? "—" : ketQua.getSoTinChi().toString()));
        bangKetQua.getColumns().add(taoCot("Điểm tổng kết", 100, ketQua -> ketQua.getDiemTongKet() == null
                ? "Chưa có điểm" : DINH_DANG_DIEM.format(ketQua.getDiemTongKet())));
        bangKetQua.getColumns().add(taoCot("Điểm thành phần", 250, ketQua ->
                ketQua.getDiemThanhPhan() == null || ketQua.getDiemThanhPhan().isBlank()
                        ? "Chưa có điểm thành phần" : ketQua.getDiemThanhPhan()));
        bangKetQua.getColumns().add(taoCot("Trạng thái", 115, KetQuaHocTap::getTrangThai));

        HBox thongKe = new HBox(12,
                taoChiSo("ĐIỂM TB HỌC KỲ", dinhDangDiemTrungBinh(diemTrungBinhHocKy)),
                taoChiSo("TÍN CHỈ ĐẠT TRONG KỲ", tinChiHocKy + " tín chỉ"));
        thongKe.getStyleClass().add("grades-summary");
        for (javafx.scene.Node thanhPhan : thongKe.getChildren()) HBox.setHgrow(thanhPhan, Priority.ALWAYS);

        VBox theHocKy = new VBox(12, tieuDe, bangKetQua, thongKe);
        theHocKy.getStyleClass().add("registration-card");
        return theHocKy;
    }

    private TableColumn<KetQuaHocTap, String> taoCot(
            String tieuDe, double doRong, java.util.function.Function<KetQuaHocTap, String> giaTri) {
        TableColumn<KetQuaHocTap, String> cotKetQua = new TableColumn<>(tieuDe);
        cotKetQua.setPrefWidth(doRong);
        cotKetQua.setCellValueFactory(duLieu -> new ReadOnlyStringWrapper(giaTri.apply(duLieu.getValue())));
        return cotKetQua;
    }

    private VBox taoChiSo(String nhanDe, String giaTri) {
        Label lblNhanDe = new Label(nhanDe);
        lblNhanDe.getStyleClass().add("grades-metric-heading");
        Label lblGiaTri = new Label(giaTri);
        lblGiaTri.getStyleClass().add("grades-metric-value");
        VBox theChiSo = new VBox(6, lblNhanDe, lblGiaTri);
        theChiSo.getStyleClass().add("grades-metric");
        return theChiSo;
    }

    private int soSanhHocKy(String hocKyDau, String hocKySau) {
        Matcher khopHocKyDau = DINH_DANG_NAM_HOC_KY.matcher(hocKyDau == null ? "" : hocKyDau);
        Matcher khopHocKySau = DINH_DANG_NAM_HOC_KY.matcher(hocKySau == null ? "" : hocKySau);
        if (khopHocKyDau.matches() && khopHocKySau.matches()) {
            int soSanhNam = Integer.compare(
                    Integer.parseInt(khopHocKyDau.group(1)), Integer.parseInt(khopHocKySau.group(1)));
            if (soSanhNam != 0) return soSanhNam;
            return Integer.compare(Integer.parseInt(khopHocKyDau.group(2)), Integer.parseInt(khopHocKySau.group(2)));
        }
        return Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER).compare(hocKyDau, hocKySau);
    }

    private String dinhDangDiemTrungBinh(double diemTrungBinh) {
        return Double.isNaN(diemTrungBinh) ? "—" : DINH_DANG_DIEM.format(diemTrungBinh) + " / 10";
    }

    private void hienThiThongTin(String thongBao) {
        lblTrangThai.getStyleClass().removeAll("teacher-status-error", "teacher-status-info");
        lblTrangThai.getStyleClass().add("teacher-status-info");
        lblTrangThai.setText(thongBao);
        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
    }

    private void hienThiLoi(String thongBao) {
        lblTrangThai.getStyleClass().removeAll("teacher-status-error", "teacher-status-info");
        lblTrangThai.getStyleClass().add("teacher-status-error");
        lblTrangThai.setText(thongBao);
        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
    }
}
