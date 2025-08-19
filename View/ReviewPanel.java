package com.library.view;

import com.library.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Bảng quản lý đánh giá và xếp hạng tài liệu.
 */
public class ReviewPanel extends JPanel implements RefreshablePanel {
    private Library library;
    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    /**
     * Khởi tạo panel quản lý đánh giá.
     * @param library đối tượng thư viện để truy xuất dữ liệu
     */
    public ReviewPanel(Library library) {
        this.library = library;
        initializePanel();
        refreshData();
    }
    
    /**
     * Khởi tạo bố cục tổng thể cho panel.
     */
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder("Review Management"));
        
        // Tạo panel trên cùng cho tìm kiếm
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Tạo panel trung tâm với bảng
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Tạo panel dưới cùng với các nút chức năng
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Tạo khu vực tìm kiếm ở phía trên.
     * @return panel tìm kiếm
     */
    private JPanel createTopPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel searchLabel = new JLabel("Search Reviews:");
        searchLabel.setFont(UITheme.FONT_TITLE);
        searchLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(searchLabel);
        
        searchField = UITheme.createTextField("Enter document title or reviewer...");
        searchField.setPreferredSize(new Dimension(250, UITheme.INPUT_HEIGHT));
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);
        
        JButton searchButton = UITheme.createPrimaryButton("Search Reviews");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton);
        
        JButton clearButton = UITheme.createSecondaryButton("Clear Search");
        clearButton.addActionListener(e -> {
            searchField.setText("Enter document title or reviewer...");
            searchField.setForeground(UITheme.TEXT_MUTED);
            refreshData();
        });
        panel.add(clearButton);
        
        return panel;
    }
    
    /**
     * Tạo khu vực trung tâm với bảng danh sách đánh giá.
     * @return panel trung tâm
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tạo bảng
        String[] columnNames = {"Review ID", "Document", "User", "Rating", "Comment", "Date", "Helpful"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bảng chỉ đọc
            }
        };
        
        reviewTable = new JTable(tableModel);
        reviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reviewTable.setRowHeight(25);
        reviewTable.getTableHeader().setReorderingAllowed(false);
        
        // Thiết lập độ rộng cột
        reviewTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã đánh giá
        reviewTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tài liệu
        reviewTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Người dùng
        reviewTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Điểm
        reviewTable.getColumnModel().getColumn(4).setPreferredWidth(300); // Bình luận
        reviewTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Ngày
        reviewTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Hữu ích
        
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tạo khu vực nút chức năng phía dưới.
     * @return panel dưới cùng
     */
    private JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton addButton = UITheme.createPrimaryButton("+ Add Review");
        addButton.addActionListener(e -> showAddReviewDialog());
        panel.add(addButton);
        
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showReviewDetails());
        panel.add(viewButton);
        
        JButton helpfulButton = UITheme.createSuccessButton("Mark Helpful");
        helpfulButton.addActionListener(e -> markHelpful());
        panel.add(helpfulButton);
        
        JButton refreshButton = UITheme.createSecondaryButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        tableModel.setRowCount(0); // Xóa dữ liệu hiện có
        
        // Lấy toàn bộ đánh giá từ tất cả tài liệu
        List<Document> documents = library.getAllDocuments();
        for (Document document : documents) {
            List<Review> reviews = library.getDocumentReviews(document.getId());
            for (Review review : reviews) {
                User user = library.getUser(review.getUserId());
                
                String userName = user != null ? user.getName() : "Unknown";
                String comment = review.hasComment() ? 
                    (review.getComment().length() > 50 ? 
                     review.getComment().substring(0, 47) + "..." : 
                     review.getComment()) : "[No comment]";
                
                Object[] row = {
                    review.getId(),
                    document.getTitle(),
                    userName,
                    review.getRatingAsStars() + " (" + review.getRating() + "/5)",
                    comment,
                    review.getReviewDate().toLocalDate(),
                    review.getHelpfulVotes()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    /**
     * Thực hiện tìm kiếm đánh giá theo tiêu đề tài liệu hoặc người đánh giá.
     */
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshData();
            return;
        }
        
        tableModel.setRowCount(0);
        
        List<Document> documents = library.getAllDocuments();
        for (Document document : documents) {
            if (document.getTitle().toLowerCase().contains(searchText)) {
                List<Review> reviews = library.getDocumentReviews(document.getId());
                for (Review review : reviews) {
                    User user = library.getUser(review.getUserId());
                    String userName = user != null ? user.getName() : "Unknown";
                    String comment = review.hasComment() ? 
                        (review.getComment().length() > 50 ? 
                         review.getComment().substring(0, 47) + "..." : 
                         review.getComment()) : "[No comment]";
                    
                    Object[] row = {
                        review.getId(),
                        document.getTitle(),
                        userName,
                        review.getRatingAsStars() + " (" + review.getRating() + "/5)",
                        comment,
                        review.getReviewDate().toLocalDate(),
                        review.getHelpfulVotes()
                    };
                    tableModel.addRow(row);
                }
            }
        }
    }
    
    /**
     * Hiển thị hộp thoại thêm đánh giá mới.
     */
    private void showAddReviewDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Review", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Chọn người dùng
        List<User> users = library.getAllUsers().stream()
            .filter(User::isActive)
            .toList();
        JComboBox<User> userCombo = new JComboBox<>();
        for (User user : users) {
            userCombo.addItem(user);
        }
        userCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User user = (User) value;
                    setText(user.getName() + " (" + user.getId() + ")");
                }
                return this;
            }
        });
        
        // Chọn tài liệu
        List<Document> documents = library.getAllDocuments();
        JComboBox<Document> documentCombo = new JComboBox<>();
        for (Document document : documents) {
            documentCombo.addItem(document);
        }
        documentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Document) {
                    Document doc = (Document) value;
                    setText(doc.getTitle() + " - " + doc.getAuthor());
                }
                return this;
            }
        });
        
        // Chọn điểm đánh giá
        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        ratingCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Integer) {
                    int rating = (Integer) value;
                    String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
                    setText(stars + " (" + rating + "/5)");
                }
                return this;
            }
        });
        ratingCombo.setSelectedItem(5);
        
        // Ô nhập bình luận
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setWrapStyleWord(true);
        commentArea.setLineWrap(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        
        // Thêm các thành phần vào hộp thoại
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("User:"), gbc);
        gbc.gridx = 1;
        dialog.add(userCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Document:"), gbc);
        gbc.gridx = 1;
        dialog.add(documentCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 1;
        dialog.add(ratingCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST;
        dialog.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        dialog.add(commentScroll, gbc);
        
        // Các nút thao tác
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Review");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            User selectedUser = (User) userCombo.getSelectedItem();
            Document selectedDocument = (Document) documentCombo.getSelectedItem();
            Integer selectedRating = (Integer) ratingCombo.getSelectedItem();
            String comment = commentArea.getText().trim();
            
            if (selectedUser == null || selectedDocument == null || selectedRating == null) {
                JOptionPane.showMessageDialog(dialog, "Please select user, document, and rating.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (library.addReview(selectedUser.getId(), selectedDocument.getId(), selectedRating, comment)) {
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Review added successfully!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add review!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Hiển thị chi tiết của đánh giá đang chọn.
     */
    private void showReviewDetails() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a review to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String reviewId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Tìm đánh giá tương ứng
        Review review = null;
        Document document = null;
        List<Document> documents = library.getAllDocuments();
        for (Document doc : documents) {
            List<Review> reviews = library.getDocumentReviews(doc.getId());
            for (Review r : reviews) {
                if (r.getId().equals(reviewId)) {
                    review = r;
                    document = doc;
                    break;
                }
            }
            if (review != null) break;
        }
        
        if (review == null || document == null) {
            JOptionPane.showMessageDialog(this, "Review not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = library.getUser(review.getUserId());
        
        StringBuilder details = new StringBuilder();
        details.append("=== REVIEW DETAILS ===\n");
        details.append("Review ID: ").append(review.getId()).append("\n");
        details.append("Rating: ").append(review.getRatingAsStars()).append(" (").append(review.getRating()).append("/5)\n");
        details.append("Date: ").append(review.getReviewDate().toLocalDate()).append("\n");
        details.append("Helpful Votes: ").append(review.getHelpfulVotes()).append("\n");
        details.append("Recommended: ").append(review.isRecommended() ? "Yes" : "No").append("\n\n");
        
        details.append("=== DOCUMENT ===\n");
        details.append("Title: ").append(document.getTitle()).append("\n");
        details.append("Author: ").append(document.getAuthor()).append("\n");
        details.append("Type: ").append(document.getDocumentType()).append("\n");
        details.append("Average Rating: ").append(String.format("%.1f", library.getDocumentAverageRating(document.getId()))).append("/5\n\n");
        
        details.append("=== REVIEWER ===\n");
        if (user != null) {
            details.append("Name: ").append(user.getName()).append("\n");
            details.append("Type: ").append(user.getUserType()).append("\n");
        } else {
            details.append("User information not available\n");
        }
        
        details.append("\n=== COMMENT ===\n");
        if (review.hasComment()) {
            details.append(review.getComment()).append("\n");
        } else {
            details.append("[No comment provided]\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Review Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Đánh dấu đánh giá là hữu ích.
     */
    private void markHelpful() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a review to mark as helpful.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String reviewId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Gọi phương thức của Library để đánh dấu hữu ích và lưu vào cơ sở dữ liệu
        if (library.markReviewAsHelpful(reviewId)) {
            refreshData();
            JOptionPane.showMessageDialog(this, "Review marked as helpful!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark review as helpful!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 