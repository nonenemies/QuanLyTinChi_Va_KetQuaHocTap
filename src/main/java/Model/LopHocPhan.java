package Model;

/**
 * Model ánh xạ tới bảng LopHocPhan trong database.
 */
public class LopHocPhan {
    private String maLop;
    private String maMon;
    private String maGiangVien;
    private String hocKy;
    private Integer siSoToiDa;
    private Integer siSoHienTai;
    private String trangThai;

    public LopHocPhan() {
    }

    public LopHocPhan(String maLop, String maMon, String maGiangVien, String hocKy,
                  Integer siSoToiDa, Integer siSoHienTai, String trangThai) {
        this.maLop = maLop;
        this.maMon = maMon;
        this.maGiangVien = maGiangVien;
        this.hocKy = hocKy;
        this.siSoToiDa = siSoToiDa;
        this.siSoHienTai = siSoHienTai;
        this.trangThai = trangThai;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(String maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public String getHocKy() {
        return hocKy;
    }

    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(Integer siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    public Integer getSiSoHienTai() {
        return siSoHienTai;
    }

    public void setSiSoHienTai(Integer siSoHienTai) {
        this.siSoHienTai = siSoHienTai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}