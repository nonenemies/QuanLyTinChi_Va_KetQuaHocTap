package DAO;

import Model.Nganh;
import Model.SinhVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Truy vấn hồ sơ học tập bổ sung của tài khoản sinh viên. */
public class SinhVienDAO extends BaseDAO {
    private static final String SELECT = "SELECT u.UserID, u.HoTen, u.Email, u.Password, u.VaiTro, "
            + "u.MaSo, n.MaNganh, n.TenNganh, u.NgayTao, u.TrangThai, sv.MaSinhVien, sv.NgaySinh, sv.GioiTinh, "
            + "sv.Lop, sv.KhoaHoc, sv.HeDaoTao FROM SinhVien sv "
            + "JOIN Users u ON u.UserID = sv.UserID "
            + "LEFT JOIN Nganh n ON n.MaNganh = sv.MaNganh ";

    public SinhVien layTheoMaSinhVien(String maSinhVien) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE sv.MaSinhVien = ?")) {
            ps.setString(1, maSinhVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docSinhVien(rs) : null;
            }
        }
    }

    public SinhVien layTheoUserID(int userID) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE sv.UserID = ?")) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docSinhVien(rs) : null;
            }
        }
    }

    public List<SinhVien> layTatCa() throws SQLException {
        List<SinhVien> students = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "ORDER BY sv.MaSinhVien");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) students.add(docSinhVien(rs));
        }
        return students;
    }

    public boolean them(SinhVien sinhVien) throws SQLException {
        Objects.requireNonNull(sinhVien, "sinhVien không được null");
        String sql = "INSERT INTO SinhVien (UserID, MaSinhVien, MaNganh, NgaySinh, GioiTinh, Lop, KhoaHoc, HeDaoTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sinhVien.getUserID());
            ps.setString(2, sinhVien.getMaSinhVien());
            ps.setString(3, sinhVien.getNganh() == null ? null : sinhVien.getNganh().getMaNganh());
            if (sinhVien.getNgaySinh() == null) ps.setDate(4, null);
            else ps.setDate(4, new java.sql.Date(sinhVien.getNgaySinh().getTime()));
            if (sinhVien.getGioiTinh() == null) ps.setObject(5, null);
            else ps.setBoolean(5, sinhVien.getGioiTinh());
            ps.setString(6, sinhVien.getLop());
            ps.setObject(7, sinhVien.getKhoaHoc());
            ps.setString(8, sinhVien.getHeDaoTao());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean capNhat(SinhVien sinhVien) throws SQLException {
        Objects.requireNonNull(sinhVien, "sinhVien không được null");
        String sql = "UPDATE SinhVien SET MaSinhVien = ?, MaNganh = ?, NgaySinh = ?, GioiTinh = ?, Lop = ?, "
                + "KhoaHoc = ?, HeDaoTao = ? WHERE UserID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sinhVien.getMaSinhVien());
            ps.setString(2, sinhVien.getNganh() == null ? null : sinhVien.getNganh().getMaNganh());
            if (sinhVien.getNgaySinh() == null) ps.setDate(3, null);
            else ps.setDate(3, new java.sql.Date(sinhVien.getNgaySinh().getTime()));
            if (sinhVien.getGioiTinh() == null) ps.setObject(4, null);
            else ps.setBoolean(4, sinhVien.getGioiTinh());
            ps.setString(5, sinhVien.getLop());
            ps.setObject(6, sinhVien.getKhoaHoc());
            ps.setString(7, sinhVien.getHeDaoTao());
            ps.setInt(8, sinhVien.getUserID());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean xoa(int userID) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SinhVien WHERE UserID = ?")) {
            ps.setInt(1, userID);
            return ps.executeUpdate() == 1;
        }
    }

    private SinhVien docSinhVien(ResultSet rs) throws SQLException {
        Boolean gioiTinh = rs.getObject("GioiTinh", Boolean.class);
        return new SinhVien(rs.getInt("UserID"), rs.getString("HoTen"), rs.getString("Email"),
                rs.getString("Password"), rs.getString("VaiTro"), rs.getString("MaSo"),
                docNganh(rs), rs.getTimestamp("NgayTao"),
                rs.getString("TrangThai"), rs.getString("MaSinhVien"),
                rs.getDate("NgaySinh"), gioiTinh, rs.getString("Lop"),
                rs.getObject("KhoaHoc", Integer.class), rs.getString("HeDaoTao"));
    }

    private Nganh docNganh(ResultSet rs) throws SQLException {
        String maNganh = rs.getString("MaNganh");
        return maNganh == null ? null : new Nganh(maNganh, rs.getString("TenNganh"));
    }
}
