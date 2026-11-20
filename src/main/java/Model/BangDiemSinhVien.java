package Model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Dùng cho giảng viên. Dữ liệu một sinh viên dùng để hiển thị bảng điểm của lớp học phần. */
public class BangDiemSinhVien {
    private final int maDangKy;
    private final String maSinhVien;
    private final String hoTen;
    private final String trangThai;
    private final Map<Integer, Float> diemThanhPhan = new LinkedHashMap<>();
    private Float diemTongKet;

    public BangDiemSinhVien(int maDangKy, String maSinhVien, String hoTen,
                            Float diemTongKet, String trangThai) {
        this.maDangKy = maDangKy;
        this.maSinhVien = maSinhVien;
        this.hoTen = hoTen;
        this.diemTongKet = diemTongKet;
        this.trangThai = trangThai;
    }

    public int getMaDangKy() {
        return maDangKy;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public Float getDiemTongKet() {
        return diemTongKet;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public Float getDiemThanhPhan(int maThanhPhan) {
        return diemThanhPhan.get(maThanhPhan);
    }

    public void setDiemThanhPhan(int maThanhPhan, Float diem) {
        if (diem != null) {
            diemThanhPhan.put(maThanhPhan, diem);
        }
    }
}