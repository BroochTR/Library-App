import com.library.model.Document;
import com.library.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDocumentRepository implements DocumentRepository {
    
    /**
     * Lưu tài liệu mới vào cơ sở dữ liệu
     * @param document tài liệu cần lưu
     * @return true nếu lưu thành công, false nếu thất bại
     */
    @Override
    public boolean save(Document document) {
        String sql = "INSERT INTO documents (id, title, author, genre, year, description, added_date, " +
                     "isbn, publisher, page_count, language, edition, total_quantity, available_quantity) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, document.getId());
            stmt.setString(2, document.getTitle());
            stmt.setString(3, document.getAuthor());
            stmt.setString(4, document.getGenre());
            stmt.setInt(5, document.getYear());
            stmt.setString(6, document.getDescription());
            stmt.setDate(7, Date.valueOf(document.getAddedDate()));
            stmt.setString(8, document.getIsbn());
            stmt.setString(9, document.getPublisher());
            stmt.setInt(10, document.getPageCount());
            stmt.setString(11, document.getLanguage());
            stmt.setString(12, document.getEdition());
            stmt.setInt(13, document.getTotalQuantity());
            stmt.setInt(14, document.getAvailableQuantity());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving document: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm tài liệu theo ID
     * @param id ID của tài liệu
     * @return tài liệu tìm được hoặc null nếu không tìm thấy
     */
    @Override
    public Document findById(String id) {
        String sql = "SELECT * FROM documents WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDocument(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for documents by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lấy danh sách tất cả tài liệu
     * @return danh sách tài liệu được sắp xếp theo tiêu đề
     */
    @Override
    public List<Document> findAll() {
        String sql = "SELECT * FROM documents ORDER BY title";
        List<Document> documents = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for all documents: " + e.getMessage());
        }
        return documents;
    }
    
    /**
     * Cập nhật thông tin tài liệu
     * @param document tài liệu với thông tin mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    @Override
    public boolean update(Document document) {
        String sql = "UPDATE documents SET title = ?, author = ?, genre = ?, year = ?, " +
                     "description = ?, isbn = ?, publisher = ?, page_count = ?, " +
                     "language = ?, edition = ?, total_quantity = ?, available_quantity = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getAuthor());
            stmt.setString(3, document.getGenre());
            stmt.setInt(4, document.getYear());
            stmt.setString(5, document.getDescription());
            stmt.setString(6, document.getIsbn());
            stmt.setString(7, document.getPublisher());
            stmt.setInt(8, document.getPageCount());
            stmt.setString(9, document.getLanguage());
            stmt.setString(10, document.getEdition());
            stmt.setInt(11, document.getTotalQuantity());
            stmt.setInt(12, document.getAvailableQuantity());
            stmt.setString(13, document.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when updating document: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tài liệu khỏi cơ sở dữ liệu
     * @param id ID của tài liệu cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM documents WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when deleting document: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tìm tài liệu theo tiêu đề
     * @param title tiêu đề cần tìm (có thể là một phần)
     * @return danh sách tài liệu có tiêu đề chứa từ khóa
     */
    @Override
    public List<Document> findByTitle(String title) {
        String sql = "SELECT * FROM documents WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
        List<Document> documents = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for documents by title: " + e.getMessage());
        }
        return documents;
    }
    
    /**
     * Tìm tài liệu theo tác giả
     * @param author tên tác giả cần tìm (có thể là một phần)
     * @return danh sách tài liệu của tác giả
     */
    @Override
    public List<Document> findByAuthor(String author) {
        String sql = "SELECT * FROM documents WHERE LOWER(author) LIKE LOWER(?) ORDER BY title";
        List<Document> documents = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + author + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for documents by author: " + e.getMessage());
        }
        return documents;
    }
    
    /**
     * Tìm tài liệu theo thể loại
     * @param genre thể loại cần tìm (có thể là một phần)
     * @return danh sách tài liệu thuộc thể loại
     */
    @Override
    public List<Document> findByGenre(String genre) {
        String sql = "SELECT * FROM documents WHERE LOWER(genre) LIKE LOWER(?) ORDER BY title";
        List<Document> documents = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + genre + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for documents by genre: " + e.getMessage());
        }
        return documents;
    }
    
    /**
     * Lấy danh sách tài liệu có sẵn để mượn
     * @return danh sách tài liệu có số lượng khả dụng > 0
     */
    @Override
    public List<Document> findAvailable() {
        String sql = "SELECT * FROM documents WHERE available_quantity > 0 ORDER BY title";
        List<Document> documents = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error when searching for available documents: " + e.getMessage());
        }
        return documents;
    }
    
    /**
     * Cập nhật số lượng khả dụng của tài liệu
     * @param documentId ID của tài liệu
     * @param availableQuantity số lượng khả dụng mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    @Override
    public boolean updateQuantity(String documentId, int availableQuantity) {
        String sql = "UPDATE documents SET available_quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, availableQuantity);
            stmt.setString(2, documentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when updating document quantity: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ ResultSet thành đối tượng Document
     */
    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setId(rs.getString("id"));
        document.setTitle(rs.getString("title"));
        document.setAuthor(rs.getString("author"));
        document.setGenre(rs.getString("genre"));
        document.setYear(rs.getInt("year"));
        document.setDescription(rs.getString("description"));
        
        Date addedDate = rs.getDate("added_date");
        if (addedDate != null) {
            document.setAddedDate(addedDate.toLocalDate());
        }
        
        document.setIsbn(rs.getString("isbn"));
        document.setPublisher(rs.getString("publisher"));
        document.setPageCount(rs.getInt("page_count"));
        document.setLanguage(rs.getString("language"));
        document.setEdition(rs.getString("edition"));
        document.setTotalQuantity(rs.getInt("total_quantity"));
        document.setAvailableQuantity(rs.getInt("available_quantity"));
        
        return document;
    }
}
