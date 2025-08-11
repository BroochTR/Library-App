import com.library.model.User;
import java.util.List;

/**
 * Interface repository cho các thao tác CRUD với người dùng
 * Định nghĩa các phương thức thao tác với cơ sở dữ liệu người dùng
 */
public interface UserRepository {
    
    /**
     * Lưu người dùng mới vào cơ sở dữ liệu
     */
    boolean save(User user);
    
    /**
     * Tìm người dùng theo ID
     */
    User findById(String id);
    
    /**
     * Lấy danh sách tất cả người dùng
     */
    List<User> findAll();
    
    /**
     * Cập nhật thông tin người dùng
     */
    boolean update(User user);
    
    /**
     * Xóa người dùng theo ID
     */
    boolean delete(String id);
    
    /**
     * Tìm kiếm người dùng theo tên (không phân biệt hoa thường)
     */
    List<User> findByName(String name);
    
    /**
     * Lấy danh sách thể loại yêu thích của người dùng
     */
    List<String> findFavoriteGenres(String userId);
    
    /**
     * Thêm thể loại yêu thích cho người dùng
     */
    boolean addFavoriteGenre(String userId, String genre);
    
    /**
     * Xóa thể loại yêu thích của người dùng
     */
    boolean removeFavoriteGenre(String userId, String genre);
    
    /**
     * Xóa toàn bộ thể loại yêu thích của người dùng
     */
    boolean clearFavoriteGenres(String userId);
}
