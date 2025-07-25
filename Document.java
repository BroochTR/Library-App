

import java.time.LocalDate;
import java.util.Objects;

public abstract class Document {
    private String id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private String description;
    private LocalDate addedDate;
    
    //Constructor
    public Document(String id, String title, String author, String genre, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.addedDate = LocalDate.now();
        this.description = "";
    }
    

    public Document() {
        this.addedDate = LocalDate.now();
        this.description = "";
    }
    // Setter va Getter
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
       
    /**
     * In thong tin tai lieu
     */
    public String getInfo() {
        return String.format("%s - %s by %s (%d)", id, title, author, year);
    }
}     
