package Services;

import java.util.*;
import java.sql.SQLException;
import DAO.DangKyHocDAO;
import DAO.DotDangKyNganhDAO;
import DAO.LopHocPhanDAO;
import DAO.MonTienQuyetDAO;
import Model.ChuongTrinhDaoTao;
import Model.DotDangKyNganh;
import Model.MonTienQuyet;
import Model.DangKyHoc;
import Model.LopHocDangKy;
import Model.LopHocThoiKhoaBieu;
import DAO.ChuongTrinhDaoTaoDAO;

public class DangKyHocService {
    public static final int MIN_TIN_CHI = 11;
    public static final int MAX_TIN_CHI = 26;

    private final DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();
    private final ChuongTrinhDaoTaoDAO chuongTrinhDaoTaoDAO = new ChuongTrinhDaoTaoDAO();
    private final DotDangKyNganhDAO dotDangKyNganhDAO = new DotDangKyNganhDAO();

    public String dangKyHoc(String maSinhVien, String maLop) {
        if (maSinhVien == null || maSinhVien.isBlank() || maLop == null || maLop.isBlank()) {
            return "KHONG_HOP_LE";
        }
        try {
                String ketQuaKiemTra = kiemTraDieuKienDangKy(maSinhVien, maLop);
                if (!"THANH_CONG".equals(ketQuaKiemTra)) {
                    return ketQuaKiemTra;
                }

                if (!lopHocPhanDAO.tangSiSo( maLop)) {
                    return "LOP_KHONG_HOAT_DONG";
                }

                DangKyHoc dk = new DangKyHoc(maLop, maSinhVien);
                if (!dangKyDAO.themDangKyMoi(dk)) {
                    return "KHONG_HOP_LE";
                }

                return "THANH_CONG";

        } catch (SQLException e) {
            throw new ServiceException("Không thể xử lý đăng ký học.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể xử lý đăng ký học.", e);
    }

    }

    private String kiemTraDieuKienDangKy(String maSinhVien, String maLop)
            throws SQLException {
        if (!dangKyDAO.sinhVienConHoatDong(maSinhVien)) {
            return "SINH_VIEN_KHONG_HOAT_DONG";
        }

        var lopHoc = lopHocPhanDAO.layLopTheoMa( maLop);
        if (lopHoc == null || !"HOAT_DONG".equals(lopHoc.getTrangThai())) {
            return "LOP_KHONG_HOAT_DONG";
        }
        DotDangKyNganh dotDangKy = dotDangKyNganhDAO.layDotDangKyHienTai(maSinhVien);
        if (dotDangKy == null) {
            return "DOT_DANG_KY_DA_DONG";
        }
        ChuongTrinhDaoTao chuongTrinh = chuongTrinhDaoTaoDAO.layChuongTrinhMonHoc(
                maSinhVien, lopHoc.getMaMon());
        if (chuongTrinh == null) {
            return "KHONG_HOP_LE";
        }
        if (dangKyDAO.kiemTraDaDangKy(maSinhVien, maLop)
                || dangKyDAO.daDangKyMonTrongHocKy(maSinhVien, lopHoc.getMaMon(), lopHoc.getHocKy())) {
            return "DA_DANG_KY_MON_TRONG_HOC_KY";
        }
        if (dangKyDAO.daDatMon(maSinhVien, lopHoc.getMaMon())) {
            return "DA_QUA_MON";
        }
        if (!dangKyDAO.coLichHoc(maLop)) {
            return "LOP_CHUA_XEP_LICH";
        }
        if (dangKyDAO.coLopBiTrungLich(maSinhVien, maLop)) {
            return "TRUNG_LICH";
        }
        for (MonTienQuyet prerequisite
                : MonTienQuyetDAO.layDanhSachMonTienQuyet(lopHoc.getMaMon())) {
            if (!"HOAT_DONG".equals(prerequisite.getMonTienQuyet().getTrangThai())
                    || !dangKyDAO.kiemTraDaQuaMon(maSinhVien,
                            prerequisite.getMaMonTienQuyet())) {
                return "THIEU_MON_TIEN_QUYET";
            }
        }

        int credits = dangKyDAO.laySoTinChi(lopHoc.getMaMon());
        if (credits <= 0 || dangKyDAO.tongTinChiDangHoc(maSinhVien, lopHoc.getHocKy()) + credits
                > MAX_TIN_CHI) {
            return "VUOT_QUA_TIN_CHI";
        }
        return "THANH_CONG";
    }

    public boolean huyDangKyHoc(String maSinhVien, String maLop) {
        if (maSinhVien == null || maSinhVien.isBlank() || maLop == null || maLop.isBlank()) {
            return false;
        }
        try {
                if (!dangKyDAO.sinhVienConHoatDong(maSinhVien)) {
                    return false;
                }
                if (lopHocPhanDAO.layLopTheoMa( maLop) == null) {
                    return false;
                }

                var lopHoc = lopHocPhanDAO.layLopTheoMa( maLop);
                if (lopHoc == null || dotDangKyNganhDAO.layDotDangKyHienTai(maSinhVien) == null) {
                    return false;
                }
                String trangThai = dangKyDAO.layTrangThaiDangKy(maSinhVien, maLop);
                if (trangThai == null || "HUY".equals(trangThai)) {
                    return false;
                }
                if (!"DANG_HOC".equals(trangThai)
                        || dangKyDAO.coDiem(maSinhVien, maLop)) {
                    return false;
                }
                if (!dangKyDAO.huyDangKy(maSinhVien, maLop)) {
                    return false;
                }

                if (!lopHocPhanDAO.giamSiSo( maLop)) {
                    return false;
                }

                return true;
        } catch (SQLException e) {
            throw new ServiceException("Không thể hủy đăng ký lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể hủy đăng ký lớp.", e);
        }
    }

    public List<LopHocThoiKhoaBieu> xemLichHocTheoTuan(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.isBlank()) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ.");
        }
        try {
            return dangKyDAO.layLichHocTheoTuan(maSinhVien);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải thời khóa biểu.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể tải thời khóa biểu.", e);
        }
    }

    public List<LopHocDangKy> xemDanhSachLopDangKy(String maSinhVien) {
        try {
            return lopHocPhanDAO.layDanhSachLopDangKy(maSinhVien);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách lớp đăng ký.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể tải danh sách lớp đăng ký.", e);
        }
    }

    public List<LopHocDangKy> xemDanhSachLopDaDangKy(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.isBlank()) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ.");
        }
        try {
            return lopHocPhanDAO.layDanhSachLopDaDangKy(maSinhVien);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách lớp đã đăng ký.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể tải danh sách lớp đã đăng ký.", e);
        }
    }

    public int layTongTinChiDangKy(String maSinhVien, String hocKy) {
        if (maSinhVien == null || maSinhVien.isBlank() || hocKy == null || hocKy.isBlank()) {
            throw new IllegalArgumentException("Mã sinh viên và học kỳ là bắt buộc.");
        }
        try {
            return dangKyDAO.tongTinChiDangHoc(maSinhVien, hocKy);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải tổng tín chỉ của học kỳ.", e);
        } catch (RuntimeException e) {
            throw ServiceException.chuyenThanhLoiDichVu("Không thể tải tổng tín chỉ của học kỳ.", e);
        }
    }

}
