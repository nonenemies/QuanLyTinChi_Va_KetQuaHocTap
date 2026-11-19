package Services;

import DAO.UsersDAO;
import Model.Users;

public class AuthService {
    private final UsersDAO usersDAO = new UsersDAO();

    // Kiểm tra tài khoản trong cơ sở dữ liệu; trả về người dùng nếu hợp lệ, ngược lại trả về null.
    public Users xacThucDangNhap(String email, String password) {
        try {
            Users user = usersDAO.layTheoEmailDangHoatDong(email);
            return user != null && user.getPassword().equals(password) ? user : null;
        } catch (java.sql.SQLException e) {
            throw new ServiceException("Không thể xác thực tài khoản.", e);
        }
    }
}