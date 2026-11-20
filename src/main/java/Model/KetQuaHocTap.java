package Model;

/** Dữ liệu tổng hợp của một học phần trong bảng kết quả học tập sinh viên. */
public class KetQuaHocTap {
    private final String maLop;
    private final String maMon;
    private final String tenMon;
    private final String hocKy;
    private final String lichHoc;
    private final Integer soTinChi;
    private final Float diemTongKet;
    private final String trangThaiDangKy;
    private final String diemThanhPhan;

    public KetQuaHocTap(String maLop, String maMon, String tenMon, String hocKy,
                        String lichHoc, Integer soTinChi, Float diemTongKet,
                        String trangThaiDangKy, String diemThanhPhan) {
        this.maLop = maLop;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.hocKy = hocKy;
        this.lichHoc = lichHoc;
        this.soTinChi = soTinChi;
        this.diemTongKet = diemTongKet;
        this.trangThaiDangKy = trangThaiDangKy;
        this.diemThanhPhan = diemThanhPhan;
    }

    public String getMaLop() { return maLop; }
    public String getMaMon() { return maMon; }
    public String getTenMon() { return tenMon; }
    public String getHocKy() { return hocKy; }
    public String getLichHoc() { return lichHoc; }
    public Integer getSoTinChi() { return soTinChi; }
    public Float getDiemTongKet() { return diemTongKet; }
    public String getTrangThaiDangKy() { return trangThaiDangKy; }
    public String getDiemThanhPhan() { return diemThanhPhan; }

    public String getTrangThai() {
        return switch (trangThaiDangKy) {
            case "DA_HOAN_THANH" -> "Đã hoàn thành";
            case "KHONG_DAT" -> "Không đạt";
            case "HUY" -> "Đã hủy";
            default -> "Đang học";
        };
    }
}
