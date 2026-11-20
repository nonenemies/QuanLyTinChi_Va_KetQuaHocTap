package Controllers;

import Model.Admin;
import Model.GiangVien;
import Model.KetQuaHocTap;
import Model.LopHocPhan;
import Model.MonHoc;
import Model.SinhVien;
import Model.Users;
import Services.AdminService;
import Services.DiemService;
import Services.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashBoardController {
    private static final Logger NHAT_KY = Logger.getLogger(DashBoardController.class.getName());
    private static final DecimalFormat DINH_DANG_DIEM = new DecimalFormat("0.##");

    private final DiemService dichVuDiem = new DiemService();
    private final AdminService dichVuQuanTri = new AdminService();

    @FXML private Label lblLoiChao;
    @FXML private Label lblTomTatVaiTro;
    @FXML private Label lblNhanVaiTro;
    @FXML private Label lblTrangThaiTaiKhoan;
    @FXML private Label lblTrangThai;
    @FXML private Label lblTieuDeHoSo;
    @FXML private Label lblTieuDeHoatDong;
    @FXML private Label lblMoTaHoatDong;
    @FXML private HBox cacTheThongKe;
    @FXML private VBox chiTietHoSo;
    @FXML private VBox danhSachHoatDong;
    @FXML private Button nutThoiKhoaBieu;

    private Users nguoiDungDangNhap;
    private ThanhDieuHuongController thanhDieuHuong;

    @FXML
    private void initialize() {
        lblTrangThai.setVisible(false);
        lblTrangThai.setManaged(false);
    }

    public void ganNguoiDungDangNhap(Users nguoiDung) {
        nguoiDungDangNhap = nguoiDung;
        if (nguoiDungDangNhap == null) {
            hienThiLoi("Không xác định được tài khoản đang đăng nhập.");
            return;
        }
        lblLoiChao.setText("Xin chào, " + giaTriHoacGach(nguoiDungDangNhap.getHoTen()));
        lblNhanVaiTro.setText(nhanVaiTro(nguoiDungDangNhap));
        lblTrangThaiTaiKhoan.setText("HOAT_DONG".equals(nguoiDungDangNhap.getTrangThai())
                ? "Tài khoản đang hoạt động" : "Tài khoản đã ngừng hoạt động");
        taiTongQuan();
    }

    public void ganThanhDieuHuong(ThanhDieuHuongController thanhDieuHuong) {
        this.thanhDieuHuong = thanhDieuHuong;
    }

    @FXML
    private void taiLaiTongQuan() {
        taiTongQuan();
    }

    @FXML
    private void moThoiKhoaBieu() {
        if (thanhDieuHuong != null && nguoiDungDangNhap instanceof SinhVien) {
            thanhDieuHuong.moThoiKhoaBieu();
        }
    }

    private void taiTongQuan() {
        cacTheThongKe.getChildren().clear();
        chiTietHoSo.getChildren().clear();
        danhSachHoatDong.getChildren().clear();
        nutThoiKhoaBieu.setVisible(nguoiDungDangNhap instanceof SinhVien);
        nutThoiKhoaBieu.setManaged(nguoiDungDangNhap instanceof SinhVien);
        lblTrangThai.setVisible(false);
        lblTrangThai.setManaged(false);

        try {
            if (nguoiDungDangNhap instanceof SinhVien sinhVien) {
                taiTongQuanSinhVien(sinhVien);
            } else if (nguoiDungDangNhap instanceof GiangVien giangVien) {
                taiTongQuanGiangVien(giangVien);
            } else if (nguoiDungDangNhap instanceof Admin quanTriVien) {
                taiTongQuanQuanTriVien(quanTriVien);
            } else {
                hienThiLoi("Vai trò tài khoản không được hỗ trợ.");
            }
        } catch (ServiceException loiPhatSinh) {
            NHAT_KY.log(Level.SEVERE, "Không thể tải tổng quan cho vai trò "
                    + nguoiDungDangNhap.getVaiTro(), loiPhatSinh);
            hienThiLoi("Không thể tải dữ liệu tổng quan. Vui lòng thử tải lại.");
        }
    }

    private void taiTongQuanSinhVien(SinhVien sinhVien) {
        lblTomTatVaiTro.setText("Thông tin học tập và tiến độ các học phần của bạn.");
        lblTieuDeHoSo.setText("Thông tin sinh viên");
        lblTieuDeHoatDong.setText("Học phần đang theo học");
        lblMoTaHoatDong.setText("Các đăng ký học phần hiện còn hiệu lực.");

        List<KetQuaHocTap> danhSachDangKy = dichVuDiem.layKetQuaHocTap(sinhVien);
        long soHocPhanDangHoc = danhSachDangKy.stream()
                .filter(muc -> "DANG_HOC".equals(muc.getTrangThaiDangKy())).count();
        long tinChiTichLuy = 0;
        Set<String> cacMonDaDat = new LinkedHashSet<>();
        for (KetQuaHocTap dangKy : danhSachDangKy) {
            int tinChi = dangKy.getSoTinChi() == null ? 0 : dangKy.getSoTinChi();
            if ("DA_HOAN_THANH".equals(dangKy.getTrangThaiDangKy())
                    && dangKy.getDiemTongKet() != null && dangKy.getDiemTongKet() >= 4
                    && cacMonDaDat.add(dangKy.getMaMon())) {
                tinChiTichLuy += tinChi;
            }
        }
        double diemTrungBinhTichLuy = dichVuDiem.tinhGpaTichLuy(danhSachDangKy, null);
        String diemTrungBinh = Double.isNaN(diemTrungBinhTichLuy) ? "—" : DINH_DANG_DIEM.format(diemTrungBinhTichLuy) + " / 10";
        themChiSo("HỌC PHẦN ĐANG HỌC", Long.toString(soHocPhanDangHoc), "HP", "overview-icon-blue");
        themChiSo("TÍN CHỈ ĐÃ TÍCH LŨY", Long.toString(tinChiTichLuy), "TC", "overview-icon-violet");
        themChiSo("ĐIỂM TRUNG BÌNH", diemTrungBinh, "TB", "overview-icon-green");

        themChiTietHoSo("Email", giaTriHoacGach(sinhVien.getEmail()));
        themChiTietHoSo("Mã sinh viên", giaTriHoacGach(sinhVien.getMaSinhVien()));
        themChiTietHoSo("Lớp", giaTriHoacGach(sinhVien.getLop()));
        themChiTietHoSo("Ngành", giaTriHoacGach(sinhVien.getNganh() == null
                ? null : sinhVien.getNganh().getTenNganh()));
        themChiTietHoSo("Khóa học", sinhVien.getKhoaHoc() == null ? "—" : sinhVien.getKhoaHoc().toString());
        themChiTietHoSo("Hệ đào tạo", giaTriHoacGach(sinhVien.getHeDaoTao()));

        List<KetQuaHocTap> danhSachDangKyDangHoc = danhSachDangKy.stream()
                .filter(muc -> "DANG_HOC".equals(muc.getTrangThaiDangKy()))
                .toList();
        if (danhSachDangKyDangHoc.isEmpty()) {
            themHoatDongTrong("Hiện bạn chưa có học phần đang theo học.");
        } else {
            danhSachDangKyDangHoc.forEach(muc -> themHoatDong(
                    muc.getTenMon(),
                    muc.getMaMon() + " · Lớp " + muc.getMaLop() + " · Học kỳ " + muc.getHocKy()
                            + " · " + muc.getLichHoc(),
                    muc.getDiemTongKet() == null
                            ? "Đang học" : "Điểm " + DINH_DANG_DIEM.format(muc.getDiemTongKet())));
        }
    }

    private void taiTongQuanGiangVien(GiangVien giangVien) {
        lblTomTatVaiTro.setText("Thông tin giảng dạy và các lớp học phần được phân công.");
        lblTieuDeHoSo.setText("Thông tin giảng viên");
        lblTieuDeHoatDong.setText("Lớp học phần phụ trách");
        lblMoTaHoatDong.setText("Các lớp đang hoạt động và sĩ số hiện tại.");

        List<LopHocPhan> danhSachLopHocPhan = dichVuDiem.layLopPhuTrach(giangVien).stream()
                .filter(muc -> "HOAT_DONG".equals(muc.getTrangThai()))
                .toList();
        int soSinhVienDangHoc = danhSachLopHocPhan.stream().map(LopHocPhan::getSiSoHienTai)
                .filter(giaTri -> giaTri != null).mapToInt(Integer::intValue).sum();
        long soHocKyPhuTrach = danhSachLopHocPhan.stream().map(LopHocPhan::getHocKy)
                .filter(giaTri -> giaTri != null).distinct().count();
        themChiSo("LỚP ĐANG PHỤ TRÁCH", Integer.toString(danhSachLopHocPhan.size()), "LỚP", "overview-icon-blue");
        themChiSo("SINH VIÊN ĐANG HỌC", Integer.toString(soSinhVienDangHoc), "SINH VIÊN", "overview-icon-violet");
        themChiSo("HỌC KỲ ĐƯỢC PHÂN CÔNG", Long.toString(soHocKyPhuTrach), "HK", "overview-icon-green");

        themChiTietHoSo("Email", giaTriHoacGach(giangVien.getEmail()));
        themChiTietHoSo("Mã giảng viên", giaTriHoacGach(giangVien.getMaGiangVien()));
        themChiTietHoSo("Chuyên ngành", giaTriHoacGach(giangVien.getChuyenNganh()));
        themChiTietHoSo("Học vị", giaTriHoacGach(giangVien.getHocVi()));

        if (danhSachLopHocPhan.isEmpty()) {
            themHoatDongTrong("Hiện chưa có lớp học phần đang hoạt động được phân công.");
        } else {
            danhSachLopHocPhan.forEach(muc -> themHoatDong(
                    "Lớp " + muc.getMaLop() + " · " + muc.getMaMon(),
                    "Học kỳ " + muc.getHocKy() + " · Sĩ số "
                            + giaTriHoacGach(muc.getSiSoHienTai() == null
                            ? null : muc.getSiSoHienTai().toString()) + "/"
                            + giaTriHoacGach(muc.getSiSoToiDa() == null
                            ? null : muc.getSiSoToiDa().toString()),
                    "Đang hoạt động"));
        }
    }

    private void taiTongQuanQuanTriVien(Admin quanTriVien) {
        lblTomTatVaiTro.setText("Tình hình tài khoản và môn học trong hệ thống.");
        lblTieuDeHoSo.setText("Thông tin quản trị viên");
        lblTieuDeHoatDong.setText("Môn học đang hoạt động");
        lblMoTaHoatDong.setText("Danh sách môn học đang được sử dụng.");

        List<Users> danhSachTaiKhoan = dichVuQuanTri.layDanhSachTaiKhoan(quanTriVien);
        long soSinhVienHoatDong = danhSachTaiKhoan.stream()
                .filter(SinhVien.class::isInstance)
                .filter(nguoiDung -> "HOAT_DONG".equals(nguoiDung.getTrangThai())).count();
        long soGiangVienHoatDong = danhSachTaiKhoan.stream()
                .filter(GiangVien.class::isInstance)
                .filter(nguoiDung -> "HOAT_DONG".equals(nguoiDung.getTrangThai())).count();
        List<MonHoc> danhSachMonHocHoatDong = dichVuQuanTri.layDanhSachMonHoc(quanTriVien).stream()
                .filter(monHoc -> "HOAT_DONG".equals(monHoc.getTrangThai()))
                .toList();

        themChiSo("SINH VIÊN HOẠT ĐỘNG", Long.toString(soSinhVienHoatDong), "SINH VIÊN", "overview-icon-blue");
        themChiSo("GIẢNG VIÊN HOẠT ĐỘNG", Long.toString(soGiangVienHoatDong), "GIẢNG VIÊN", "overview-icon-violet");
        themChiSo("MÔN HỌC HOẠT ĐỘNG", Integer.toString(danhSachMonHocHoatDong.size()), "MH", "overview-icon-green");

        themChiTietHoSo("Email", giaTriHoacGach(quanTriVien.getEmail()));
        themChiTietHoSo("Tài khoản", giaTriHoacGach(quanTriVien.getMaSo()));
        themChiTietHoSo("Họ tên", giaTriHoacGach(quanTriVien.getHoTen()));
        themChiTietHoSo("Trạng thái", "Đang hoạt động");

        if (danhSachMonHocHoatDong.isEmpty()) {
            themHoatDongTrong("Hiện chưa có môn học đang hoạt động.");
        } else {
            danhSachMonHocHoatDong.stream().limit(8).forEach(monHoc -> themHoatDong(
                    giaTriHoacGach(monHoc.getTenMon()),
                    giaTriHoacGach(monHoc.getMaMon()) + " · "
                            + giaTriHoacGach(monHoc.getSoTinChi() == null
                            ? null : monHoc.getSoTinChi() + " tín chỉ"),
                    "Đang hoạt động"));
        }
    }

    private void themChiSo(String tieuDe, String giaTri, String bieuTuong, String lopBieuTuong) {
        Label lblTieuDe = new Label(tieuDe);
        lblTieuDe.getStyleClass().add("overview-metric-label");
        Label lblBieuTuong = new Label(bieuTuong);
        lblBieuTuong.getStyleClass().add("overview-metric-icon-label");
        VBox hopBieuTuong = new VBox(lblBieuTuong);
        hopBieuTuong.getStyleClass().addAll("overview-metric-icon", lopBieuTuong);
        HBox hangTieuDe = new HBox(lblTieuDe, new Region(), hopBieuTuong);
        hangTieuDe.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(hangTieuDe.getChildren().get(1), Priority.ALWAYS);

        Label lblGiaTri = new Label(giaTri);
        lblGiaTri.getStyleClass().add("overview-metric-value");
        VBox theThongKe = new VBox(14, hangTieuDe, lblGiaTri);
        theThongKe.getStyleClass().add("overview-metric-card");
        HBox.setHgrow(theThongKe, Priority.ALWAYS);
        cacTheThongKe.getChildren().add(theThongKe);
    }

    private void themChiTietHoSo(String nhan, String giaTri) {
        HBox hang = new HBox(12);
        Label khoa = new Label(nhan);
        khoa.getStyleClass().add("overview-detail-label");
        Label chiTiet = new Label(giaTri);
        chiTiet.getStyleClass().add("overview-detail-value");
        chiTiet.setWrapText(true);
        hang.getChildren().addAll(khoa, new Region(), chiTiet);
        HBox.setHgrow(hang.getChildren().get(1), Priority.ALWAYS);
        chiTietHoSo.getChildren().add(hang);
    }

    private void themHoatDong(String tieuDe, String chiTiet, String nhanHuyHieu) {
        Label lblTieuDe = new Label(giaTriHoacGach(tieuDe));
        lblTieuDe.getStyleClass().add("overview-activity-title");
        Label lblChiTiet = new Label(chiTiet);
        lblChiTiet.getStyleClass().add("overview-activity-detail");
        lblChiTiet.setWrapText(true);
        VBox vanBan = new VBox(5, lblTieuDe, lblChiTiet);
        Label huyHieu = new Label(nhanHuyHieu);
        huyHieu.getStyleClass().add("overview-activity-badge");
        huyHieu.setWrapText(true);
        HBox hang = new HBox(14, vanBan, huyHieu);
        hang.getStyleClass().add("overview-activity-row");
        hang.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(vanBan, Priority.ALWAYS);
        danhSachHoatDong.getChildren().add(hang);
    }

    private void themHoatDongTrong(String thongBao) {
        Label noiDungTrong = new Label(thongBao);
        noiDungTrong.getStyleClass().add("section-subtitle");
        noiDungTrong.setWrapText(true);
        danhSachHoatDong.getChildren().add(noiDungTrong);
    }

    private String nhanVaiTro(Users nguoiDung) {
        if (nguoiDung instanceof SinhVien) return "SINH VIÊN";
        if (nguoiDung instanceof GiangVien) return "GIẢNG VIÊN";
        if (nguoiDung instanceof Admin) return "QUẢN TRỊ VIÊN";
        return giaTriHoacGach(nguoiDung.getVaiTro());
    }

    private String giaTriHoacGach(String giaTri) {
        return giaTri == null || giaTri.isBlank() ? "—" : giaTri;
    }

    private void hienThiLoi(String thongBao) {
        lblTrangThai.setText(thongBao);
        lblTrangThai.setVisible(true);
        lblTrangThai.setManaged(true);
    }
}
