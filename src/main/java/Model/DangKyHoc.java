package Model;
import java.util.*;

public class DangKyHoc {

    private int maDangKy;
    private String maLop;
    private String maSinhVien;
    private Date ngayDangKy;
    private float diemTongKet;
    private String trangThai;

    public DangKyHoc() {
    }

    public DangKyHoc(String maLop, String maSinhVien) {
        this.maLop = maLop;
        this.maSinhVien = maSinhVien;
    }

    public DangKyHoc(int maDangKy, String maLop, String maSinhVien, Date ngayDangKy, float diemTongKet, String trangThai) {
        this.maDangKy = maDangKy;
        this.maLop = maLop;
        this.maSinhVien = maSinhVien;
        this.ngayDangKy = ngayDangKy;
        this.diemTongKet = diemTongKet;
        this.trangThai = trangThai;
    }

    public int getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(int maDangKy) {
        this.maDangKy = maDangKy;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public float getDiemTongKet() {
        return diemTongKet;
    }

    public void setDiemTongKet(float diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}