package com.library.view;

import com.library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Khung giao diện chính cho Hệ thống Quản lý Thư viện
 */
public class MainFrame extends JFrame {
    private Library library;
    private JTabbedPane tabbedPane;
    
    // Các panel
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
        setTitle("Hệ thống Quản lý Thư viện - Phiên bản Hiện đại");
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
        
        getContentPane().setBackground(UITheme.BACKGROUND_PRIMARY);
        
        createHeader();
        
        createMenuBar();
        
        createTabbedPane();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        createStatusBar();
        
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 800));
        
    }
    
    private void createHeader() {
        JPanel headerPanel = UITheme.createPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_LARGE, UITheme.PADDING_MEDIUM, UITheme.PADDING_LARGE));
        
        JLabel titleLabel = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
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
        
        documentPanel = new DocumentPanel(library);
        userPanel = new UserPanel(library);
        loanPanel = new LoanPanel(library);
        reviewPanel = new ReviewPanel(library);
        statisticsPanel = new StatisticsPanel(library);
        
        tabbedPane.addTab("  Documents  ", null, documentPanel, "Quản lý sách và tài liệu");
        tabbedPane.addTab("  Users  ", null, userPanel, "Quản lý thành viên thư viện");
        tabbedPane.addTab("  Loans  ", null, loanPanel, "Xử lý mượn và trả sách");
        tabbedPane.addTab("  Reviews  ", null, reviewPanel, "Đánh giá và nhận xét");
        tabbedPane.addTab("  Statistics  ", null, statisticsPanel, "Thống kê và báo cáo");
        
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, UITheme.BACKGROUND_CARD);
            tabbedPane.setForegroundAt(i, UITheme.TEXT_SECONDARY);
        }
        
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof RefreshablePanel) {
                ((RefreshablePanel) selectedComponent).refreshData();
            }
            
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
        
        JLabel statusLabel = new JLabel("Sẵn sàng - Hệ thống Quản lý Thư viện");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void resetLibrary() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn reset thư viện? Tất cả dữ liệu sẽ bị mất.",
            "Reset Library",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            library = Library.getInstance();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Thư viện đã được reset. Cơ sở dữ liệu hiện trống.");
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
        String message = "HỆ THỐNG QUẢN LÝ THƯ VIỆN\n" +
            "Phiên bản 1.0.0 - Bản Hiện đại\n\n" +
            "Phát triển với Java Swing\n" +
            "Minh họa các nguyên lý OOP:\n" +
            "✓ Kế thừa (Document → Book)\n" +
            "✓ Đa hình (Các cách triển khai khác nhau)\n" +
            "✓ Đóng gói (Trường private, phương thức public)\n" +
            "✓ Trừu tượng (Lớp trừu tượng Document)\n\n" +
            "Chức năng:\n" +
            "• Quản lý tài liệu\n" +
            "• Quản lý người dùng\n" +
            "• Quản lý mượn trả\n" +
            "• Hệ thống đánh giá\n" +
            "• Thống kê & báo cáo\n" +
            "• Tích hợp Google Books API\n";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Giới thiệu Hệ thống Quản lý Thư viện",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
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
