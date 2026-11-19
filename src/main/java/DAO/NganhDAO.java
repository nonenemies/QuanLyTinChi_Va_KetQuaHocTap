package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NganhDAO extends BaseDAO {
    public boolean tonTai(String maNganh) throws SQLException {
        String sql = "SELECT 1 FROM Nganh WHERE MaNganh = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNganh);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
