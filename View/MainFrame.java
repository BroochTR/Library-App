package com.library.view;

import com.library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Giao diện chính cho Hệ thống Quản lý Thư viện
 * Minh họa giao diện Swing với layout chuyên nghiệp
 */
public class MainFrame extends JFrame {
    private Library library;
    private JTabbedPane tabbedPane;
    
    // Các Panel
    private DocumentPanel documentPanel;
    private UserPanel userPanel;
    private LoanPanel loanPanel;
    private ReviewPanel reviewPanel;
    private StatisticsPanel statisticsPanel;
    
    public MainFrame() {
        this.library = Library.getInstance();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Library Management System - Modern Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Thiết lập giao diện hiện đại
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Thiết lập màu nền
        getContentPane().setBackground(UITheme.BACKGROUND_PRIMARY);
        
        // Tạo header với style hiện đại
        createHeader();
        
        // Tạo thanh menu
        createMenuBar();
        
        // Tạo nội dung chính với các tab
        createTabbedPane();
        
        // Thêm các component vào frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Tạo thanh trạng thái
        createStatusBar();
        
        // Thiết lập thuộc tính frame
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 800));
        
        // Thiết lập icon ứng dụng (tùy chọn)
        // setIconImage(createApplicationIcon());
    }
    
    private void createHeader() {
        JPanel headerPanel = UITheme.createPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_LARGE, UITheme.PADDING_MEDIUM, UITheme.PADDING_LARGE));
        
        // Phần tiêu đề
        JLabel titleLabel = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Phần thống kê
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setOpaque(false);
        
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UITheme.BACKGROUND_CARD);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));
        
        // Menu File
        JMenu fileMenu = new JMenu(" File ");
        fileMenu.setFont(UITheme.FONT_BODY);
        fileMenu.setMnemonic('F');
        
        JMenuItem newItem = new JMenuItem("New Library");
        newItem.setFont(UITheme.FONT_BODY);
        newItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newItem.addActionListener(e -> resetLibrary());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(UITheme.FONT_BODY);
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menu View
        JMenu viewMenu = new JMenu(" View ");
        viewMenu.setFont(UITheme.FONT_BODY);
        viewMenu.setMnemonic('V');
        
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.setFont(UITheme.FONT_BODY);
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> refreshAllPanels());
        
        viewMenu.add(refreshItem);
        
        // Menu Help
        JMenu helpMenu = new JMenu(" Help ");
        helpMenu.setFont(UITheme.FONT_BODY);
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setFont(UITheme.FONT_BODY);
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(UITheme.FONT_TITLE);
        tabbedPane.setBackground(UITheme.BACKGROUND_CARD);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_SMALL, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        // Tạo các panel
        documentPanel = new DocumentPanel(library);
        userPanel = new UserPanel(library);
        loanPanel = new LoanPanel(library);
        reviewPanel = new ReviewPanel(library);
        statisticsPanel = new StatisticsPanel(library);
        
        // Thêm các panel vào tab với style hiện đại
        tabbedPane.addTab("  Documents  ", null, documentPanel, "Quản lý sách và tài liệu");
        tabbedPane.addTab("  Users  ", null, userPanel, "Quản lý thành viên thư viện");
        tabbedPane.addTab("  Loans  ", null, loanPanel, "Xử lý mượn và trả sách");
        tabbedPane.addTab("  Reviews  ", null, reviewPanel, "Đánh giá và nhận xét");
        tabbedPane.addTab("  Statistics  ", null, statisticsPanel, "Thống kê và báo cáo");
        
        // Tạo style cho các tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, UITheme.BACKGROUND_CARD);
            tabbedPane.setForegroundAt(i, UITheme.TEXT_SECONDARY);
        }
        
        // Thiết lập listener cho tab để refresh data và cập nhật style
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof RefreshablePanel) {
                ((RefreshablePanel) selectedComponent).refreshData();
            }
            
            // Cập nhật style cho tab được chọn
            int selectedIndex = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == selectedIndex) {
                    tabbedPane.setForegroundAt(i, UITheme.PRIMARY_COLOR);
                } else {
                    tabbedPane.setForegroundAt(i, UITheme.TEXT_SECONDARY);
                }
            }
        });
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(240, 240, 240));
        
        JLabel statusLabel = new JLabel("Ready - Library Management System");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    

    
    private void resetLibrary() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the library? All data will be lost.",
            "Reset Library",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Reset thư viện (tạo instance mới)
            library = Library.getInstance();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Library has been reset. Database is now empty.");
        }
    }
    
    private void refreshAllPanels() {
        documentPanel.refreshData();
        userPanel.refreshData();
        loanPanel.refreshData();
        reviewPanel.refreshData();
        statisticsPanel.refreshData();
    }
    
    private void showAboutDialog() {
        String message = "LIBRARY MANAGEMENT SYSTEM\n" +
            "Version 1.0.0 - Modern Edition\n\n" +
            "Developed with Java Swing\n" +
            "Demonstrates OOP Principles:\n" +
            "✓ Inheritance (Document → Book)\n" +
            "✓ Polymorphism (Different implementations)\n" +
            "✓ Encapsulation (Private fields, public methods)\n" +
            "✓ Abstraction (Abstract Document class)\n\n" +
            "Features:\n" +
            "• Document Management\n" +
            "• User Management\n" +
            "• Loan Tracking\n" +
            "• Review System\n" +
            "• Statistics & Reports\n" +
            "• Google Books API Integration\n";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "About Library Management System",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Thử thiết lập Nimbus look and feel
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new MainFrame().setVisible(true);
        });
    }
} 
