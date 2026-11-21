package Controllers;

import Model.LopHocThoiKhoaBieu;
import Model.Users;
import Services.DangKyHocService;
import Services.ServiceException;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ThoiKhoaBieuController {
    private static final String[] TEN_NGAY = {
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
    };
    private static final int SO_CA_HOC = 17;
    private static final int CA_NGHI_TRUA = 6;
    private static final int PHUT_BAT_DAU_NGAY = 7 * 60;
    private static final int TONG_PHUT_THOI_KHOA_BIEU = SO_CA_HOC * 60;
    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("H:mm");

    private final DangKyHocService dichVuDangKy = new DangKyHocService();
    private final List<Node> cacTheTietHoc = new ArrayList<>();
    private Users sinhVien;

    @FXML private Label lblTrangThai;
    @FXML private GridPane luoiThoiKhoaBieu;

    @FXML
    private void initialize() {
        cauHinhCot(luoiThoiKhoaBieu);
        themOTrenBang("CA", 0, "timetable-fixed-header");
        for (int ngay = 0; ngay < TEN_NGAY.length; ngay++) {
            themOTrenBang(TEN_NGAY[ngay], ngay + 1, "timetable-day-header");
        }
        themOTrenBang("GIỜ HỌC", 8, "timetable-fixed-header");
        for (int caHoc = 0; caHoc < SO_CA_HOC; caHoc++) {
            themHangThoiKhoaBieu(caHoc);
        }
    }

    public void ganSinhVien(Users sinhVien) {
        this.sinhVien = sinhVien;
        if (sinhVien == null || !"SINHVIEN".equalsIgnoreCase(sinhVien.getVaiTro())
                || sinhVien.getMaSo() == null || sinhVien.getMaSo().isBlank()) {
            hienThiTrangThai("Không xác định được mã sinh viên. Vui lòng đăng nhập lại.", true);
            return;
        }
        taiThoiKhoaBieu();
    }

    @FXML
    private void taiLaiThoiKhoaBieu() {
        taiThoiKhoaBieu();
    }

    private void taiThoiKhoaBieu() {
        xoaThoiKhoaBieu();
        if (sinhVien == null || sinhVien.getMaSo() == null || sinhVien.getMaSo().isBlank()) {
            return;
        }
        try {
            List<LopHocThoiKhoaBieu> cacBuoiHoc =
                    dichVuDangKy.xemLichHocTheoTuan(sinhVien.getMaSo());
            if (cacBuoiHoc.isEmpty()) {
                hienThiTrangThai("Bạn chưa có lớp đang học được xếp lịch. Bảng 20 ca vẫn được hiển thị.", false);
                return;
            }

            for (LopHocThoiKhoaBieu buoiHoc : cacBuoiHoc) {
                int cotNgay = layCotNgay(buoiHoc.getThu());
                if (cotNgay < 1) {
                    continue;
                }
                themTietVaoThoiKhoaBieu(buoiHoc, cotNgay);
            }
            hienThiTrangThai("Thời khóa biểu gồm " + cacBuoiHoc.size()
                    + " buổi học. Mỗi môn nằm theo đúng khung giờ thực tế.", false);
        } catch (ServiceException loiPhatSinh) {
            hienThiTrangThai("Không thể tải thời khóa biểu. Hãy kiểm tra kết nối cơ sở dữ liệu rồi thử lại.", true);
        }
    }

    private int layCotNgay(String thu) {
        if (thu == null) return -1;
        return switch (thu.trim().toLowerCase(Locale.ROOT)) {
            case "thứ 2" -> 1;
            case "thứ 3" -> 2;
            case "thứ 4" -> 3;
            case "thứ 5" -> 4;
            case "thứ 6" -> 5;
            case "thứ 7" -> 6;
            case "chủ nhật" -> 7;
            default -> -1;
        };
    }

    private void themTietVaoThoiKhoaBieu(LopHocThoiKhoaBieu buoiHoc, int cotNgay) {
        LocalTime gioBatDau;
        LocalTime gioKetThuc;
        try {
            gioBatDau = LocalTime.parse(buoiHoc.getGioBatDau());
            gioKetThuc = LocalTime.parse(buoiHoc.getGioKetThuc());
        } catch (DateTimeParseException loiPhatSinh) {
            return;
        }
        int phutBatDauTrongNgay = gioBatDau.getHour() * 60 + gioBatDau.getMinute();
        int phutKetThucTrongNgay = gioKetThuc.getHour() * 60 + gioKetThuc.getMinute();
        int phutBatDauTuDauNgay = phutBatDauTrongNgay - PHUT_BAT_DAU_NGAY;
        if (phutBatDauTuDauNgay < 0) {
            phutBatDauTuDauNgay += 24 * 60;
        }
        int phutKetThucTuDauNgay = phutKetThucTrongNgay - PHUT_BAT_DAU_NGAY;
        if (phutKetThucTuDauNgay < 0) {
            phutKetThucTuDauNgay += 24 * 60;
        }
        if (phutKetThucTuDauNgay <= phutBatDauTuDauNgay) {
            phutKetThucTuDauNgay += 24 * 60;
        }

        int thoiLuong = phutKetThucTuDauNgay - phutBatDauTuDauNgay;
        if (phutBatDauTuDauNgay >= TONG_PHUT_THOI_KHOA_BIEU || phutKetThucTuDauNgay > TONG_PHUT_THOI_KHOA_BIEU || thoiLuong <= 0) {
            return;
        }

        int chiSoCa = phutBatDauTuDauNgay / 60;
        int soHang = (phutBatDauTuDauNgay % 60 + thoiLuong + 59) / 60;
        VBox theHocPhan = taoTheHocPhan(buoiHoc);
        theHocPhan.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(theHocPhan, Priority.ALWAYS);
        GridPane.setVgrow(theHocPhan, Priority.ALWAYS);
        GridPane.setMargin(theHocPhan, new Insets(3));
        luoiThoiKhoaBieu.add(theHocPhan, cotNgay, chiSoCa + 1, 1, soHang);
        cacTheTietHoc.add(theHocPhan);
    }

    private void themHangThoiKhoaBieu(int chiSoHang) {
        int hangLuoi = chiSoHang + 1;
        Label caHoc = new Label("Ca " + (chiSoHang + 1));
        caHoc.getStyleClass().add("timetable-session");
        caHoc.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(caHoc, Priority.ALWAYS);
        luoiThoiKhoaBieu.add(caHoc, 0, hangLuoi);

        for (int ngay = 0; ngay < TEN_NGAY.length; ngay++) {
            VBox oTrongNgay = new VBox(7);
            oTrongNgay.getStyleClass().add("timetable-cell");
            oTrongNgay.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(oTrongNgay, Priority.ALWAYS);
            if (chiSoHang + 1 == CA_NGHI_TRUA) {
                Label gioNghiTrua = new Label("NGHỈ TRƯA");
                gioNghiTrua.setWrapText(true);
                gioNghiTrua.getStyleClass().add("timetable-lunch");
                oTrongNgay.getChildren().add(gioNghiTrua);
            }
            luoiThoiKhoaBieu.add(oTrongNgay, ngay + 1, hangLuoi);
        }

        int phutBatDau = PHUT_BAT_DAU_NGAY + chiSoHang * 60;
        int phutKetThuc = phutBatDau + 60;
        Label gioHienThi = new Label(dinhDangThoiGian(phutBatDau) + " - " + dinhDangThoiGian(phutKetThuc));
        gioHienThi.getStyleClass().add("timetable-time");
        gioHienThi.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(gioHienThi, Priority.ALWAYS);
        luoiThoiKhoaBieu.add(gioHienThi, 8, hangLuoi);
    }

    private void xoaThoiKhoaBieu() {
        luoiThoiKhoaBieu.getChildren().removeAll(cacTheTietHoc);
        cacTheTietHoc.clear();
    }

    private String dinhDangThoiGian(int tongPhutTrongNgay) {
        int phutTrongNgay = Math.floorMod(tongPhutTrongNgay, 24 * 60);
        return LocalTime.of(phutTrongNgay / 60, phutTrongNgay % 60).format(DINH_DANG_GIO);
    }

    private VBox taoTheHocPhan(LopHocThoiKhoaBieu buoiHoc) {
        VBox theHocPhan = new VBox(4);
        theHocPhan.getStyleClass().add("timetable-lesson");
        Label tenMon = new Label(buoiHoc.getTenMon());
        tenMon.setWrapText(true);
        tenMon.getStyleClass().add("timetable-lesson-name");
        Label maMon = new Label(buoiHoc.getMaMon() + " · " + buoiHoc.getMaLop());
        maMon.setWrapText(true);
        maMon.getStyleClass().add("timetable-lesson-code");
        Label khungGioHoc = new Label(buoiHoc.getGioBatDau() + " - " + buoiHoc.getGioKetThuc());
        khungGioHoc.getStyleClass().add("timetable-lesson-time");
        Label hocKy = new Label("Học kỳ " + buoiHoc.getHocKy());
        hocKy.getStyleClass().add("timetable-lesson-semester");
        Label phongHoc = new Label(buoiHoc.getPhongHoc() == null || buoiHoc.getPhongHoc().isBlank()
                ? "Chưa cập nhật phòng học" : buoiHoc.getPhongHoc());
        phongHoc.setWrapText(true);
        phongHoc.getStyleClass().add("timetable-lesson-room");
        theHocPhan.getChildren().addAll(tenMon, maMon, khungGioHoc, phongHoc, hocKy);
        return theHocPhan;
    }

    private void cauHinhCot(GridPane luoi) {
        double[] tyLeCot = {8.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 12.0, 8.0};
        for (double tyLe : tyLeCot) {
            ColumnConstraints cot = new ColumnConstraints();
            cot.setPercentWidth(tyLe);
            cot.setHgrow(Priority.ALWAYS);
            luoi.getColumnConstraints().add(cot);
        }
        luoi.setMaxWidth(Double.MAX_VALUE);
    }

    private void themOTrenBang(String vanBan, int cot, String lopKieu) {
        Label oTieuDe = new Label(vanBan);
        oTieuDe.getStyleClass().add(lopKieu);
        oTieuDe.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(oTieuDe, Priority.ALWAYS);
        luoiThoiKhoaBieu.add(oTieuDe, cot, 0);
    }

    private void hienThiTrangThai(String thongBao, boolean loi) {
        lblTrangThai.setText(thongBao);
        lblTrangThai.getStyleClass().removeAll("timetable-status-error", "timetable-status-info");
        lblTrangThai.getStyleClass().add(loi ? "timetable-status-error" : "timetable-status-info");
        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
    }

}
