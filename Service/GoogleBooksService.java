import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service để gọi Google Books API và lấy thông tin sách theo ISBN
 */
public class GoogleBooksService {
    private static final String API_KEY = "AIzaSyBVCKD8olrb3Vhmpf12duoS9kued85DfVA";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    
    /**
     * Tìm kiếm sách theo ISBN
     * @param isbn ISBN của sách cần tìm
     * @return BookInfo object chứa thông tin sách hoặc null nếu không tìm thấy
     */
    public BookInfo searchByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        
        // Làm sạch ISBN (bỏ dấu gạch ngang và khoảng trắng)
        String cleanISBN = isbn.replaceAll("[\\-\\s]", "");
        
        try {
            // Tạo URL với query parameter
            String encodedISBN = URLEncoder.encode("isbn:" + cleanISBN, StandardCharsets.UTF_8.toString());
            String urlString = BASE_URL + "?q=" + encodedISBN + "&key=" + API_KEY;
            
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000); // 10 seconds
            connection.setRequestProperty("Accept", "application/json");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.err.println("API call failed with response code: " + responseCode);
                return null;
            }
            
            // Đọc response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            
            return parseBookInfo(response.toString());
            
        } catch (IOException e) {
            System.err.println("Error calling Google Books API: " + e.getMessage());
            return null;
        }
    }
