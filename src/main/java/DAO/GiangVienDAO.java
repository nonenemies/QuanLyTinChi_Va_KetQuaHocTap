package DAO;

import Model.GiangVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Truy vấn hồ sơ chuyên môn bổ sung của tài khoản giảng viên. */
public class GiangVienDAO extends BaseDAO {
    private static final String SELECT = "SELECT u.UserID, u.HoTen, u.Email, u.Password, u.VaiTro, "
            + "u.MaSo, u.NgayTao, u.TrangThai, gv.MaGiangVien, gv.ChuyenNganh, gv.HocVi "
            + "FROM GiangVien gv JOIN Users u ON u.UserID = gv.UserID ";

    public GiangVien layTheoMaGiangVien(String maGiangVien) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE gv.MaGiangVien = ?")) {
            ps.setString(1, maGiangVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docGiangVien(rs) : null;
            }
        }
    }

    public GiangVien layTheoUserID(int userID) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE gv.UserID = ?")) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? docGiangVien(rs) : null;
            }
        }
    }

    public List<GiangVien> layTatCa() throws SQLException {
        List<GiangVien> lecturers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "ORDER BY gv.MaGiangVien");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lecturers.add(docGiangVien(rs));
        }
        return lecturers;
    }

    public boolean them(GiangVien giangVien) throws SQLException {
        Objects.requireNonNull(giangVien, "giangVien không được null");
        String sql = "INSERT INTO GiangVien (UserID, MaGiangVien, ChuyenNganh, HocVi) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, giangVien.getUserID());
            ps.setString(2, giangVien.getMaGiangVien());
            ps.setString(3, giangVien.getChuyenNganh());
            ps.setString(4, giangVien.getHocVi());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean capNhat(GiangVien giangVien) throws SQLException {
        Objects.requireNonNull(giangVien, "giangVien không được null");
        String sql = "UPDATE GiangVien SET MaGiangVien = ?, ChuyenNganh = ?, HocVi = ? WHERE UserID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, giangVien.getMaGiangVien());
            ps.setString(2, giangVien.getChuyenNganh());
            ps.setString(3, giangVien.getHocVi());
            ps.setInt(4, giangVien.getUserID());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean xoa(int userID) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM GiangVien WHERE UserID = ?")) {
            ps.setInt(1, userID);
            return ps.executeUpdate() == 1;
        }
    }

    private GiangVien docGiangVien(ResultSet rs) throws SQLException {
        return new GiangVien(rs.getInt("UserID"), rs.getString("HoTen"), rs.getString("Email"),
                rs.getString("Password"), rs.getString("VaiTro"), rs.getString("MaSo"),
                rs.getTimestamp("NgayTao"),
                rs.getString("TrangThai"),
                rs.getString("MaGiangVien"), rs.getString("ChuyenNganh"), rs.getString("HocVi"));
    }
}
