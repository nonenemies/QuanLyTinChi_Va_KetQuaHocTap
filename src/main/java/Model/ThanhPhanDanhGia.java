package Model;

import java.math.BigDecimal;

public class ThanhPhanDanhGia {
    private int maThanhPhan;
    private String maMon;
    private String tenThanhPhan;
    private BigDecimal trongSo;
    private Integer thuTu;
    private boolean trangThai = true;

    public ThanhPhanDanhGia() {
    }

    public ThanhPhanDanhGia(int maThanhPhan, String maMon, String tenThanhPhan, BigDecimal trongSo,
                           Integer thuTu, boolean trangThai) {
        this.maThanhPhan = maThanhPhan;
        this.maMon = maMon;
        this.tenThanhPhan = tenThanhPhan;
        this.trongSo = trongSo;
        this.thuTu = thuTu;
        this.trangThai = trangThai;
    }

    public int getMaThanhPhan() {
        return maThanhPhan;
    }

    public void setMaThanhPhan(int maThanhPhan) {
        this.maThanhPhan = maThanhPhan;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenThanhPhan() {
        return tenThanhPhan;
    }

    public void setTenThanhPhan(String tenThanhPhan) {
        this.tenThanhPhan = tenThanhPhan;
    }

    public BigDecimal getTrongSo() {
        return trongSo;
    }

    public void setTrongSo(BigDecimal trongSo) {
        this.trongSo = trongSo;
    }

    public Integer getThuTu() {
        return thuTu;
    }

    public void setThuTu(Integer thuTu) {
        this.thuTu = thuTu;
    }

    public boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
}
