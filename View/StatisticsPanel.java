package com.library.view;

import com.library.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Bảng hiển thị thống kê và báo cáo của thư viện.
 */
public class StatisticsPanel extends JPanel implements RefreshablePanel {
    private Library library;
    private JTextArea statsTextArea;
    private JTextArea reportsTextArea;
    
    /**
     * Khởi tạo panel thống kê và báo cáo.
     * @param library đối tượng thư viện để tổng hợp dữ liệu
     */
    public StatisticsPanel(Library library) {
        this.library = library;
        initializePanel();
        refreshData();
    }
    
    /**
     * Khởi tạo bố cục cho panel thống kê.
     */
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder("Library Statistics & Reports"));
        
        // Tạo khu vực chính với bộ chia dọc
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setBackground(UITheme.BACKGROUND_PRIMARY);
        
        // Panel trái - Thống kê nhanh
        JPanel leftPanel = createStatisticsPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Panel phải - Báo cáo chi tiết
        JPanel rightPanel = createReportsPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Panel dưới cùng với các nút thao tác
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Tạo panel thống kê nhanh (bên trái).
     * @return panel thống kê
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new BorderLayout());
        panel.setBorder(UITheme.createTitledBorder("Quick Statistics"));
        
        statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("Arial", Font.PLAIN, 13));
        statsTextArea.setLineWrap(true);
        statsTextArea.setWrapStyleWord(true);
        statsTextArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(statsTextArea);
        scrollPane.setPreferredSize(new Dimension(380, 500));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tạo panel báo cáo chi tiết (bên phải).
     * @return panel báo cáo
     */
    private JPanel createReportsPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new BorderLayout());
        panel.setBorder(UITheme.createTitledBorder("Detailed Reports"));
        
        reportsTextArea = new JTextArea();
        reportsTextArea.setEditable(false);
        reportsTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        reportsTextArea.setLineWrap(true);
        reportsTextArea.setWrapStyleWord(true);
        reportsTextArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(reportsTextArea);
        scrollPane.setPreferredSize(new Dimension(380, 500));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tạo khu vực nút thao tác phía dưới.
     * @return panel dưới cùng
     */
    private JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton refreshButton = UITheme.createPrimaryButton("Refresh Statistics");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        JButton exportButton = UITheme.createSecondaryButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        panel.add(exportButton);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        updateStatistics();
        updateReports();
    }
    
    /**
     * Cập nhật phần thống kê nhanh từ dữ liệu hiện tại của thư viện.
     */
    private void updateStatistics() {
        StringBuilder stats = new StringBuilder();
        Map<String, Object> libraryStats = library.getLibraryStatistics();
        
        stats.append("=== LIBRARY OVERVIEW ===\n");
        stats.append("Library: ").append(library.getLibraryName()).append("\n");
        stats.append("Address: ").append(library.getAddress()).append("\n\n");
        
        stats.append("=== COLLECTION STATISTICS ===\n");
        stats.append("Total Documents: ").append(libraryStats.get("totalDocuments")).append("\n");
        stats.append("Available: ").append(libraryStats.get("availableDocuments")).append("\n");
        stats.append("Borrowed: ").append(libraryStats.get("borrowedDocuments")).append("\n");
        
        // Phân loại theo loại tài liệu
        List<Document> documents = library.getAllDocuments();
        stats.append("\nDocuments: ").append(documents.size()).append("\n\n");
        
        stats.append("=== USER STATISTICS ===\n");
        stats.append("Total Users: ").append(libraryStats.get("totalUsers")).append("\n");
        
        // Phân loại theo nhóm người dùng
        List<User> users = library.getAllUsers();
        long students = users.stream().filter(u -> u.getUserType() == User.UserType.STUDENT).count();
        long faculty = users.stream().filter(u -> u.getUserType() == User.UserType.FACULTY).count();
        long staff = users.stream().filter(u -> u.getUserType() == User.UserType.STAFF).count();
        long guests = users.stream().filter(u -> u.getUserType() == User.UserType.GUEST).count();
        
        stats.append("\nStudents: ").append(students).append("\n");
        stats.append("Faculty: ").append(faculty).append("\n");
        stats.append("Staff: ").append(staff).append("\n");
        stats.append("Guests: ").append(guests).append("\n\n");
        
        stats.append("=== TRANSACTION STATISTICS ===\n");
        stats.append("Total Transactions: ").append(libraryStats.get("totalTransactions")).append("\n");
        stats.append("Overdue: ").append(libraryStats.get("overdueTransactions")).append("\n\n");
        
        stats.append("=== REVIEW STATISTICS ===\n");
        stats.append("Total Reviews: ").append(libraryStats.get("totalReviews")).append("\n");
        
        // Tính điểm trung bình trên tất cả tài liệu có đánh giá
        List<Document> allDocs = library.getAllDocuments();
        double totalRating = 0;
        int docsWithReviews = 0;
        for (Document doc : allDocs) {
            double rating = library.getDocumentAverageRating(doc.getId());
            if (rating > 0) {
                totalRating += rating;
                docsWithReviews++;
            }
        }
        double avgRating = docsWithReviews > 0 ? totalRating / docsWithReviews : 0;
        stats.append("📊 Average Rating: ").append(String.format("%.1f", avgRating)).append("/5\n");
        
        stats.append("\n=== SYSTEM SETTINGS ===\n");
        stats.append("💰 Daily Fine Rate: $").append(library.getDailyFineRate()).append("\n");
        stats.append("📅 Default Loan Period: ").append(library.getDefaultLoanDays()).append(" days\n");
        
        statsTextArea.setText(stats.toString());
    }
    
    /**
     * Cập nhật phần báo cáo chi tiết (top rated, overdue, hoạt động...).
     */
    private void updateReports() {
        StringBuilder reports = new StringBuilder();
        
        // Báo cáo tài liệu được đánh giá cao
        reports.append("=== TOP RATED DOCUMENTS ===\n");
        List<Document> popularDocs = library.getPopularDocuments();
        if (popularDocs.isEmpty()) {
            reports.append("No documents with ratings yet.\n");
        } else {
            for (int i = 0; i < Math.min(5, popularDocs.size()); i++) {
                Document doc = popularDocs.get(i);
                double rating = library.getDocumentAverageRating(doc.getId());
                int reviewCount = library.getDocumentReviews(doc.getId()).size();
                reports.append(String.format("%d. %s\n", i + 1, doc.getTitle()));
                reports.append(String.format("   Author: %s\n", doc.getAuthor()));
                reports.append(String.format("   Rating: %.1f/5 (%d reviews)\n", rating, reviewCount));
                
                // Sử dụng định dạng trạng thái số lượng (còn / tổng)
                String statusDisplay = doc.getQuantityStatus();
                reports.append(String.format("   Status: %s\n\n", statusDisplay));
            }
        }
        
        // Báo cáo quá hạn
        reports.append("=== OVERDUE DOCUMENTS ===\n");
        List<LoanTransaction> overdueTransactions = library.getOverdueTransactions();
        if (overdueTransactions.isEmpty()) {
            reports.append("No overdue documents.\n");
        } else {
            for (LoanTransaction transaction : overdueTransactions) {
                User user = library.getUser(transaction.getUserId());
                Document document = library.getDocument(transaction.getDocumentId());
                
                reports.append(String.format("• %s\n", document != null ? document.getTitle() : "Unknown"));
                reports.append(String.format("  Borrower: %s\n", user != null ? user.getName() : "Unknown"));
                reports.append(String.format("  Due Date: %s\n", transaction.getDueDate()));
                reports.append(String.format("  Days Overdue: %d\n", transaction.getDaysOverdue()));
                reports.append(String.format("  Fine: $%.2f\n\n", transaction.calculateFine(library.getDailyFineRate())));
            }
        }
        
        // Báo cáo người mượn tích cực nhất
        reports.append("=== MOST ACTIVE BORROWERS ===\n");
        List<User> users = library.getAllUsers();
        users.sort((u1, u2) -> Integer.compare(u2.getBorrowedCount(), u1.getBorrowedCount()));
        
        for (int i = 0; i < Math.min(5, users.size()); i++) {
            User user = users.get(i);
            if (user.getBorrowedCount() > 0) {
                reports.append(String.format("%d. %s (%s)\n", i + 1, user.getName(), user.getUserType()));
                reports.append(String.format("   Currently borrowed: %d/%d\n", 
                              user.getBorrowedCount(), user.getMaxBorrowLimit()));
                reports.append(String.format("   Email: %s\n\n", user.getEmail()));
            }
        }
        
        // Phân bố theo thể loại
        reports.append("=== COLLECTION BY GENRE ===\n");
        Map<String, Long> genreCounts = library.getAllDocuments().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Document::getGenre,
                java.util.stream.Collectors.counting()
            ));
        
        genreCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> 
                reports.append(String.format("• %s: %d documents\n", entry.getKey(), entry.getValue()))
            );
        
        // Hoạt động gần đây
        reports.append("\n=== RECENT ACTIVITY ===\n");
        List<LoanTransaction> recentTransactions = library.getAllTransactions().stream()
            .sorted((t1, t2) -> t2.getBorrowDate().compareTo(t1.getBorrowDate()))
            .limit(10)
            .toList();
        
        for (LoanTransaction transaction : recentTransactions) {
            User user = library.getUser(transaction.getUserId());
            Document document = library.getDocument(transaction.getDocumentId());
            
            String action = transaction.getReturnDate() != null ? "Returned" : "Borrowed";
            reports.append(String.format("• %s: %s\n", action, 
                          document != null ? document.getTitle() : "Unknown"));
            reports.append(String.format("  User: %s\n", user != null ? user.getName() : "Unknown"));
            reports.append(String.format("  Date: %s\n", 
                          transaction.getReturnDate() != null ? 
                          transaction.getReturnDate() : transaction.getBorrowDate()));
            reports.append(String.format("  Status: %s\n\n", transaction.getStatus()));
        }
        
        reportsTextArea.setText(reports.toString());
    }
    
    /**
     * Xuất báo cáo đầy đủ ra hộp thoại để sao chép hoặc xem.
     */
    private void exportReport() {
        StringBuilder fullReport = new StringBuilder();
        fullReport.append("LIBRARY MANAGEMENT SYSTEM - FULL REPORT\n");
        fullReport.append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n");
        fullReport.append("=" .repeat(50)).append("\n\n");
        
        fullReport.append("STATISTICS:\n");
        fullReport.append(statsTextArea.getText()).append("\n\n");
        
        fullReport.append("DETAILED REPORTS:\n");
        fullReport.append(reportsTextArea.getText());
        
        // Tạo hộp thoại để hiển thị báo cáo
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Export Report", true);
        dialog.setLayout(new BorderLayout());
        
        JTextArea reportArea = new JTextArea(fullReport.toString());
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Arial", Font.PLAIN, 11));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> {
            reportArea.selectAll();
            reportArea.copy();
            JOptionPane.showMessageDialog(dialog, "Report copied to clipboard!");
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(copyButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 