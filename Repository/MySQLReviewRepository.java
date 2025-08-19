import com.library.model.Review;
import com.library.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai MySQL của ReviewRepository
 * Xử lý tất cả các thao tác cơ sở dữ liệu cho đánh giá
 */
public class MySQLReviewRepository implements ReviewRepository {
    
    /**
     * Lưu một đánh giá mới vào cơ sở dữ liệu
     * @param review đối tượng Review cần lưu
     * @return true nếu lưu thành công, false nếu thất bại
     */
    @Override
    public boolean save(Review review) {
        String sql = "INSERT INTO reviews (id, user_id, document_id, rating, comment, review_date, helpful_votes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, review.getId());
            stmt.setString(2, review.getUserId());
            stmt.setString(3, review.getDocumentId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            stmt.setTimestamp(6, Timestamp.valueOf(review.getReviewDate()));
            stmt.setInt(7, review.getHelpfulVotes());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving review: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm đánh giá theo ID
     * @param id mã đánh giá
     * @return đối tượng Review nếu tìm thấy, null nếu không
     */
    @Override
    public Review findById(String id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReview(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding review by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lấy tất cả các đánh giá
     * @return danh sách tất cả đánh giá, sắp xếp theo ngày đánh giá giảm dần
     */
    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM reviews ORDER BY review_date DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all reviews: " + e.getMessage());
        }
        return reviews;
    }
    
    /**
     * Cập nhật thông tin đánh giá
     * @param review đối tượng Review cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    @Override
    public boolean update(Review review) {
        String sql = "UPDATE reviews SET user_id = ?, document_id = ?, rating = ?, comment = ?, " +
                     "review_date = ?, helpful_votes = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, review.getUserId());
            stmt.setString(2, review.getDocumentId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setTimestamp(5, Timestamp.valueOf(review.getReviewDate()));
            stmt.setInt(6, review.getHelpfulVotes());
            stmt.setString(7, review.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa đánh giá theo ID
     * @param id mã đánh giá cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm tất cả đánh giá của một tài liệu
     * @param documentId mã tài liệu
     * @return danh sách đánh giá của tài liệu, sắp xếp theo ngày đánh giá giảm dần
     */
    @Override
    public List<Review> findByDocumentId(String documentId) {
        String sql = "SELECT * FROM reviews WHERE document_id = ? ORDER BY review_date DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding reviews by document ID: " + e.getMessage());
        }
        return reviews;
    }
    
    /**
     * Tìm tất cả đánh giá của một người dùng
     * @param userId mã người dùng
     * @return danh sách đánh giá của người dùng, sắp xếp theo ngày đánh giá giảm dần
     */
    @Override
    public List<Review> findByUserId(String userId) {
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY review_date DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding reviews by user ID: " + e.getMessage());
        }
        return reviews;
    }
    
    /**
     * Lấy điểm đánh giá trung bình của một tài liệu
     * @param documentId mã tài liệu
     * @return điểm đánh giá trung bình (0.0 nếu không có đánh giá)
     */
    @Override
    public double getAverageRating(String documentId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE document_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            System.err.println("Error getting average rating: " + e.getMessage());
        }
        return 0.0;
    }
    
    /**
     * Lấy số lượng đánh giá của một tài liệu
     * @param documentId mã tài liệu
     * @return số lượng đánh giá
     */
    @Override
    public int getReviewCount(String documentId) {
        String sql = "SELECT COUNT(*) as review_count FROM reviews WHERE document_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("review_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting review count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Kiểm tra xem người dùng đã đánh giá tài liệu chưa
     * @param userId mã người dùng
     * @param documentId mã tài liệu
     * @return true nếu đã đánh giá, false nếu chưa
     */
    @Override
    public boolean hasUserReviewed(String userId, String documentId) {
        String sql = "SELECT COUNT(*) as count FROM reviews WHERE user_id = ? AND document_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, documentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user has reviewed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ ResultSet thành đối tượng Review
     * @param rs ResultSet từ câu truy vấn SQL
     * @return đối tượng Review được tạo từ dữ liệu ResultSet
     * @throws SQLException nếu có lỗi khi đọc dữ liệu từ ResultSet
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getString("id"));
        review.setUserId(rs.getString("user_id"));
        review.setDocumentId(rs.getString("document_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        
        Timestamp reviewDateTime = rs.getTimestamp("review_date");
        if (reviewDateTime != null) {
            review.setReviewDate(reviewDateTime.toLocalDateTime());
        }
        
        try {
            review.setHelpfulVotes(rs.getInt("helpful_votes"));
        } catch (SQLException e) {
            review.setHelpfulVotes(0);
        }
        
        review.setRecommended(review.getRating() >= 4); 
        
        return review;
    }
}
