import com.library.model.User;
import com.library.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai MySQL của UserRepository
 * Xử lý tất cả các thao tác cơ sở dữ liệu cho người dùng
 */
public class MySQLUserRepository implements UserRepository {
    
    /**
     * Lưu một người dùng mới vào cơ sở dữ liệu
     * @param user đối tượng User cần lưu
     * @return true nếu lưu thành công, false nếu thất bại
     */
    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users (id, name, email, phone, address, registration_date, " +
                     "user_type, is_active, max_borrow_limit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setDate(6, Date.valueOf(user.getRegistrationDate()));
            stmt.setString(7, user.getUserType().name());
            stmt.setBoolean(8, user.isActive());
            stmt.setInt(9, user.getMaxBorrowLimit());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm người dùng theo ID
     * @param id mã người dùng
     * @return đối tượng User nếu tìm thấy, null nếu không
     */
    @Override
    public User findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);               
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lấy tất cả người dùng
     * @return danh sách tất cả người dùng, sắp xếp theo tên
     */
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }        
        return users;
    }
    
    /**
     * Cập nhật thông tin người dùng
     * @param user đối tượng User cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, " +
                     "user_type = ?, is_active = ?, max_borrow_limit = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getUserType().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getMaxBorrowLimit());
            stmt.setString(8, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa người dùng theo ID
     * @param id mã người dùng cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm người dùng theo tên (tìm kiếm gần đúng)
     * @param name tên người dùng cần tìm (có thể là một phần của tên)
     * @return danh sách người dùng có tên chứa chuỗi tìm kiếm, sắp xếp theo tên
     */
    @Override
    public List<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error finding users by name: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Lấy danh sách thể loại yêu thích của người dùng
     * @param userId mã người dùng
     * @return danh sách thể loại yêu thích, sắp xếp theo tên thể loại
     */
    @Override
    public List<String> findFavoriteGenres(String userId) {
        String sql = "SELECT genre FROM user_favorite_genres WHERE user_id = ? ORDER BY genre";
        List<String> genres = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding favorite genres: " + e.getMessage());
        }
        return genres;
    }
    
    /**
     * Thêm một thể loại yêu thích cho người dùng
     * @param userId mã người dùng
     * @param genre thể loại cần thêm
     * @return true nếu thêm thành công, false nếu thất bại hoặc đã tồn tại
     */
    @Override
    public boolean addFavoriteGenre(String userId, String genre) {
        String sql = "INSERT IGNORE INTO user_favorite_genres (user_id, genre) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, genre);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding favorite genre: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa một thể loại yêu thích của người dùng
     * @param userId mã người dùng
     * @param genre thể loại cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @Override
    public boolean removeFavoriteGenre(String userId, String genre) {
        String sql = "DELETE FROM user_favorite_genres WHERE user_id = ? AND genre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, genre);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing favorite genre: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tất cả thể loại yêu thích của người dùng
     * @param userId mã người dùng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @Override
    public boolean clearFavoriteGenres(String userId) {
        String sql = "DELETE FROM user_favorite_genres WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing favorite genres: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ ResultSet thành đối tượng User
     * @param rs ResultSet từ câu truy vấn SQL
     * @return đối tượng User được tạo từ dữ liệu ResultSet
     * @throws SQLException nếu có lỗi khi đọc dữ liệu từ ResultSet
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            user.setRegistrationDate(regDate.toLocalDate());
        }
        
        String userTypeStr = rs.getString("user_type");
        if (userTypeStr != null) {
            user.setUserType(User.UserType.valueOf(userTypeStr));
        }
        
        user.setActive(rs.getBoolean("is_active"));
        user.setMaxBorrowLimit(rs.getInt("max_borrow_limit"));
        
        user.setBorrowedDocumentIds(new ArrayList<>());
        user.setFavoriteGenres(new ArrayList<>());
        
        return user;
    }
}
