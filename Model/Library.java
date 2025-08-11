package com.library.model;

import java.util.*;
import java.util.stream.Collectors;
import com.library.repository.*;

/**
 * Class Library - Xử lý logic nghiệp vụ chính
 * Được cập nhật để sử dụng Repository pattern cho lưu trữ cơ sở dữ liệu
 * Thể hiện các nguyên tắc OOP: Đóng gói, Kết hợp, Tiêm phụ thuộc
 * Áp dụng Singleton pattern cho instance thư viện duy nhất
 */
public class Library {
    private static Library instance;
    private String libraryName;
    private String address;
    
    // Các dependency Repository 
    private DocumentRepository documentRepository;
    private UserRepository userRepository;
    private LoanTransactionRepository transactionRepository;
    private ReviewRepository reviewRepository;
    
    private int nextDocumentId;
    private int nextUserId;
    private int nextTransactionId;
    private int nextReviewId;
    private final double dailyFineRate = 0.50; 
    private final int defaultLoanDays = 14; 
    
    /**
     * Constructor private cho Singleton pattern
     */
    private Library() {
        this.libraryName = "Hệ thống Quản lý Thư viện Số";
        this.address = "123 Phố Thư viện, Thành phố Tri thức";
        
        // Khởi tạo các repository với implementations MySQL
        this.documentRepository = new MySQLDocumentRepository();
        this.userRepository = new MySQLUserRepository();
        this.transactionRepository = new MySQLLoanTransactionRepository();
        this.reviewRepository = new MySQLReviewRepository();
        
        this.nextDocumentId = getNextIdFromDatabase("documents", "DOC") + 1;
        this.nextUserId = getNextIdFromDatabase("users", "USER") + 1;
        this.nextTransactionId = getNextIdFromDatabase("loan_transactions", "TXN") + 1;
        this.nextReviewId = getNextIdFromDatabase("reviews", "REV") + 1;
    }
    
