package Services;

import DAO.GiangVienDAO;
import DAO.MonHocDAO;
import DAO.NganhDAO;
import DAO.SinhVienDAO;
import DAO.UsersDAO;
import Model.Admin;
import Model.GiangVien;
import Model.MonHoc;
import Model.SinhVien;
import Model.Users;
import java.sql.SQLException;
import java.util.List;

/** Nghiệp vụ quản trị tài khoản sinh viên, giảng viên và môn học. */
public class AdminService {
    private final UsersDAO usersDAO = new UsersDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final GiangVienDAO giangVienDAO = new GiangVienDAO();
    private final MonHocDAO monHocDAO = new MonHocDAO();
    private final NganhDAO nganhDAO = new NganhDAO();

    public List<Users> layDanhSachTaiKhoan(Admin admin) {
        kiemTraAdminHienTai(admin);
        try {
            return usersDAO.layTatCa().stream()
                    .filter(user -> user instanceof SinhVien || user instanceof GiangVien)
                    .toList();
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách tài khoản.", e);
        }
    }

    public List<MonHoc> layDanhSachMonHoc(Admin admin) {
        kiemTraAdminHienTai(admin);
        try {
            return monHocDAO.layTatCaMonHoc();
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách môn học.", e);
        }
    }

    public boolean taoMonHoc(Admin admin, MonHoc monHoc) {
        kiemTraAdminHienTai(admin);
        try {
            return monHocDAO.themMonHoc(monHoc);
        } catch (SQLException e) {
            throw new ServiceException("Không thể thêm môn học. Hãy kiểm tra mã môn có bị trùng không.", e);
        }
    }

    public boolean capNhatMonHoc(Admin admin, MonHoc monHoc) {
        kiemTraAdminHienTai(admin);
        try {
            return monHocDAO.suaMonHoc(monHoc);
        } catch (SQLException e) {
            throw new ServiceException("Không thể cập nhật môn học.", e);
        }
    }

    public boolean ngungMonHoc(Admin admin, String maMon) {
        kiemTraAdminHienTai(admin);
        try {
            return monHocDAO.xoaMonHoc(maMon);
        } catch (SQLException e) {
            throw new ServiceException("Không thể ngừng hoạt động môn học.", e);
        }
    }

    public boolean taoTaiKhoan(Admin admin, Users taiKhoan) {
        kiemTraAdmin(admin);
        kiemTraHoSo(taiKhoan);
        try {
            kiemTraAdminTrongCoSoDuLieu(admin);
            kiemTraNganhTonTai(taiKhoan);
            int userID = usersDAO.them(taiKhoan);
            taiKhoan.setUserID(userID);
            return taiKhoan instanceof SinhVien sinhVien
                    ? sinhVienDAO.them(sinhVien)
                    : giangVienDAO.them((GiangVien) taiKhoan);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tạo tài khoản.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể tạo tài khoản.", e);
        }
    }

    public boolean capNhatTaiKhoan(Admin admin, Users taiKhoan) {
        kiemTraAdmin(admin);
        kiemTraHoSo(taiKhoan);
        if (taiKhoan.getUserID() == null || taiKhoan.getUserID() <= 0) {
            throw new ServiceException("Tài khoản cần cập nhật không hợp lệ.");
        }
        try {
            kiemTraAdminTrongCoSoDuLieu(admin);
            kiemTraNganhTonTai(taiKhoan);
            Users hienTai = usersDAO.layTheoID(taiKhoan.getUserID());
            if (hienTai == null || hienTai instanceof Admin
                    || hienTai.getClass() != taiKhoan.getClass()
                    || !usersDAO.capNhat(taiKhoan)) {
                return false;
            }
            return taiKhoan instanceof SinhVien sinhVien
                    ? sinhVienDAO.capNhat(sinhVien)
                    : giangVienDAO.capNhat((GiangVien) taiKhoan);
        } catch (SQLException e) {
            throw new ServiceException("Không thể cập nhật tài khoản.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể cập nhật tài khoản.", e);
        }
    }

