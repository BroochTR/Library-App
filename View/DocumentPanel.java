package com.library.view;

import com.library.model.*;
import com.library.service.GoogleBooksService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel để quản lý tài liệu 
 */
public class DocumentPanel extends JPanel implements RefreshablePanel {
    private Library library;
    private JTable documentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JComboBox<String> documentTypeCombo;
    private GoogleBooksService googleBooksService;
    
    public DocumentPanel(Library library) {
        this.library = library;
        this.googleBooksService = new GoogleBooksService();
        initializePanel();
        refreshData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder("Document Management"));
        
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UITheme.FONT_TITLE);
        searchLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(searchLabel);
        
        searchField = UITheme.createTextField("Enter search term...");
        searchField.setPreferredSize(new Dimension(200, UITheme.INPUT_HEIGHT));
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);
        
        searchTypeCombo = new JComboBox<>(new String[]{"Title", "Author", "Genre", "All"});
        searchTypeCombo.setFont(UITheme.FONT_BODY);
        searchTypeCombo.setPreferredSize(new Dimension(100, UITheme.INPUT_HEIGHT));
        searchTypeCombo.addActionListener(e -> performSearch());
        panel.add(searchTypeCombo);
        
        JButton searchButton = UITheme.createPrimaryButton("Search Books");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton);
        
        JButton clearButton = UITheme.createSecondaryButton("Clear Search");
        clearButton.addActionListener(e -> {
            searchField.setText("Enter search term...");
            searchField.setForeground(UITheme.TEXT_MUTED);
            refreshData();
        });
        panel.add(clearButton);
        
        panel.add(Box.createHorizontalStrut(20));
        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(UITheme.FONT_TITLE);
        typeLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(typeLabel);
        
        documentTypeCombo = new JComboBox<>(new String[]{"All Documents", "Books Only"});
        documentTypeCombo.setFont(UITheme.FONT_BODY);
        documentTypeCombo.setPreferredSize(new Dimension(120, UITheme.INPUT_HEIGHT));
        documentTypeCombo.addActionListener(e -> refreshData());
        panel.add(documentTypeCombo);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID", "Title", "Author", "Genre", "Year", "Status", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Đặt bảng ở chế độ chỉ đọc
            }
        };
        
        documentTable = new JTable(tableModel);
        documentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentTable.setRowHeight(25);
        documentTable.getTableHeader().setReorderingAllowed(false);
        
        documentTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        documentTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Title
        documentTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        documentTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Genre
        documentTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Year
        documentTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
        documentTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Details
        
        JScrollPane scrollPane = new JScrollPane(documentTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton addBookButton = UITheme.createPrimaryButton("Add");
        addBookButton.addActionListener(e -> showAddBookDialog());
        panel.add(addBookButton);
        
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showDocumentDetails());
        panel.add(viewButton);
        
        JButton editButton = UITheme.createWarningButton("Edit");
        editButton.addActionListener(e -> showEditDialog());
        panel.add(editButton);
        
        JButton deleteButton = UITheme.createDangerButton("Delete");
        deleteButton.addActionListener(e -> deleteDocument());
        panel.add(deleteButton);
        
        JButton refreshButton = UITheme.createSuccessButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        tableModel.setRowCount(0); 
        
        List<Document> documents;
        String selectedType = (String) documentTypeCombo.getSelectedItem();
        
        switch (selectedType) {
            case "Books Only":
                documents = library.getAllDocuments(); 
                break;
            default:
                documents = library.getAllDocuments();
                break;
        }
        
        for (Document doc : documents) {
            String details = "ISBN: " + doc.getIsbn() + " | " + doc.getPageCount() + " pages";
            String status = doc.getQuantityStatus(); 
            
            Object[] row = {
                doc.getId(),
                doc.getTitle(),
                doc.getAuthor(),
                doc.getGenre(),
                doc.getYear(),
                status,
                details
            };
            tableModel.addRow(row);
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshData();
            return;
        }
        
        tableModel.setRowCount(0);
        String searchType = (String) searchTypeCombo.getSelectedItem();
        List<Document> results;
        
        switch (searchType) {
            case "Title":
                results = library.searchDocumentsByTitle(searchText);
                break;
            case "Author":
                results = library.searchDocumentsByAuthor(searchText);
                break;
            case "Genre":
                results = library.searchDocumentsByGenre(searchText);
                break;
            default: 
                results = library.getAllDocuments().stream()
                    .filter(doc -> doc.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                  doc.getAuthor().toLowerCase().contains(searchText.toLowerCase()) ||
                                  doc.getGenre().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();
                break;
        }
        
        for (Document doc : results) {
            String details = "ISBN: " + doc.getIsbn() + " | " + doc.getPageCount() + " pages";
            String status = doc.getQuantityStatus(); 
            
            Object[] row = {
                doc.getId(),
                doc.getTitle(),
                doc.getAuthor(),
                doc.getGenre(),
                doc.getYear(),
                status,
                details
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField genreField = new JTextField(15);
        JTextField yearField = new JTextField(10);
        JTextField isbnField = new JTextField(15);
        JTextField publisherField = new JTextField(20);
        JTextField pagesField = new JTextField(10);
        JTextField quantityField = new JTextField("1", 10);
        JTextArea descriptionArea = new JTextArea(3, 20);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1;
        dialog.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        dialog.add(yearField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 4;
        JButton autoFillButton = UITheme.createSuccessButton("Auto Fill");
        autoFillButton.setToolTipText("Auto fill information from Google Books API");
        autoFillButton.addActionListener(e -> autoFillFromISBN(
            isbnField, titleField, authorField, genreField, yearField, publisherField, pagesField, descriptionArea, dialog
        ));
        dialog.add(autoFillButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        dialog.add(publisherField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Pages:"), gbc);
        gbc.gridx = 1;
        dialog.add(pagesField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(new JLabel("Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(descriptionArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = UITheme.createPrimaryButton("Save Book");
        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String isbn = isbnField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title and Author are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Number must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Kiểm tra xem tài liệu đã tồn tại 
                Document existingDoc = findExistingDocument(title, author, isbn);
                
                if (existingDoc != null) {
                    existingDoc.addQuantity(quantity);
                    if (library.updateDocument(existingDoc)) {
                        refreshData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, 
                            String.format("Added %d books. Total quantity: %d", quantity, existingDoc.getTotalQuantity())
                        );
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update book quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    Document doc = new Document("", title, author,
                       genreField.getText(), Integer.parseInt(yearField.getText()),
                       isbn, publisherField.getText(),
                       Integer.parseInt(pagesField.getText()), quantity);
                    doc.setDescription(descriptionArea.getText());
                    
                    if (library.addDocument(doc)) {
                        refreshData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, 
                            String.format("Document added successfully! Quantity: %d", quantity)
                        );
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add document!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter valid numbers for year, pages, and number!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Tìm tài liệu đã tồn tại theo tiêu đề, tác giả và ISBN.
     */
    private Document findExistingDocument(String title, String author, String isbn) {
        for (Document doc : library.getAllDocuments()) {
            boolean titleMatch = doc.getTitle().equalsIgnoreCase(title);
            boolean authorMatch = doc.getAuthor().equalsIgnoreCase(author);
            boolean isbnMatch = (isbn.isEmpty() && doc.getIsbn().isEmpty()) ||
                               doc.getIsbn().equalsIgnoreCase(isbn);
            
            if (titleMatch && authorMatch && isbnMatch) {
                return doc;
            }
        }
        return null;
    }
    
    private void showDocumentDetails() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String documentId = (String) tableModel.getValueAt(selectedRow, 0);
        Document document = library.getDocument(documentId);
        
        if (document != null) {
            StringBuilder details = new StringBuilder();
            details.append("=== DOCUMENT DETAILS ===\n");
            details.append("ID: ").append(document.getId()).append("\n");
            details.append("Type: ").append(document.getDocumentType()).append("\n");
            details.append("Title: ").append(document.getTitle()).append("\n");
            details.append("Author: ").append(document.getAuthor()).append("\n");
            details.append("Genre: ").append(document.getGenre()).append("\n");
            details.append("Year: ").append(document.getYear()).append("\n");
            
            String statusDisplay = document.getQuantityStatus(); 
            details.append("Status: ").append(statusDisplay).append("\n");
            details.append("Added Date: ").append(document.getAddedDate()).append("\n");
            
            details.append("\n=== BOOK DETAILS ===\n");
            details.append("ISBN: ").append(document.getIsbn()).append("\n");
            details.append("Publisher: ").append(document.getPublisher()).append("\n");
            details.append("Pages: ").append(document.getPageCount()).append("\n");
            details.append("Language: ").append(document.getLanguage()).append("\n");
            details.append("Edition: ").append(document.getEdition()).append("\n");
            
            if (!document.getDescription().isEmpty()) {
                details.append("\nDescription: ").append(document.getDescription()).append("\n");
            }
            
            List<Review> reviews = library.getDocumentReviews(documentId);
            if (!reviews.isEmpty()) {
                details.append("\n=== REVIEWS ===\n");
                double avgRating = library.getDocumentAverageRating(documentId);
                details.append("Average Rating: ").append(String.format("%.1f", avgRating)).append("/5\n");
                details.append("Total Reviews: ").append(reviews.size()).append("\n\n");
                
                for (Review review : reviews) {
                    details.append(review.getRatingAsStars()).append(" (").append(review.getRating()).append("/5)");
                    details.append("\n");
                    if (review.hasComment()) {
                        details.append(review.getComment()).append("\n");
                    }
                    details.append("---\n");
                }
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 13));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Document Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showEditDialog() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String documentId = (String) tableModel.getValueAt(selectedRow, 0);
        Document document = library.getDocument(documentId);
        
        if (document == null) {
            JOptionPane.showMessageDialog(this, "Document not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hiện tất cả tài liệu có thể được chỉnh sửa
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tạo trường nhập với giá trị hiện tại
        JTextField titleField = new JTextField(document.getTitle(), 20);
        JTextField authorField = new JTextField(document.getAuthor(), 20);
        JTextField genreField = new JTextField(document.getGenre(), 15);
        JTextField yearField = new JTextField(String.valueOf(document.getYear()), 10);
        JTextField isbnField = new JTextField(document.getIsbn(), 15);
        JTextField publisherField = new JTextField(document.getPublisher(), 20);
        JTextField pagesField = new JTextField(String.valueOf(document.getPageCount()), 10);
        JTextArea descriptionArea = new JTextArea(document.getDescription(), 3, 20);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1;
        dialog.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        dialog.add(yearField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        dialog.add(publisherField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Pages:"), gbc);
        gbc.gridx = 1;
        dialog.add(pagesField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(descriptionArea), gbc);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                // Kiểm tra các trường bắt buộc
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title and Author are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                document.setTitle(title);
                document.setAuthor(author);
                document.setGenre(genreField.getText().trim());
                document.setYear(Integer.parseInt(yearField.getText().trim()));
                document.setIsbn(isbnField.getText().trim());
                document.setPublisher(publisherField.getText().trim());
                document.setPageCount(Integer.parseInt(pagesField.getText().trim()));
                document.setDescription(descriptionArea.getText().trim());
                
                if (library.updateDocument(document)) {
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Document updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update document!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for year and pages!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteDocument() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String documentId = (String) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete \"" + title + "\"?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YESOption) {
            if (library.removeDocument(documentId)) {
                refreshData();
                JOptionPane.showMessageDialog(this, "Document deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot delete document. It may be currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Tự động điền thông tin sách từ ISBN sử dụng Google Books API.
     */
    private void autoFillFromISBN(JTextField isbnField, JTextField titleField, JTextField authorField,
                                  JTextField genreField, JTextField yearField, JTextField publisherField,
                                  JTextField pagesField, JTextArea descriptionArea, JDialog parentDialog) {
        String isbn = isbnField.getText().trim();
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(parentDialog, "Please enter ISBN before Auto Fill!", "ISBN missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog loadingDialog = new JDialog(parentDialog, "Finding...", true);
        JLabel loadingLabel = new JLabel("Waiting...", JLabel.CENTER);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loadingDialog.add(loadingLabel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(parentDialog);
        
        SwingWorker<GoogleBooksService.BookInfo, Void> worker = new SwingWorker<>() {
            @Override
            protected GoogleBooksService.BookInfo doInBackground() throws Exception {
                return googleBooksService.searchByISBN(isbn);
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    GoogleBooksService.BookInfo bookInfo = get();
                    if (bookInfo != null && bookInfo.isValid()) {
                        
                        if (bookInfo.title != null && !bookInfo.title.isEmpty()) {
                            titleField.setText(bookInfo.title);
                        }
                        
                        if (bookInfo.author != null && !bookInfo.author.isEmpty()) {
                            authorField.setText(bookInfo.author);
                        }
                        
                        if (bookInfo.genre != null && !bookInfo.genre.isEmpty()) {
                            genreField.setText(bookInfo.genre);
                        }
                        
                        if (bookInfo.year > 0) {
                            yearField.setText(String.valueOf(bookInfo.year));
                        }
                        
                        if (bookInfo.publisher != null && !bookInfo.publisher.isEmpty()) {
                            publisherField.setText(bookInfo.publisher);
                        }
                        
                        if (bookInfo.pageCount > 0) {
                            pagesField.setText(String.valueOf(bookInfo.pageCount));
                        }
                        
                        if (bookInfo.description != null && !bookInfo.description.isEmpty()) {
                            descriptionArea.setText(bookInfo.description);
                        }
                        
                        if (bookInfo.isbn != null && !bookInfo.isbn.isEmpty()) {
                            isbnField.setText(bookInfo.isbn);
                        }
                        
                        // Tô highlight các trường vừa được tự động điền bằng màu xanh nhẹ
                        Color highlightColor = new Color(220, 255, 220); 
                        Color originalColor = Color.WHITE;
                        
                        if (!titleField.getText().isEmpty()) titleField.setBackground(highlightColor);
                        if (!authorField.getText().isEmpty()) authorField.setBackground(highlightColor);
                        if (!publisherField.getText().isEmpty()) publisherField.setBackground(highlightColor);
                        if (!genreField.getText().isEmpty()) genreField.setBackground(highlightColor);
                        if (!yearField.getText().isEmpty()) yearField.setBackground(highlightColor);
                        if (!pagesField.getText().isEmpty()) pagesField.setBackground(highlightColor);
                        if (!descriptionArea.getText().isEmpty()) descriptionArea.setBackground(highlightColor);
                        
                        // Sau 2 giây, trả về màu mặc định
                        Timer timer = new Timer(2000, evt -> {
                            titleField.setBackground(originalColor);
                            authorField.setBackground(originalColor);
                            publisherField.setBackground(originalColor);
                            genreField.setBackground(originalColor);
                            yearField.setBackground(originalColor);
                            pagesField.setBackground(originalColor);
                            descriptionArea.setBackground(originalColor);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        JOptionPane.showMessageDialog(parentDialog, 
                            "Cannot find information with ISBN: " + isbn + "\n" +
                            "Please check again or type manually", 
                            "Error", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parentDialog, 
                        "Error when calling Google Books API: " + e.getMessage() + "\n" +
                        "Please check the Internet and try again", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }
}
