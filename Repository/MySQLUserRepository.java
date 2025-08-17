package com.library.repository;

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
    
    @Override
    public User findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                // Tải thể loại yêu thích
                user.setFavoriteGenres(findFavoriteGenres(id));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Đầu tiên, tải tất cả người dùng mà không có thể loại yêu thích
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }
        
        // Sau đó, tải thể loại yêu thích cho từng người dùng riêng biệt
        for (User user : users) {
            user.setFavoriteGenres(findFavoriteGenres(user.getId()));
        }
        
        return users;
    }
    
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
    
    @Override
    public boolean delete(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Xóa thể loại yêu thích trước
                clearFavoriteGenres(id);
                
                // Xóa người dùng
                String sql = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    boolean result = stmt.executeUpdate() > 0;
                    
                    conn.commit();
                    return result;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            // Đầu tiên, tải tất cả người dùng mà không có thể loại yêu thích
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error finding users by name: " + e.getMessage());
        }
        
        // Sau đó, tải thể loại yêu thích cho từng người dùng riêng biệt
        for (User user : users) {
            user.setFavoriteGenres(findFavoriteGenres(user.getId()));
        }
        
        return users;
    }
    
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
    
    @Override
    public boolean clearFavoriteGenres(String userId) {
        String sql = "DELETE FROM user_favorite_genres WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate(); // Luôn trả về true, ngay cả khi không có bản ghi nào bị xóa
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing favorite genres: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ ResultSet thành đối tượng User
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
        
        // borrowedDocumentIds sẽ được điền bởi các phương thức khác nếu cần
        user.setBorrowedDocumentIds(new ArrayList<>());
        user.setFavoriteGenres(new ArrayList<>());
        
        return user;
    }
}