    public boolean ngungTaiKhoan(Admin admin, int userID) {
        kiemTraAdmin(admin);
        if (userID <= 0 || admin.getUserID().equals(userID)) {
            throw new ServiceException("Không thể ngừng tài khoản admin đang đăng nhập.");
        }
        try {
            kiemTraAdminTrongCoSoDuLieu(admin);
            Users target = usersDAO.layTheoID(userID);
            if (target == null || target instanceof Admin
                    || !"HOAT_DONG".equals(target.getTrangThai())
                    || !usersDAO.ngungHoatDong(userID)) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            throw new ServiceException("Không thể ngừng tài khoản.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể ngừng tài khoản.", e);
        }
    }

    private void kiemTraAdminTrongCoSoDuLieu(Admin admin) throws SQLException {
        Users current = usersDAO.layTheoID(admin.getUserID());
        if (!(current instanceof Admin)
                || !"HOAT_DONG".equals(current.getTrangThai())) {
            throw new ServiceException("Tài khoản admin không còn hoạt động.");
        }
    }

    private void kiemTraAdmin(Admin admin) {
        if (admin == null || admin.getUserID() == null || admin.getUserID() <= 0
                || !"ADMIN".equalsIgnoreCase(admin.getVaiTro())
                || !"HOAT_DONG".equals(admin.getTrangThai())) {
            throw new ServiceException("Cần đăng nhập bằng tài khoản admin đang hoạt động.");
        }
    }

    private void kiemTraAdminHienTai(Admin admin) {
        kiemTraAdmin(admin);
        try {
            Users current = usersDAO.layTheoID(admin.getUserID());
            if (!(current instanceof Admin)
                    || !"HOAT_DONG".equals(current.getTrangThai())) {
                throw new ServiceException("Tài khoản admin không còn hoạt động.");
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể xác thực tài khoản admin.", e);
        }
    }

    private void kiemTraHoSo(Users taiKhoan) {
        if (taiKhoan == null || taiKhoan.getHoTen() == null || taiKhoan.getHoTen().isBlank()
                || taiKhoan.getHoTen().strip().length() > 100
                || taiKhoan.getEmail() == null || taiKhoan.getEmail().isBlank()
                || taiKhoan.getEmail().strip().length() > 100
                || taiKhoan.getPassword() == null || taiKhoan.getPassword().isBlank()
                || taiKhoan.getPassword().length() > 255
                || taiKhoan.getMaSo() == null || taiKhoan.getMaSo().isBlank()
                || taiKhoan.getMaSo().length() > 20
                || taiKhoan.getTrangThai() == null) {
            throw new ServiceException("Thông tin tài khoản không hợp lệ.");
        }
        if (taiKhoan instanceof SinhVien sinhVien) {
            if (!"SINHVIEN".equalsIgnoreCase(taiKhoan.getVaiTro())
                    || !taiKhoan.getMaSo().equals(sinhVien.getMaSinhVien())
                    || (sinhVien.getNganh() != null
                    && (sinhVien.getNganh().getMaNganh() == null
                    || sinhVien.getNganh().getMaNganh().isBlank()
                    || sinhVien.getNganh().getMaNganh().length() > 20))
                    || (sinhVien.getLop() != null && sinhVien.getLop().length() > 50)
                    || (sinhVien.getHeDaoTao() != null && sinhVien.getHeDaoTao().length() > 50)) {
                throw new ServiceException("Mã sinh viên hoặc vai trò không hợp lệ.");
            }
        } else if (taiKhoan instanceof GiangVien giangVien) {
            if (!"GIAOVIEN".equalsIgnoreCase(taiKhoan.getVaiTro())
                    || !taiKhoan.getMaSo().equals(giangVien.getMaGiangVien())
                    || (giangVien.getChuyenNganh() != null && giangVien.getChuyenNganh().length() > 100)
                    || (giangVien.getHocVi() != null && giangVien.getHocVi().length() > 50)) {
                throw new ServiceException("Mã giảng viên hoặc vai trò không hợp lệ.");
            }
        } else {
            throw new ServiceException("Chỉ hỗ trợ tạo và cập nhật hồ sơ sinh viên, giảng viên.");
        }
    }

    private void kiemTraNganhTonTai(Users taiKhoan) throws SQLException {
        if (taiKhoan instanceof SinhVien sinhVien && sinhVien.getNganh() != null
                && !nganhDAO.tonTai(sinhVien.getNganh().getMaNganh())) {
            throw new ServiceException("Mã ngành không tồn tại.");
        }
    }

}
