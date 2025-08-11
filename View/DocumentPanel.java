package com.library.view;

import com.library.model.*;
import com.library.service.GoogleBooksService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel để quản lý tài liệu (sách và luận văn)
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
        
        // Tạo panel trên cùng cho tìm kiếm và bộ lọc
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Tạo panel giữa với bảng dữ liệu
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Tạo panel dưới cùng với các nút
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Các thành phần tìm kiếm
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
        
        // Lọc theo loại tài liệu
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
        
        // Tạo bảng dữ liệu
        String[] columnNames = {"ID", "Title", "Author", "Genre", "Year", "Status", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Làm cho bảng chỉ đọc
            }
        };
        
        documentTable = new JTable(tableModel);
        documentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentTable.setRowHeight(25);
        documentTable.getTableHeader().setReorderingAllowed(false);
        
        // Thiết lập độ rộng cột
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
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Thêm khoảng cách giữa các nút
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton addBookButton = UITheme.createPrimaryButton("+ Add Book");
        addBookButton.addActionListener(e -> showAddBookDialog());
        panel.add(addBookButton);
        
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showDocumentDetails());
        panel.add(viewButton);
        
        JButton editButton = UITheme.createWarningButton("Edit Document");
        editButton.addActionListener(e -> showEditDialog());
        panel.add(editButton);
        
        JButton deleteButton = UITheme.createDangerButton("Delete Document");
        deleteButton.addActionListener(e -> deleteDocument());
        panel.add(deleteButton);
        
        JButton refreshButton = UITheme.createSuccessButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        tableModel.setRowCount(0); // Xóa dữ liệu hiện tại
        
        List<Document> documents;
        String selectedType = (String) documentTypeCombo.getSelectedItem();
        
        switch (selectedType) {
            case "Books Only":
                documents = library.getAllDocuments(); // Tất cả tài liệu hiện tại đều là sách
                break;
            default:
                documents = library.getAllDocuments();
                break;
        }
        
        for (Document doc : documents) {
            String details = "ISBN: " + doc.getIsbn() + " | " + doc.getPageCount() + " pages";
            String status = doc.getQuantityStatus(); // Hiển thị định dạng có sẵn/tổng số
            
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
            default: // "Tất cả"
                results = library.getAllDocuments().stream()
                    .filter(doc -> doc.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                  doc.getAuthor().toLowerCase().contains(searchText.toLowerCase()) ||
                                  doc.getGenre().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();
                break;
        }
        
        for (Document doc : results) {
            String details = "ISBN: " + doc.getIsbn() + " | " + doc.getPageCount() + " pages";
            String status = doc.getQuantityStatus(); // Hiển thị định dạng có sẵn/tổng số
            
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
        
        // Tạo các trường nhập liệu
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField genreField = new JTextField(15);
        JTextField yearField = new JTextField(10);
        JTextField isbnField = new JTextField(15);
        JTextField publisherField = new JTextField(20);
        JTextField pagesField = new JTextField(10);
        JTextField quantityField = new JTextField("1", 10);
        JTextArea descriptionArea = new JTextArea(3, 20);
        
        // Thêm các thành phần vào dialog
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
        
        // Nút tự động điền
        gbc.gridx = 2; gbc.gridy = 4;
        JButton autoFillButton = UITheme.createSuccessButton("Auto Fill from ISBN");
        autoFillButton.setToolTipText("Tự động điền thông tin từ Google Books API");
        autoFillButton.addActionListener(e -> autoFillFromISBN(isbnField, titleField, authorField, genreField, yearField, publisherField, pagesField, descriptionArea, dialog));
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
        
        // Các nút
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
                    JOptionPane.showMessageDialog(dialog, "Tiêu đề và Tác giả là bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Kiểm tra xem tài liệu đã tồn tại chưa (theo tiêu đề, tác giả và ISBN)
                Document existingDoc = findExistingDocument(title, author, isbn);
                
                if (existingDoc != null) {
                    // Tài liệu đã tồn tại, thêm số lượng
                    existingDoc.addQuantity(quantity);
                    if (library.updateDocument(existingDoc)) {
                        refreshData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, 
                            String.format("Added %d books. Total quantity: %d", quantity, existingDoc.getTotalQuantity()));
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Không thể cập nhật số lượng sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Tài liệu mới
                    Document doc = new Document("", title, author,
                                       genreField.getText(), Integer.parseInt(yearField.getText()),
                                       isbn, publisherField.getText(),
                                       Integer.parseInt(pagesField.getText()), quantity);
                    doc.setDescription(descriptionArea.getText());
                    
                    if (library.addDocument(doc)) {
                        refreshData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, String.format("Thêm tài liệu thành công! Số lượng: %d", quantity));
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Không thể thêm tài liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ cho năm, số trang và số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
