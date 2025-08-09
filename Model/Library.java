import com.library.repository.*;

public class Library {
    private static Library instance;
    private String libraryName;
    private String address;

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

    private Library() {
        this.libraryName = "Digital Library Management System";
        this.address = "123 Library Street, Knowledge City";

        this.documentRepository = new MySQLDocumentRepository();
        this.userRepository = new MySQLUserRepository();
        this.transactionRepository = new MySQLLoanTransactionRepository();
        this.reviewRepository = new MySQLReviewRepository();

        this.nextDocumentId = getNextIdFromDatabase("documents", "DOC") + 1;
        this.nextUserId = getNextIdFromDatabase("users", "USER") + 1;
        this.nextTransactionId = getNextIdFromDatabase("loan_transactions", "TXN") + 1;
        this.nextReviewId = getNextIdFromDatabase("reviews", "REV") + 1;
    }

    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public void setRepositories(DocumentRepository documentRepo, UserRepository userRepo,
                                 LoanTransactionRepository transactionRepo, ReviewRepository reviewRepo) {
        this.documentRepository = documentRepo;
        this.userRepository = userRepo;
        this.transactionRepository = transactionRepo;
        this.reviewRepository = reviewRepo;
    }

    private int getNextIdFromDatabase(String tableName, String prefix) {
        try {
            String sql = "SELECT MAX(CAST(SUBSTRING(id, " + (prefix.length() + 1) + ") AS UNSIGNED)) FROM " + tableName;

            java.sql.Connection conn = com.library.database.DatabaseConnection.getConnection();
            if (conn == null) return 0;

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
            System.err.println("Warning: Could not get max ID from " + tableName + ": " + e.getMessage());
        }
        return 0;
    }

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

    // Quản lý sách
        public boolean addDocument(Document document) {
    if (document == null) {
        return false;
    }
    
    if (document.getId() == null || document.getId().isEmpty()) {
        document.setId("DOC" + String.format("%04d", nextDocumentId++));
    }
    
    if (documentRepository.findById(document.getId()) != null) {
        return false;
    }
    
    return documentRepository.save(document);
}

public boolean removeDocument(String documentId) {
    if (documentId == null || documentRepository.findById(documentId) == null) {
        return false;
    }
    
    if (isDocumentBorrowed(documentId)) {
        return false;
    }
    
    List<Review> documentReviews = reviewRepository.findByDocumentId(documentId);
    for (Review review : documentReviews) {
        reviewRepository.delete(review.getId());
    }
    
    return documentRepository.delete(documentId);
}

public boolean updateDocument(Document document) {
    if (document == null || document.getId() == null || 
        documentRepository.findById(document.getId()) == null) {
        return false;
    }
    
    return documentRepository.update(document);
}

public Document getDocument(String documentId) {
    return documentRepository.findById(documentId);
}

public List<Document> getAllDocuments() {
    return documentRepository.findAll();
}

public List<Document> searchDocumentsByTitle(String title) {
    return documentRepository.findByTitle(title);
}

public List<Document> searchDocumentsByAuthor(String author) {
    return documentRepository.findByAuthor(author);
}

public List<Document> searchDocumentsByGenre(String genre) {
    return documentRepository.findByGenre(genre);
}

public List<Document> getAvailableDocuments() {
    return documentRepository.findAvailable();
}

public List<Document> getDocumentsByType(Class<? extends Document> type) {
    return getAllDocuments();
}

public boolean addUser(User user) {
    if (user == null) {
        return false;
    }
    
    if (user.getId() == null || user.getId().isEmpty()) {
        user.setId("USER" + String.format("%04d", nextUserId++));
    }
    
    if (userRepository.findById(user.getId()) != null) {
        return false;
    }
    
    return userRepository.save(user);
}

public boolean removeUser(String userId) {
    if (userId == null || userRepository.findById(userId) == null) {
        return false;
    }
    
    User user = userRepository.findById(userId);
    if (user.getBorrowedCount() > 0) {
        return false;
    }
    
    List<Review> userReviews = reviewRepository.findByUserId(userId);
    for (Review review : userReviews) {
        reviewRepository.delete(review.getId());
    }
    
    return userRepository.delete(userId);
}

public boolean updateUser(User user) {
    if (user == null || user.getId() == null || 
        userRepository.findById(user.getId()) == null) {
        return false;
    }
    
    return userRepository.update(user);
}

public User getUser(String userId) {
    return userRepository.findById(userId);
}

public List<User> getAllUsers() {
    return userRepository.findAll();
}

public List<User> searchUsersByName(String name) {
    return userRepository.findByName(name);
}} 



