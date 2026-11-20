package Model;

import java.util.Date;

public class DiemThanhPhan {
    private int maDiemThanhPhan;
    private int maDangKy;
    private int maThanhPhan;
    private float diemDatDuoc;
    private Date ngayNhap;

    public DiemThanhPhan() {
    }

    public DiemThanhPhan(int maDiemThanhPhan, int maDangKy, int maThanhPhan, float diemDatDuoc) {
        this.maDiemThanhPhan = maDiemThanhPhan;
        this.maDangKy = maDangKy;
        this.maThanhPhan = maThanhPhan;
        this.diemDatDuoc = diemDatDuoc;
    }

    public int getMaDiemThanhPhan() {
        return maDiemThanhPhan;
    }

    public void setMaDiemThanhPhan(int maDiemThanhPhan) {
        this.maDiemThanhPhan = maDiemThanhPhan;
    }

    public int getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(int maDangKy) {
        this.maDangKy = maDangKy;
    }

    public int getMaThanhPhan() {
        return maThanhPhan;
    }

    public void setMaThanhPhan(int maThanhPhan) {
        this.maThanhPhan = maThanhPhan;
    }

    public float getDiemDatDuoc() {
        return diemDatDuoc;
    }

    public void setDiemDatDuoc(float diemDatDuoc) {
        this.diemDatDuoc = diemDatDuoc;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }
}
