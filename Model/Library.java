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
} 

