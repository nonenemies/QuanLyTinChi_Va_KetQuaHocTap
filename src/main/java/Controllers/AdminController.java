package Controllers;

import Model.Admin;
import Model.GiangVien;
import Model.MonHoc;
import Model.Nganh;
import Model.SinhVien;
import Model.Users;
import Services.AdminService;
import Services.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

public class AdminController {
    private static final String VAI_TRO_SINH_VIEN = "SINHVIEN";
    private static final String VAI_TRO_GIANG_VIEN = "GIAOVIEN";

    private final AdminService dichVuQuanTri = new AdminService();
    private final ObservableList<Users> danhSachTaiKhoan = FXCollections.observableArrayList();
    private final FilteredList<Users> taiKhoanDaLoc = new FilteredList<>(danhSachTaiKhoan, muc -> true);
    private final ObservableList<MonHoc> danhSachMonHoc = FXCollections.observableArrayList();
    private final FilteredList<MonHoc> monHocDaLoc = new FilteredList<>(danhSachMonHoc, muc -> true);

    @FXML private Label lblTrangThai;
    @FXML private ScrollPane khungQuanLyTaiKhoan;
    @FXML private ScrollPane khungQuanLyMonHoc;
    @FXML private TableView<Users> bangTaiKhoan;
    @FXML private TableColumn<Users, Integer> cotMaNguoiDung;
    @FXML private TableColumn<Users, String> cotMaTaiKhoan;
    @FXML private TableColumn<Users, String> cotTenNguoiDung;
    @FXML private TableColumn<Users, String> cotEmailNguoiDung;
    @FXML private TableColumn<Users, String> cotVaiTroNguoiDung;
    @FXML private TableColumn<Users, String> cotNganhNguoiDung;
    @FXML private TableColumn<Users, String> cotTrangThaiNguoiDung;
    @FXML private TextField txtTimTaiKhoan;
    @FXML private TextField txtMaTaiKhoan;
    @FXML private TextField txtTenNguoiDung;
    @FXML private TextField txtEmailNguoiDung;
    @FXML private TextField txtMatKhauNguoiDung;
    @FXML private TextField txtNganhNguoiDung;
    @FXML private TextField txtLopSinhVien;
    @FXML private TextField txtKhoaHocSinhVien;
    @FXML private TextField txtHeDaoTaoSinhVien;
    @FXML private GridPane cacTruongSinhVien;
    @FXML private DatePicker dpNgaySinhSinhVien;
    @FXML private ComboBox<String> cmbGioiTinhSinhVien;
    @FXML private TextField txtChuyenNganhGiangVien;
    @FXML private TextField txtHocViGiangVien;
    @FXML private GridPane cacTruongGiangVien;
    @FXML private ComboBox<String> cmbTrangThaiNguoiDung;

    @FXML private TableView<MonHoc> bangMonHoc;
    @FXML private TableColumn<MonHoc, String> cotMaMonHoc;
    @FXML private TableColumn<MonHoc, String> cotTenMonHoc;
    @FXML private TableColumn<MonHoc, Integer> cotSoTinChi;
    @FXML private TableColumn<MonHoc, String> cotLoaiMonHoc;
    @FXML private TableColumn<MonHoc, String> cotTrangThaiMonHoc;
    @FXML private TextField txtTimMonHoc;
    @FXML private TextField txtMaMonHoc;
    @FXML private TextField txtTenMonHoc;
    @FXML private TextField txtSoTinChiMonHoc;
    @FXML private TextField txtLoaiMonHoc;
    @FXML private TextArea txtMoTaMonHoc;

    private Admin quanTriVien;
    private String vaiTroTaiKhoan;
    private Users taiKhoanDangChon;
    private MonHoc monHocDangChon;

