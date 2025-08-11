import com.library.model.Review;
import java.util.List;

/**
 * Interface repository cho các thao tác CRUD với đánh giá tài liệu
 * Định nghĩa các phương thức thao tác với cơ sở dữ liệu đánh giá tài liệu
 */
public interface ReviewRepository {
    
    /**
     * Lưu đánh giá mới vào cơ sở dữ liệu
     */
    boolean save(Review review);
    
    /**
     * Tìm đánh giá theo ID
     */
    Review findById(String id);
    
    /**
     * Lấy danh sách tất cả đánh giá
     */
    List<Review> findAll();
    
    /**
     * Cập nhật thông tin đánh giá
     */
    boolean update(Review review);
    
    /**
     * Xóa đánh giá theo ID
     */
    boolean delete(String id);
    
    /**
     * Lấy tất cả đánh giá cho một tài liệu cụ thể
     */
    List<Review> findByDocumentId(String documentId);
    
    /**
     * Lấy tất cả đánh giá của một người dùng cụ thể
     */
    List<Review> findByUserId(String userId);
    
    /**
     * Lấy điểm đánh giá trung bình cho một tài liệu
     */
    double getAverageRating(String documentId);
    
    /**
     * Lấy số lượng đánh giá cho một tài liệu
     */
    int getReviewCount(String documentId);
    
    /**
     * Kiểm tra người dùng đã đánh giá tài liệu này chưa
     */
    boolean hasUserReviewed(String userId, String documentId);
}
