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
        
        String cleanISBN = isbn.replaceAll("[\\-\\s]", "");
        
        try {
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
    
    /**
     * Parse JSON response từ Google Books API
     */
    private BookInfo parseBookInfo(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            
            if (!root.has("items") || root.getJSONArray("items").length() == 0) {
                return null;
            }
            
            JSONObject item = root.getJSONArray("items").getJSONObject(0);
            JSONObject volumeInfo = item.getJSONObject("volumeInfo");
            
            BookInfo bookInfo = new BookInfo();
            
            if (volumeInfo.has("title")) {
                bookInfo.title = volumeInfo.getString("title");
            }
            
            if (volumeInfo.has("authors")) {
                JSONArray authors = volumeInfo.getJSONArray("authors");
                StringBuilder authorString = new StringBuilder();
                for (int i = 0; i < authors.length(); i++) {
                    if (i > 0) authorString.append(", ");
                    authorString.append(authors.getString(i));
                }
                bookInfo.author = authorString.toString();
            }
            
            if (volumeInfo.has("publisher")) {
                bookInfo.publisher = volumeInfo.getString("publisher");
            }
            
            if (volumeInfo.has("publishedDate")) {
                String publishedDate = volumeInfo.getString("publishedDate");
                try {
                    bookInfo.year = Integer.parseInt(publishedDate.substring(0, 4));
                } catch (Exception e) {
                    bookInfo.year = 0;
                }
            }
            
            if (volumeInfo.has("pageCount")) {
                bookInfo.pageCount = volumeInfo.getInt("pageCount");
            }
            
            if (volumeInfo.has("categories")) {
                JSONArray categories = volumeInfo.getJSONArray("categories");
                if (categories.length() > 0) {
                    bookInfo.genre = categories.getString(0);
                }
            }
            
            if (volumeInfo.has("description")) {
                bookInfo.description = volumeInfo.getString("description");
                // Giới hạn độ dài description
                if (bookInfo.description.length() > 500) {
                    bookInfo.description = bookInfo.description.substring(0, 500) + "...";
                }
            }
            
            if (volumeInfo.has("language")) {
                bookInfo.language = volumeInfo.getString("language");
            }

            if (volumeInfo.has("industryIdentifiers")) {
                JSONArray identifiers = volumeInfo.getJSONArray("industryIdentifiers");
                for (int i = 0; i < identifiers.length(); i++) {
                    JSONObject identifier = identifiers.getJSONObject(i);
                    String type = identifier.getString("type");
                    if ("ISBN_13".equals(type) || "ISBN_10".equals(type)) {
                        bookInfo.isbn = identifier.getString("identifier");
                        break;
                    }
                }
            }
            
            return bookInfo;
            
        } catch (Exception e) {
            System.err.println("Error parsing Google Books API response: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Class để chứa thông tin sách từ Google Books API
     */
    public static class BookInfo {
        public String title;
        public String author;
        public String publisher;
        public int year;
        public int pageCount;
        public String genre;
        public String description;
        public String language;
        public String isbn;
        
        public boolean isValid() {
            return title != null && !title.trim().isEmpty() &&
                   author != null && !author.trim().isEmpty();
        }
        
        @Override
        public String toString() {
            return String.format("BookInfo{title='%s', author='%s', publisher='%s', year=%d, pages=%d}", 
                               title, author, publisher, year, pageCount);
        }
    }
} 
