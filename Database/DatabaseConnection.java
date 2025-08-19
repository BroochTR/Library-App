import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối cơ sở dữ liệu.
 * Xử lý kết nối MySQL sử dụng mẫu Singleton
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Minh1234*"; 
    
    private static Connection connection = null;
    
    /**
     * Lấy kết nối đến cơ sở dữ liệu.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection newConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Successfully connected to the database!");
            return newConnection;
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MySQL Driver: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Kết nối cơ sở dữ liệu thất bại: " + e.getMessage());
            System.err.println("Vui lòng kiểm tra MySQL đã chạy và thông tin đăng nhập chính xác");
            return null;
        }
    }
    
    /**
     * Đóng kết nối cơ sở dữ liệu
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra kết nối cơ sở dữ liệu
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Test connection error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing test connection: " + e.getMessage());
                }
            }
        }
        return false;
    }
}