    @FXML
    private void initialize() {
        cauHinhBangTaiKhoan();
        cauHinhBangMonHoc();
        cmbTrangThaiNguoiDung.setItems(FXCollections.observableArrayList("HOAT_DONG", "NGUNG_HOAT_DONG"));
        cmbTrangThaiNguoiDung.getSelectionModel().select("HOAT_DONG");
        cmbGioiTinhSinhVien.setItems(FXCollections.observableArrayList("Không chỉ định", "Nam", "Nữ"));
        cmbGioiTinhSinhVien.getSelectionModel().selectFirst();
        bangTaiKhoan.setItems(taiKhoanDaLoc);
        bangMonHoc.setItems(monHocDaLoc);
        txtTimTaiKhoan.textProperty().addListener((thuocTinh, giaTriCu, giaTriMoi) -> locTaiKhoan());
        txtTimMonHoc.textProperty().addListener((thuocTinh, giaTriCu, giaTriMoi) -> locMonHoc());
        bangTaiKhoan.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriCu, duocChon) -> dienBieuMauTaiKhoan(duocChon));
        bangMonHoc.getSelectionModel().selectedItemProperty().addListener(
                (thuocTinh, giaTriCu, duocChon) -> dienBieuMauMonHoc(duocChon));
        capNhatTruongTheoVaiTro(vaiTroTaiKhoan);
    }

    public void ganQuanTriVien(Users nguoiDung, String mucQuanLy) {
        if (!(nguoiDung instanceof Admin quanTriVienXacThuc)
                || !"HOAT_DONG".equals(quanTriVienXacThuc.getTrangThai())) {
            datTrangThai("Không xác định được tài khoản admin đang hoạt động.", true);
            return;
        }
        quanTriVien = quanTriVienXacThuc;
        boolean dangQuanLyMonHoc = "admin-courses".equals(mucQuanLy);
        khungQuanLyTaiKhoan.setVisible(!dangQuanLyMonHoc);
        khungQuanLyTaiKhoan.setManaged(!dangQuanLyMonHoc);
        khungQuanLyMonHoc.setVisible(dangQuanLyMonHoc);
        khungQuanLyMonHoc.setManaged(dangQuanLyMonHoc);
        if (!dangQuanLyMonHoc) {
            vaiTroTaiKhoan = "admin-lecturers".equals(mucQuanLy)
                    ? VAI_TRO_GIANG_VIEN : VAI_TRO_SINH_VIEN;
            capNhatTruongTheoVaiTro(vaiTroTaiKhoan);
        }
        taiToanBoDuLieu();
    }

    @FXML
    private void taiLaiDuLieu() {
        if (khungQuanLyTaiKhoan.isVisible()) xoaTrangBieuMauTaiKhoan();
        else xoaTrangBieuMauMonHoc();
        taiToanBoDuLieu();
    }

    @FXML
    private void xoaTrangBieuMauTaiKhoan() {
        bangTaiKhoan.getSelectionModel().clearSelection();
        taiKhoanDangChon = null;
        txtMaTaiKhoan.clear();
        txtMaTaiKhoan.setDisable(false);
        txtTenNguoiDung.clear();
        txtEmailNguoiDung.clear();
        txtMatKhauNguoiDung.clear();
        txtMatKhauNguoiDung.setPromptText("Mật khẩu (bắt buộc khi tạo)");
        txtNganhNguoiDung.clear();
        txtLopSinhVien.clear();
        txtKhoaHocSinhVien.clear();
        txtHeDaoTaoSinhVien.clear();
        dpNgaySinhSinhVien.setValue(null);
        cmbGioiTinhSinhVien.getSelectionModel().selectFirst();
        txtChuyenNganhGiangVien.clear();
        txtHocViGiangVien.clear();
        cmbTrangThaiNguoiDung.getSelectionModel().select("HOAT_DONG");
    }

    @FXML
    private void luuTaiKhoan() {
        if (quanTriVien == null) return;
        try {
            Users taiKhoan = taoTaiKhoanTuBieuMau();
            boolean daLuu;
            if (taiKhoanDangChon == null) {
                daLuu = dichVuQuanTri.taoTaiKhoan(quanTriVien, taiKhoan);
            } else {
                taiKhoan.setUserID(taiKhoanDangChon.getUserID());
                if (taiKhoan.getPassword().isBlank()) {
                    taiKhoan.setPassword(taiKhoanDangChon.getPassword());
                }
                daLuu = dichVuQuanTri.capNhatTaiKhoan(quanTriVien, taiKhoan);
            }
            if (!daLuu) {
                datTrangThai("Không thể lưu tài khoản. Dữ liệu có thể đã thay đổi.", true);
                return;
            }
            xoaTrangBieuMauTaiKhoan();
            taiDanhSachTaiKhoan();
            datTrangThai("Đã lưu hồ sơ và tài khoản.", false);
        } catch (IllegalArgumentException | ServiceException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        }
    }

    @FXML
    private void ngungTaiKhoan() {
        if (quanTriVien == null || taiKhoanDangChon == null) {
            datTrangThai("Chọn một sinh viên hoặc giảng viên cần ngừng tài khoản.", true);
            return;
        }
        if (!xacNhan("Ngừng tài khoản " + taiKhoanDangChon.getMaSo() + "?",
                "Tài khoản sẽ không thể đăng nhập. Hồ sơ và dữ liệu học vụ được giữ lại.")) return;
        try {
            if (!dichVuQuanTri.ngungTaiKhoan(quanTriVien, taiKhoanDangChon.getUserID())) {
                datTrangThai("Tài khoản không còn hoạt động hoặc đã thay đổi.", true);
                return;
            }
            xoaTrangBieuMauTaiKhoan();
            taiDanhSachTaiKhoan();
            datTrangThai("Đã ngừng hoạt động tài khoản.", false);
        } catch (ServiceException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        }
    }

    @FXML
    private void xoaTrangBieuMauMonHoc() {
        bangMonHoc.getSelectionModel().clearSelection();
        monHocDangChon = null;
        txtMaMonHoc.clear();
        txtMaMonHoc.setDisable(false);
        txtTenMonHoc.clear();
        txtSoTinChiMonHoc.clear();
        txtLoaiMonHoc.clear();
        txtMoTaMonHoc.clear();
    }

    @FXML
    private void luuMonHoc() {
        try {
            String maSo = batBuoc(txtMaMonHoc.getText(), "Mã môn học");
            String hoTen = batBuoc(txtTenMonHoc.getText(), "Tên môn học");
            int soTinChi = docSoNguyenDuong(txtSoTinChiMonHoc.getText(), "Số tín chỉ");
            String loai = batBuoc(txtLoaiMonHoc.getText(), "Loại môn");
            if (maSo.length() > 20 || hoTen.length() > 200 || loai.length() > 20) {
                throw new IllegalArgumentException("Mã môn, tên môn hoặc loại môn vượt quá giới hạn.");
            }
            MonHoc monHoc = new MonHoc(maSo, hoTen, soTinChi, loai, tuyChon(txtMoTaMonHoc.getText()),
                    monHocDangChon == null ? "HOAT_DONG" : monHocDangChon.getTrangThai());
            boolean daLuu = monHocDangChon == null
                    ? dichVuQuanTri.taoMonHoc(quanTriVien, monHoc) : dichVuQuanTri.capNhatMonHoc(quanTriVien, monHoc);
            if (!daLuu) {
                datTrangThai("Không thể lưu môn học.", true);
                return;
            }
            xoaTrangBieuMauMonHoc();
            taiDanhSachMonHoc();
            datTrangThai("Đã lưu môn học.", false);
        } catch (IllegalArgumentException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        } catch (ServiceException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        }
    }

    @FXML
    private void ngungMonHoc() {
        if (monHocDangChon == null) {
            datTrangThai("Chọn môn học cần ngừng hoạt động.", true);
            return;
        }
        if (!xacNhan("Ngừng môn " + monHocDangChon.getMaMon() + "?", "Môn học sẽ được giữ lại để bảo toàn dữ liệu lịch sử.")) return;
        try {
            if (!dichVuQuanTri.ngungMonHoc(quanTriVien, monHocDangChon.getMaMon())) {
                datTrangThai("Môn học không còn hoạt động hoặc đã thay đổi.", true);
                return;
            }
            xoaTrangBieuMauMonHoc();
            taiDanhSachMonHoc();
            datTrangThai("Đã ngừng hoạt động môn học.", false);
        } catch (ServiceException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        }
    }

    private void cauHinhBangTaiKhoan() {
        cotMaNguoiDung.setCellValueFactory(new PropertyValueFactory<>("userID"));
        cotMaTaiKhoan.setCellValueFactory(new PropertyValueFactory<>("maSo"));
        cotTenNguoiDung.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cotEmailNguoiDung.setCellValueFactory(new PropertyValueFactory<>("email"));
        cotVaiTroNguoiDung.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));
        cotNganhNguoiDung.setCellValueFactory(oDuLieu -> new ReadOnlyStringWrapper(
                oDuLieu.getValue() instanceof SinhVien sinhVien && sinhVien.getNganh() != null
                        ? sinhVien.getNganh().getMaNganh() : ""));
        cotTrangThaiNguoiDung.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void cauHinhBangMonHoc() {
        cotMaMonHoc.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        cotTenMonHoc.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        cotSoTinChi.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        cotLoaiMonHoc.setCellValueFactory(new PropertyValueFactory<>("loaiMon"));
        cotTrangThaiMonHoc.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void taiToanBoDuLieu() {
        if (quanTriVien == null) return;
        if (khungQuanLyTaiKhoan.isVisible()) taiDanhSachTaiKhoan();
        else taiDanhSachMonHoc();
    }

    private void taiDanhSachTaiKhoan() {
        try {
            danhSachTaiKhoan.setAll(dichVuQuanTri.layDanhSachTaiKhoan(quanTriVien));
            locTaiKhoan();
        } catch (ServiceException loiPhatSinh) {
            datTrangThai("Không thể tải tài khoản. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", true);
        }
    }

    private void taiDanhSachMonHoc() {
        try {
            danhSachMonHoc.setAll(dichVuQuanTri.layDanhSachMonHoc(quanTriVien));
            locMonHoc();
        } catch (ServiceException loiPhatSinh) {
            datTrangThai(loiPhatSinh.getMessage(), true);
        }
    }

    private void locTaiKhoan() {
        String tuKhoa = chuanHoa(txtTimTaiKhoan.getText());
        taiKhoanDaLoc.setPredicate(taiKhoan -> (vaiTroTaiKhoan == null
                || vaiTroTaiKhoan.equalsIgnoreCase(taiKhoan.getVaiTro()))
                && (taiKhoan.getMaSo() + " " + taiKhoan.getHoTen() + " "
                + taiKhoan.getEmail() + " " + taiKhoan.getVaiTro() + " " + nganhTaiKhoan(taiKhoan))
                .toLowerCase(Locale.ROOT).contains(tuKhoa));
    }

    private String nganhTaiKhoan(Users taiKhoan) {
        return taiKhoan instanceof SinhVien sinhVien && sinhVien.getNganh() != null
                ? tuyChon(sinhVien.getNganh().getMaNganh()) : "";
    }

    private void locMonHoc() {
        String tuKhoa = chuanHoa(txtTimMonHoc.getText());
        monHocDaLoc.setPredicate(monHoc -> (monHoc.getMaMon() + " " + monHoc.getTenMon() + " "
                + monHoc.getLoaiMon()).toLowerCase(Locale.ROOT).contains(tuKhoa));
    }

    private void dienBieuMauTaiKhoan(Users duocChon) {
        taiKhoanDangChon = duocChon;
        if (duocChon == null) return;
        vaiTroTaiKhoan = duocChon instanceof SinhVien ? VAI_TRO_SINH_VIEN : VAI_TRO_GIANG_VIEN;
        txtMaTaiKhoan.setText(duocChon.getMaSo());
        txtMaTaiKhoan.setDisable(true);
        txtTenNguoiDung.setText(duocChon.getHoTen());
        txtEmailNguoiDung.setText(duocChon.getEmail());
        txtMatKhauNguoiDung.clear();
        txtMatKhauNguoiDung.setPromptText("Để trống để giữ mật khẩu");
        txtNganhNguoiDung.setText(duocChon instanceof SinhVien sinhVien && sinhVien.getNganh() != null
                ? tuyChon(sinhVien.getNganh().getMaNganh()) : "");
        cmbTrangThaiNguoiDung.setValue(duocChon.getTrangThai());
        if (duocChon instanceof SinhVien sinhVien) {
            txtLopSinhVien.setText(sinhVien.getLop());
            txtKhoaHocSinhVien.setText(sinhVien.getKhoaHoc() == null ? "" : sinhVien.getKhoaHoc().toString());
            txtHeDaoTaoSinhVien.setText(sinhVien.getHeDaoTao());
            dpNgaySinhSinhVien.setValue(sinhVien.getNgaySinh() == null ? null
                    : new Date(sinhVien.getNgaySinh().getTime()).toLocalDate());
            cmbGioiTinhSinhVien.setValue(sinhVien.getGioiTinh() == null ? "Không chỉ định"
                    : sinhVien.getGioiTinh());
        } else if (duocChon instanceof GiangVien giangVien) {
            txtChuyenNganhGiangVien.setText(giangVien.getChuyenNganh());
            txtHocViGiangVien.setText(giangVien.getHocVi());
        }
        capNhatTruongTheoVaiTro(vaiTroTaiKhoan);
    }

    private Users taoTaiKhoanTuBieuMau() {
        String vaiTro = vaiTroTaiKhoan;
        String maSo = batBuoc(txtMaTaiKhoan.getText(), "Mã tài khoản");
        String hoTen = batBuoc(txtTenNguoiDung.getText(), "Họ tên");
        String diaChiEmail = batBuoc(txtEmailNguoiDung.getText(), "Email");
        String matKhau = txtMatKhauNguoiDung.getText() == null ? "" : txtMatKhauNguoiDung.getText().trim();
        if (taiKhoanDangChon == null && matKhau.isBlank()) {
            throw new IllegalArgumentException("Cần nhập mật khẩu cho tài khoản mới.");
        }
        if (taiKhoanDangChon != null && matKhau.isBlank()) matKhau = taiKhoanDangChon.getPassword();
        String nganh = tuyChon(txtNganhNguoiDung.getText());
        String trangThai = cmbTrangThaiNguoiDung.getValue();
        if (vaiTro == null || trangThai == null) throw new IllegalArgumentException("Chọn vai trò và trạng thái tài khoản.");
        if (VAI_TRO_SINH_VIEN.equals(vaiTro)) {
            Integer khoaHoc = txtKhoaHocSinhVien.getText().isBlank() ? null
                    : docSoNguyenDuong(txtKhoaHocSinhVien.getText(), "Khóa học");
            String chuoiGioiTinh = cmbGioiTinhSinhVien.getValue();
            String gioiTinh = "Không chỉ định".equals(chuoiGioiTinh) ? null : chuoiGioiTinh;
            LocalDate ngaySinh = dpNgaySinhSinhVien.getValue();
            return new SinhVien(taiKhoanDangChon == null ? null : taiKhoanDangChon.getUserID(),
                    hoTen, diaChiEmail, matKhau, VAI_TRO_SINH_VIEN, maSo,
                    nganh == null ? null : new Nganh(nganh, null), null, trangThai,
                    maSo, ngaySinh == null ? null : Date.valueOf(ngaySinh), gioiTinh,
                    tuyChon(txtLopSinhVien.getText()), khoaHoc, tuyChon(txtHeDaoTaoSinhVien.getText()));
        }
        if (VAI_TRO_GIANG_VIEN.equals(vaiTro)) {
            return new GiangVien(taiKhoanDangChon == null ? null : taiKhoanDangChon.getUserID(),
                    hoTen, diaChiEmail, matKhau, VAI_TRO_GIANG_VIEN, maSo, null, trangThai,
                    maSo, tuyChon(txtChuyenNganhGiangVien.getText()), tuyChon(txtHocViGiangVien.getText()));
        }
        throw new IllegalArgumentException("Vai trò tài khoản không hợp lệ.");
    }

    private void dienBieuMauMonHoc(MonHoc duocChon) {
        monHocDangChon = duocChon;
        if (duocChon == null) return;
        txtMaMonHoc.setText(duocChon.getMaMon());
        txtMaMonHoc.setDisable(true);
        txtTenMonHoc.setText(duocChon.getTenMon());
        txtSoTinChiMonHoc.setText(duocChon.getSoTinChi().toString());
        txtLoaiMonHoc.setText(duocChon.getLoaiMon());
        txtMoTaMonHoc.setText(duocChon.getMoTa());
    }

    private void capNhatTruongTheoVaiTro(String vaiTro) {
        boolean sinhVien = VAI_TRO_SINH_VIEN.equals(vaiTro);
        boolean giangVien = VAI_TRO_GIANG_VIEN.equals(vaiTro);
        cacTruongSinhVien.setManaged(sinhVien);
        cacTruongSinhVien.setVisible(sinhVien);
        cacTruongGiangVien.setManaged(giangVien);
        cacTruongGiangVien.setVisible(giangVien);
    }

    private boolean xacNhan(String tieuDe, String chiTiet) {
        Alert hopThoai = new Alert(Alert.AlertType.CONFIRMATION);
        hopThoai.setTitle("Xác nhận thao tác");
        hopThoai.setHeaderText(tieuDe);
        hopThoai.setContentText(chiTiet);
        Optional<ButtonType> luaChon = hopThoai.showAndWait();
        return luaChon.isPresent() && luaChon.get() == ButtonType.OK;
    }

    private String batBuoc(String giaTri, String truongDuLieu) {
        if (giaTri == null || giaTri.isBlank()) throw new IllegalArgumentException(truongDuLieu + " không được để trống.");
        return giaTri.trim();
    }

    private String tuyChon(String giaTri) {
        return giaTri == null || giaTri.isBlank() ? null : giaTri.trim();
    }

    private int docSoNguyenDuong(String giaTri, String truongDuLieu) {
        try {
            int ketQua = Integer.parseInt(giaTri.trim());
            if (ketQua > 0) return ketQua;
        } catch (NumberFormatException duocBoQua) {
        }
        throw new IllegalArgumentException(truongDuLieu + " phải là số nguyên dương.");
    }

    private String chuanHoa(String vanBan) {
        return vanBan == null ? "" : vanBan.trim().toLowerCase(Locale.ROOT);
    }

    private void datTrangThai(String thongBao, boolean loi) {
        lblTrangThai.setText(thongBao);
        lblTrangThai.getStyleClass().removeAll("teacher-status-error", "teacher-status-info");
        lblTrangThai.getStyleClass().add(loi ? "teacher-status-error" : "teacher-status-info");
    }
}
