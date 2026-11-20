package Controllers;

import Model.BangDiemSinhVien;
import Model.GiangVien;
import Model.LopHocPhan;
import Model.ThanhPhanDanhGia;
import Model.Users;
import DAO.ThanhPhanDanhGiaDAO;
import Services.DiemService;
import Services.ServiceException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GiangVienController {
    private final DiemService dichVuDiem = new DiemService();
    private final ThanhPhanDanhGiaDAO daoThanhPhanDanhGia = new ThanhPhanDanhGiaDAO();
    private final DecimalFormat dinhDangDiem = new DecimalFormat("0.##");

    @FXML private Label lblTrangThai;
    @FXML private TableView<LopHocPhan> bangLopHoc;
    @FXML private TableColumn<LopHocPhan, String> cotMaLopHoc;
    @FXML private TableColumn<LopHocPhan, String> cotMaMonHoc;
    @FXML private TableColumn<LopHocPhan, String> cotHocKy;
    @FXML private TableColumn<LopHocPhan, String> cotSiSo;
    @FXML private TableView<BangDiemSinhVien> bangSinhVien;
    @FXML private TableColumn<BangDiemSinhVien, String> cotMaSinhVien;
    @FXML private TableColumn<BangDiemSinhVien, String> cotTenSinhVien;
    @FXML private TableColumn<BangDiemSinhVien, String> cotDiemTongKet;
    @FXML private ComboBox<ThanhPhanDanhGia> cmbThanhPhanDanhGia;
    @FXML private TextField txtDiem;

    private GiangVien giangVien;

    @FXML
    private void initialize() {
        cotMaLopHoc.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(duLieu.getValue().getMaLop()));
        cotMaMonHoc.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(duLieu.getValue().getMaMon()));
        cotHocKy.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(duLieu.getValue().getHocKy()));
        cotSiSo.setCellValueFactory(duLieu -> new ReadOnlyStringWrapper(
                duLieu.getValue().getSiSoHienTai() + "/" + duLieu.getValue().getSiSoToiDa()));
        cotMaSinhVien.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(duLieu.getValue().getMaSinhVien()));
        cotTenSinhVien.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(duLieu.getValue().getHoTen()));
        cotDiemTongKet.setCellValueFactory(duLieu ->
                new ReadOnlyStringWrapper(dinhDangDiemSo(duLieu.getValue().getDiemTongKet())));

        cmbThanhPhanDanhGia.setConverter(new StringConverter<>() {
            @Override
            public String toString(ThanhPhanDanhGia thanhPhan) {
                if (thanhPhan == null) return "";
                return thanhPhan.getTenThanhPhan() + " (" + dinhDangDiem.format(thanhPhan.getTrongSo() * 100) + "%)";
            }

            @Override
            public ThanhPhanDanhGia fromString(String giaTri) {
                return null;
            }
        });
        bangLopHoc.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriTruoc, duocChon) -> taiBangDiem(duocChon, null));
        bangSinhVien.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriTruoc, duocChon) -> capNhatTruongNhapDiem());
        cmbThanhPhanDanhGia.valueProperty().addListener(
                (thuocTinh, giaTriTruoc, duocChon) -> capNhatTruongNhapDiem());
    }

    public void ganGiangVien(Users nguoiDung) {
        if (!(nguoiDung instanceof GiangVien giangVienXacThuc)
                || !"HOAT_DONG".equals(giangVienXacThuc.getTrangThai())
                || !"GIAOVIEN".equalsIgnoreCase(giangVienXacThuc.getVaiTro())
                || giangVienXacThuc.getMaGiangVien() == null || giangVienXacThuc.getMaGiangVien().isBlank()) {
            hienThiTrangThai("Không xác định được tài khoản giảng viên. Vui lòng đăng nhập lại.", true);
            return;
        }
        giangVien = giangVienXacThuc;
        taiDanhSachLop();
    }

    @FXML
    private void taiLaiDanhSachLop() {
        taiDanhSachLop();
    }

    @FXML
    private void luuDiem() {
        LopHocPhan lopDuocChon = bangLopHoc.getSelectionModel().getSelectedItem();
        BangDiemSinhVien sinhVienDuocChon = bangSinhVien.getSelectionModel().getSelectedItem();
        ThanhPhanDanhGia thanhPhanDuocChon = cmbThanhPhanDanhGia.getValue();
        if (lopDuocChon == null || sinhVienDuocChon == null || thanhPhanDuocChon == null) {
            hienThiTrangThai("Chọn lớp, sinh viên và thành phần đánh giá trước khi lưu điểm.", true);
            return;
        }

        float diem;
        try {
            diem = Float.parseFloat(txtDiem.getText().trim().replace(',', '.'));
        } catch (NumberFormatException loiPhatSinh) {
            hienThiTrangThai("Điểm phải là số từ 0 đến 10.", true);
            return;
        }
        if (!Float.isFinite(diem) || diem < 0 || diem > 10) {
            hienThiTrangThai("Điểm phải nằm trong khoảng từ 0 đến 10.", true);
            return;
        }

        try {
            boolean daLuu = dichVuDiem.nhapDiemVaTinhTongKet(giangVien, sinhVienDuocChon.getMaSinhVien(),
                    lopDuocChon.getMaLop(), thanhPhanDuocChon, diem);
            if (!daLuu) {
                hienThiTrangThai("Không thể lưu điểm. Hãy kiểm tra sinh viên và thành phần đánh giá.", true);
                return;
            }
            String maSinhVien = sinhVienDuocChon.getMaSinhVien();
            txtDiem.clear();
            taiBangDiem(lopDuocChon, maSinhVien);
            hienThiTrangThai("Đã lưu điểm thành phần.", false);
        } catch (ServiceException loiPhatSinh) {
            hienThiTrangThai("Không thể lưu điểm. Vui lòng thử lại sau.", true);
        }
    }

    private void taiDanhSachLop() {
        if (giangVien == null) return;
        bangLopHoc.getItems().clear();
        bangSinhVien.getItems().clear();
        cmbThanhPhanDanhGia.getItems().clear();
        xoaCotThanhPhan();
        try {
            List<LopHocPhan> danhSachLop = dichVuDiem.layLopPhuTrach(giangVien);
            bangLopHoc.setItems(FXCollections.observableArrayList(danhSachLop));
            if (danhSachLop.isEmpty()) {
                cmbThanhPhanDanhGia.getItems().clear();
                bangSinhVien.getItems().clear();
                xoaCotThanhPhan();
                hienThiTrangThai("Bạn hiện chưa được phân công lớp học phần nào.", false);
                return;
            }
            bangLopHoc.getSelectionModel().selectFirst();
        } catch (ServiceException loiPhatSinh) {
            hienThiTrangThai("Không thể tải danh sách lớp phụ trách. Hãy kiểm tra kết nối rồi thử lại.", true);
        }
    }

    private void taiBangDiem(LopHocPhan lopHoc, String maSinhVienCanChon) {
        bangSinhVien.getItems().clear();
        xoaCotThanhPhan();
        ThanhPhanDanhGia thanhPhanDaChonTruoc = cmbThanhPhanDanhGia.getValue();
        cmbThanhPhanDanhGia.getItems().clear();
        if (lopHoc == null || giangVien == null) return;

        try {
            List<ThanhPhanDanhGia> cacThanhPhan = daoThanhPhanDanhGia.layTheoMon(lopHoc.getMaMon())
                    .stream().filter(ThanhPhanDanhGia::getTrangThai)
                    .sorted((thanhPhanDau, thanhPhanSau) -> Integer.compare(thanhPhanDau.getThuTu(), thanhPhanSau.getThuTu()))
                    .toList();
            cmbThanhPhanDanhGia.setItems(FXCollections.observableArrayList(cacThanhPhan));
            if (thanhPhanDaChonTruoc != null) {
                cacThanhPhan.stream()
                        .filter(thanhPhan -> thanhPhan.getMaThanhPhan() == thanhPhanDaChonTruoc.getMaThanhPhan())
                        .findFirst().ifPresent(cmbThanhPhanDanhGia.getSelectionModel()::select);
            }
            if (cmbThanhPhanDanhGia.getValue() == null && !cacThanhPhan.isEmpty()) {
                cmbThanhPhanDanhGia.getSelectionModel().selectFirst();
            }
            themCotThanhPhan(cacThanhPhan);

            List<BangDiemSinhVien> danhSachSinhVien = dichVuDiem.layBangDiem(giangVien, lopHoc.getMaLop());
            bangSinhVien.setItems(FXCollections.observableArrayList(danhSachSinhVien));
            if (maSinhVienCanChon != null) {
                danhSachSinhVien.stream().filter(sinhVien -> maSinhVienCanChon.equals(sinhVien.getMaSinhVien()))
                        .findFirst().ifPresent(sinhVien -> bangSinhVien.getSelectionModel().select(sinhVien));
            }
            if (cacThanhPhan.isEmpty()) {
                hienThiTrangThai("Môn học chưa có thành phần đánh giá đang hoạt động.", true);
            } else if (danhSachSinhVien.isEmpty()) {
                hienThiTrangThai("Lớp chưa có sinh viên đăng ký.", false);
            } else {
                hienThiTrangThai("Đang xem bảng điểm " + lopHoc.getMaLop() + ".", false);
            }
        } catch (ServiceException | java.sql.SQLException loiPhatSinh) {
            hienThiTrangThai("Không thể tải bảng điểm lớp học phần. Vui lòng thử lại.", true);
        }
    }

    private void themCotThanhPhan(List<ThanhPhanDanhGia> cacThanhPhan) {
        int viTriChen = bangSinhVien.getColumns().indexOf(cotDiemTongKet);
        for (ThanhPhanDanhGia thanhPhan : cacThanhPhan) {
            TableColumn<BangDiemSinhVien, String> cot = new TableColumn<>(thanhPhan.getTenThanhPhan());
            cot.setPrefWidth(110);
            cot.setCellValueFactory(duLieu -> new ReadOnlyStringWrapper(
                    dinhDangDiemSo(duLieu.getValue().getDiemThanhPhan(thanhPhan.getMaThanhPhan()))));
            bangSinhVien.getColumns().add(viTriChen++, cot);
        }
    }

    private void xoaCotThanhPhan() {
        List<TableColumn<BangDiemSinhVien, ?>> cacCotDong = new ArrayList<>(
                bangSinhVien.getColumns().subList(2, bangSinhVien.getColumns().size() - 1));
        bangSinhVien.getColumns().removeAll(cacCotDong);
    }

    private String dinhDangDiemSo(Float diem) {
        return diem == null ? "—" : dinhDangDiem.format(diem);
    }

    private void capNhatTruongNhapDiem() {
        BangDiemSinhVien sinhVien = bangSinhVien.getSelectionModel().getSelectedItem();
        ThanhPhanDanhGia thanhPhan = cmbThanhPhanDanhGia.getValue();
        Float diem = sinhVien == null || thanhPhan == null
                ? null : sinhVien.getDiemThanhPhan(thanhPhan.getMaThanhPhan());
        txtDiem.setText(diem == null ? "" : dinhDangDiemSo(diem));
    }

    private void hienThiTrangThai(String thongBao, boolean loi) {
        lblTrangThai.setText(thongBao);
        lblTrangThai.getStyleClass().removeAll("teacher-status-error", "teacher-status-info");
        lblTrangThai.getStyleClass().add(loi ? "teacher-status-error" : "teacher-status-info");
    }
}
