import com.library.model.Document;
import java.util.List;

/**
 * Interface repository cho các thao tác CRUD với tài liệu
 * Định nghĩa các phương thức thao tác với cơ sở dữ liệu tài liệu
 */
public interface DocumentRepository {
    
    /**
     * Lưu tài liệu mới vào cơ sở dữ liệu
     */
    boolean save(Document document);
    
    /**
     * Tìm tài liệu theo ID
     */
    Document findById(String id);
    
    /**
     * Lấy danh sách tất cả tài liệu
     */
    List<Document> findAll();
    
    /**
     * Cập nhật thông tin tài liệu
     */
    boolean update(Document document);
    
    /**
     * Xóa tài liệu theo ID
     */
    boolean delete(String id);
    
    /**
     * Tìm kiếm tài liệu theo tiêu đề (không phân biệt hoa thường)
     */
    List<Document> findByTitle(String title);
    
    /**
     * Tìm kiếm tài liệu theo tác giả (không phân biệt hoa thường)
     */
    List<Document> findByAuthor(String author);
    
    /**
     * Tìm kiếm tài liệu theo thể loại (không phân biệt hoa thường)
     */
    List<Document> findByGenre(String genre);
    
    /**
     * Lấy danh sách tài liệu còn số lượng (available_quantity > 0)
     */
    List<Document> findAvailable();
    
    /**
     * Cập nhật số lượng sau khi mượn/trả
     */
    boolean updateQuantity(String documentId, int availableQuantity);
}
