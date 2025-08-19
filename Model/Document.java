import java.time.LocalDate;
import java.util.Objects;

/**
 * Lớp Document bao gồm tất cả tài liệu trong thư viện
 */
public class Document {
    private String id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private String description;
    private LocalDate addedDate;

    // Các trường dành riêng cho sách (được gộp từ bảng books)
    private String isbn;
    private String publisher;
    private int pageCount;
    private String language;
    private String edition;
    private int totalQuantity;
    private int availableQuantity;

    /**
     * Enum biểu diễn trạng thái của tài liệu (tính từ số lượng còn lại)
     */
    public enum DocumentStatus {
        AVAILABLE,  // Có sẵn
        BORROWED    // Đã được mượn hết
    }

    /**
     * Constructor khởi tạo thông tin cơ bản của tài liệu
     */
    public Document(String id, String title, String author, String genre, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.addedDate = LocalDate.now();
        this.description = "";
        this.language = "English";
        this.edition = "1st";
        this.totalQuantity = 1;
        this.availableQuantity = 1;
    }

    /**
     * Constructor khởi tạo đầy đủ thông tin của tài liệu
     */
    public Document(String id, String title, String author, String genre, int year,
                   String isbn, String publisher, int pageCount, int totalQuantity) {
        this(id, title, author, genre, year);
        this.isbn = isbn;
        this.publisher = publisher;
        this.pageCount = pageCount;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
    }

    /**
     * Constructor mặc định
     */
    public Document() {
        this.addedDate = LocalDate.now();
        this.description = "";
        this.language = "English";
        this.edition = "1st";
        this.totalQuantity = 1;
        this.availableQuantity = 1;
    }

    /**
     * Lấy loại tài liệu 
     */
    public String getDocumentType() {
        return "Book";
    }

    // Các getter và setter 
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    // Getter và setter cho các thuộc tính riêng của sách
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        if (this.availableQuantity > totalQuantity) {
            this.availableQuantity = totalQuantity;
        }
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = Math.max(0, Math.min(availableQuantity, totalQuantity));
    }

    public int getBorrowedQuantity() {
        return totalQuantity - availableQuantity;
    }

    /**
     * Kiểm tra tài liệu còn có thể mượn hay không
     */
    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    /**
     * Lấy trạng thái tài liệu dựa vào số lượng
     */
    public DocumentStatus getStatus() {
        return isAvailable() ? DocumentStatus.AVAILABLE : DocumentStatus.BORROWED;
    }

    /**
     * Lấy thông tin trạng thái số lượng 
     */
    public String getQuantityStatus() {
        return availableQuantity + "/" + totalQuantity;
    }

    /**
     * Mượn 1 bản 
     */
    public boolean borrowOne() {
        if (availableQuantity > 0) {
            availableQuantity--;
            return true;
        }
        return false;
    }

    /**
     * Trả lại 1 bản 
     */
    public void returnOne() {
        if (availableQuantity < totalQuantity) {
            availableQuantity++;
        }
    }

    /**
     * Thêm số lượng bản mới vào kho
     */
    public void addQuantity(int quantity) {
        this.totalQuantity += quantity;
        this.availableQuantity += quantity;
    }

    /**
     * Lấy thông tin cơ bản của tài liệu
     */
    public String getBasicInfo() {
        return String.format("%s - %s by %s (%d) [%s]", id, title, author, year, getQuantityStatus());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Document document = (Document) obj;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Document[id=%s, title=%s, author=%s, quantity=%s]",
                id, title, author, getQuantityStatus());
    }
}
