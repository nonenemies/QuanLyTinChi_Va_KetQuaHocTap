package DAO;

import Model.ThanhPhanDanhGia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Đọc cấu hình thành phần đánh giá được dùng trong bảng điểm. */
public class ThanhPhanDanhGiaDAO extends BaseDAO {
    public List<ThanhPhanDanhGia> layTheoMon(String maMon) throws SQLException {
        List<ThanhPhanDanhGia> ketQua = new ArrayList<>();
        String sql = "SELECT MaThanhPhan, MaMon, TenThanhPhan, TrongSo, ThuTu, TrangThai "
                + "FROM ThanhPhanDanhGia WHERE MaMon = ? ORDER BY ThuTu, MaThanhPhan";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new ThanhPhanDanhGia(rs.getInt("MaThanhPhan"), rs.getString("MaMon"),
                            rs.getString("TenThanhPhan"), rs.getBigDecimal("TrongSo"),
                            rs.getObject("ThuTu", Integer.class), rs.getBoolean("TrangThai")));
                }
            }
        }
        return ketQua;
    }
}