    /**
     * Lấy singleton instance
     */
    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }
    
    /**
     * Thiết lập repositories (cho testing hoặc các implementation khác)
     */
    public void setRepositories(DocumentRepository documentRepo, UserRepository userRepo,
                              LoanTransactionRepository transactionRepo, ReviewRepository reviewRepo) {
        this.documentRepository = documentRepo;
        this.userRepository = userRepo;
        this.transactionRepository = transactionRepo;
        this.reviewRepository = reviewRepo;
    }
    
    /**
     * Lấy ID khả dụng tiếp theo từ database bằng cách trích xuất phần số lớn nhất
     */
    private int getNextIdFromDatabase(String tableName, String prefix) {
        try {
            String sql = "SELECT MAX(CAST(SUBSTRING(id, " + (prefix.length() + 1) + ") AS UNSIGNED)) FROM " + tableName;
            
            java.sql.Connection conn = com.library.database.DatabaseConnection.getConnection();
            if (conn == null) return 0; // Nếu không có kết nối DB, bắt đầu từ 1
            
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                int maxId = rs.getInt(1);
                rs.close();
                stmt.close();
                return maxId;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Cảnh báo: Không thể lấy ID lớn nhất từ " + tableName + ": " + e.getMessage());
        }
        return 0; // Mặc định nếu bảng trống hoặc có lỗi xảy ra
    }
    
    // Getters và Setters
    public String getLibraryName() {
        return libraryName;
    }
    
    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public double getDailyFineRate() {
        return dailyFineRate;
    }
    
    public int getDefaultLoanDays() {
        return defaultLoanDays;
    }
    
    // ================ QUẢN LÝ TÀI LIỆU ================
    
    /**
     * Thêm tài liệu vào thư viện
     */
    public boolean addDocument(Document document) {
        if (document == null) {
            return false;
        }
        
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId("DOC" + String.format("%04d", nextDocumentId++));
        }
        
        // Kiểm tra nếu tài liệu với ID này đã tồn tại
        if (documentRepository.findById(document.getId()) != null) {
            return false; // Tài liệu với ID này đã tồn tại
        }
        
        return documentRepository.save(document);
    }
    
    /**
     * Xóa tài liệu khỏi thư viện
     */
    public boolean removeDocument(String documentId) {
        if (documentId == null || documentRepository.findById(documentId) == null) {
            return false;
        }
        
        // Kiểm tra nếu tài liệu hiện tại đang được mượn
        if (isDocumentBorrowed(documentId)) {
            return false; 
        }
        
        // Xóa tài liệu và các đánh giá liên quan
        List<Review> documentReviews = reviewRepository.findByDocumentId(documentId);
        for (Review review : documentReviews) {
            reviewRepository.delete(review.getId());
        }
        
        return documentRepository.delete(documentId);
    }
    
    /**
     * Cập nhật thông tin tài liệu
     */
    public boolean updateDocument(Document document) {
        if (document == null || document.getId() == null || 
            documentRepository.findById(document.getId()) == null) {
            return false;
        }
        
        return documentRepository.update(document);
    }
    
    /**
     * Lấy tài liệu theo ID
     */
    public Document getDocument(String documentId) {
        return documentRepository.findById(documentId);
    }
    
    /**
     * Lấy tất cả tài liệu
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    /**
     * Tìm kiếm tài liệu theo tiêu đề
     */
    public List<Document> searchDocumentsByTitle(String title) {
        return documentRepository.findByTitle(title);
    }
    
    /**
     * Tìm kiếm tài liệu theo tác giả
     */
    public List<Document> searchDocumentsByAuthor(String author) {
        return documentRepository.findByAuthor(author);
    }
    
    /**
     * Tìm kiếm tài liệu theo thể loại
     */
    public List<Document> searchDocumentsByGenre(String genre) {
        return documentRepository.findByGenre(genre);
    }
    
    /**
     * Lấy tài liệu có sẵn
     */
    public List<Document> getAvailableDocuments() {
        return documentRepository.findAvailable();
    }
    
    /**
     * Lấy tài liệu theo loại (tất cả tài liệu hiện tại cùng loại)
     */
    public List<Document> getDocumentsByType(Class<? extends Document> type) {
        return getAllDocuments(); // Tất cả tài liệu hiện tại là loại Document
    }
    
    // ================ QUẢN LÝ NGƯỜI DÙNG ================
    
    /**
     * Thêm người dùng vào thư viện
     */
    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId("USER" + String.format("%04d", nextUserId++));
        }
        
        // Kiểm tra nếu người dùng với ID này đã tồn tại
        if (userRepository.findById(user.getId()) != null) {
            return false; 
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Xóa người dùng khỏi thư viện
     */
    public boolean removeUser(String userId) {
        if (userId == null || userRepository.findById(userId) == null) {
            return false;
        }
        
        User user = userRepository.findById(userId);
        if (user.getBorrowedCount() > 0) {
            return false; 
        }
        
        // Xóa đánh giá của người dùng trước
        List<Review> userReviews = reviewRepository.findByUserId(userId);
        for (Review review : userReviews) {
            reviewRepository.delete(review.getId());
        }
        
        return userRepository.delete(userId);
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null || 
            userRepository.findById(user.getId()) == null) {
            return false;
        }
        
        return userRepository.update(user);
    }
    
    /**
     * Lấy người dùng theo ID
     */
    public User getUser(String userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Lấy tất cả người dùng
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Tìm kiếm người dùng theo tên
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByName(name);
    }
    
    // ================ QUẢN LÝ MƯỢN TRẢ ================
    
    /**
     * Mượn tài liệu
     */
    public String borrowDocument(String userId, String documentId) {
        User user = userRepository.findById(userId);
        Document document = documentRepository.findById(documentId);
        
        if (user == null || document == null) {
            return null; // Không tìm thấy người dùng hoặc tài liệu
        }
        
        if (!user.isActive() || !user.canBorrowMore()) {
            return null; 
        }
        
        // Kiểm tra tính khả dụng
        boolean canBorrow = document.isAvailable();
        
        if (!canBorrow) {
            return null; 
        }
        
        // Tạo giao dịch mượn
        String transactionId = "TXN" + String.format("%04d", nextTransactionId++);
        LoanTransaction transaction = new LoanTransaction(transactionId, userId, documentId, defaultLoanDays);
        
        // Cập nhật trạng thái người dùng và tài liệu
        user.borrowDocument(documentId);
        document.borrowOne(); // Giảm số lượng có sẵn
        
        // Lưu cập nhật vào cơ sở dữ liệu
        userRepository.update(user);
        documentRepository.updateQuantity(documentId, document.getAvailableQuantity());
        transactionRepository.save(transaction);
        
        return transactionId;
    }
    
    /**
     * Trả tài liệu
     */
    public boolean returnDocument(String transactionId) {
        LoanTransaction transaction = transactionRepository.findById(transactionId);
        if (transaction == null || transaction.getStatus() != LoanTransaction.TransactionStatus.ACTIVE) {
            return false;
        }
        
        User user = userRepository.findById(transaction.getUserId());
        Document document = documentRepository.findById(transaction.getDocumentId());
        
        if (user == null || document == null) {
            return false;
        }
        
        // Tính phí phạt nếu quá hạn
        if (transaction.isOverdue()) {
            double fine = transaction.calculateFine(dailyFineRate);
            transaction.setFineAmount(fine);
        }
        
        // Cập nhật giao dịch, người dùng và tài liệu
        transaction.returnDocument();
        user.returnDocument(transaction.getDocumentId());
        document.returnOne(); // Tăng số lượng có sẵn
        
        // Lưu cập nhật vào cơ sở dữ liệu
        transactionRepository.update(transaction);
        userRepository.update(user);
        documentRepository.updateQuantity(document.getId(), document.getAvailableQuantity());
        
        return true;
    }
    
    /**
     * Gia hạn mượn
     */
    public boolean renewLoan(String transactionId) {
        LoanTransaction transaction = transactionRepository.findById(transactionId);
        if (transaction == null) {
            return false;
        }
        
        boolean renewed = transaction.renew(defaultLoanDays);
        if (renewed) {
            transactionRepository.update(transaction);
        }
        return renewed;
    }
    
    /**
     * Kiểm tra nếu tài liệu hiện tại đang được mượn
     */
    public boolean isDocumentBorrowed(String documentId) {
        return transactionRepository.isDocumentBorrowed(documentId);
    }
    
    /**
     * Lấy các khoản mượn đang hoạt động của người dùng
     */
    public List<LoanTransaction> getUserActiveLoans(String userId) {
        return transactionRepository.findActiveTransactionsByUserId(userId);
    }
    
    /**
     * Lấy giao dịch quá hạn
     */
    public List<LoanTransaction> getOverdueTransactions() {
        return transactionRepository.findOverdueTransactions();
    }
    
    /**
     * Lấy tất cả giao dịch
     */
    public List<LoanTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    // ================ QUẢN LÝ ĐÁNH GIÁ ================
    
    /**
     * Thêm đánh giá
     */
    public boolean addReview(String userId, String documentId, int rating, String comment) {
        if (userRepository.findById(userId) == null || documentRepository.findById(documentId) == null) {
            return false;
        }
        
        // Kiểm tra nếu người dùng đã đánh giá tài liệu này
        if (reviewRepository.hasUserReviewed(userId, documentId)) {
            return false; 
        }
        
        String reviewId = "REV" + String.format("%04d", nextReviewId++);
        
        Review review = new Review(reviewId, userId, documentId, rating, comment);
        return reviewRepository.save(review);
    }
    
    /**
     * Lấy đánh giá cho tài liệu
     */
    public List<Review> getDocumentReviews(String documentId) {
        return reviewRepository.findByDocumentId(documentId);
    }
    
    /**
     * Lấy điểm đánh giá trung bình cho tài liệu
     */
    public double getDocumentAverageRating(String documentId) {
        return reviewRepository.getAverageRating(documentId);
    }
    
    /**
     * Đánh dấu đánh giá là hữu ích (tăng số lượt bình chọn hữu ích)
     */
    public boolean markReviewAsHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            return false;
        }
        
        review.addHelpfulVote();
        return reviewRepository.update(review);
    }
    
    // ================ HỆ THỐNG GỢI Ý ================
    
    /**
     * Lấy tài liệu được gợi ý cho người dùng dựa trên thể loại yêu thích
     */
    public List<Document> getRecommendedDocuments(String userId) {
        User user = userRepository.findById(userId);
        if (user == null || user.getFavoriteGenres().isEmpty()) {
            return getPopularDocuments(); 
        }
        
        List<Document> recommended = new ArrayList<>();
        for (String genre : user.getFavoriteGenres()) {
            List<Document> genreDocuments = documentRepository.findByGenre(genre).stream()
                    .filter(Document::isAvailable)
                    .collect(Collectors.toList());
            recommended.addAll(genreDocuments);
        }
        
        return recommended.stream()
                .distinct()
                .sorted((d1, d2) -> Double.compare(getDocumentAverageRating(d2.getId()), 
                                                 getDocumentAverageRating(d1.getId())))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tài liệu phổ biến (được đánh giá cao nhất)
     */
    public List<Document> getPopularDocuments() {
        return documentRepository.findAvailable().stream()
                .sorted((d1, d2) -> Double.compare(getDocumentAverageRating(d2.getId()), 
                                                 getDocumentAverageRating(d1.getId())))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    // ================ THỐNG KÊ ================
    
    /**
     * Lấy thống kê thư viện
     */
    public Map<String, Object> getLibraryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Document> allDocuments = documentRepository.findAll();
        List<Document> availableDocuments = documentRepository.findAvailable();
        List<User> allUsers = userRepository.findAll();
        List<LoanTransaction> allTransactions = transactionRepository.findAll();
        List<LoanTransaction> overdueTransactions = transactionRepository.findOverdueTransactions();
        
        stats.put("totalDocuments", allDocuments.size());
        stats.put("availableDocuments", availableDocuments.size());
        stats.put("borrowedDocuments", allDocuments.size() - availableDocuments.size());
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", allUsers.stream().filter(User::isActive).count());
        stats.put("totalTransactions", allTransactions.size());
        stats.put("overdueTransactions", overdueTransactions.size());
        stats.put("totalReviews", reviewRepository.findAll().size());
        
        return stats;
    }
    

}
