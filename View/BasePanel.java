package com.library.view;

import com.library.model.Library;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Lớp cơ sở trừu tượng cho tất cả các panel quản lý.
 * Giúp giảm lặp mã và cung cấp các chức năng chung.
 */
public abstract class BasePanel extends JPanel implements RefreshablePanel {
    protected Library library;
    protected JTable mainTable;
    protected DefaultTableModel tableModel;
    protected JTextField searchField;
    
    /**
     * Khởi tạo {@link BasePanel} với tham chiếu thư viện.
     * @param library đối tượng thư viện dùng để tải và thao tác dữ liệu
     */
    public BasePanel(Library library) {
        this.library = library;
        initializePanel();
        refreshData();
    }
    
    /**
     * Khởi tạo panel với bố cục chung.
     */
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder(getPanelTitle()));
        
        // Tạo panel trên cùng cho tìm kiếm và bộ lọc
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
     * Tạo panel tìm kiếm ở phía trên — có thể override để tùy biến tìm kiếm.
     * @return panel cho khu vực tìm kiếm
     */
    protected JPanel createTopPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Thành phần tìm kiếm
        JLabel searchLabel = new JLabel(getSearchLabel());
        searchLabel.setFont(UITheme.FONT_TITLE);
        searchLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(searchLabel);
        
        searchField = UITheme.createTextField(getSearchPlaceholder());
        searchField.setPreferredSize(new Dimension(250, UITheme.INPUT_HEIGHT));
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);
        
        JButton searchButton = UITheme.createPrimaryButton("Search");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton);
        
        JButton clearButton = UITheme.createSecondaryButton("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText(getSearchPlaceholder());
            searchField.setForeground(UITheme.TEXT_MUTED);
            refreshData();
        });
        panel.add(clearButton);
        
        // Thêm các thành phần tùy chỉnh (có thể override)
        addCustomTopPanelComponents(panel);
        
        return panel;
    }
    
    /**
     * Tạo panel trung tâm chứa bảng dữ liệu.
     * @return panel trung tâm
     */
    protected JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tạo bảng
        String[] columnNames = getColumnNames();
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bảng chỉ đọc, không cho phép chỉnh sửa ô
            }
        };
        
        mainTable = new JTable(tableModel);
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainTable.setRowHeight(25);
        mainTable.getTableHeader().setReorderingAllowed(false);
        
        // Thiết lập độ rộng cột tùy chỉnh
        setupColumnWidths(mainTable);
        
        JScrollPane scrollPane = new JScrollPane(mainTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tạo panel dưới cùng với các nút CRUD.
     * @return panel dưới cùng
     */
    protected JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(
            UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, 
            UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton addButton = UITheme.createPrimaryButton("Add " + getEntityName());
        addButton.addActionListener(e -> performAdd());
        panel.add(addButton);
        
        JButton editButton = UITheme.createSecondaryButton("Edit " + getEntityName());
        editButton.addActionListener(e -> performEdit());
        panel.add(editButton);
        
        JButton deleteButton = UITheme.createDangerButton("Delete " + getEntityName());
        deleteButton.addActionListener(e -> performDelete());
        panel.add(deleteButton);
        
        JButton refreshButton = UITheme.createSuccessButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        // Thêm các nút tùy chỉnh (có thể override)
        addCustomBottomPanelComponents(panel);
        
        return panel;
    }
    
    /**
     * Lấy đối tượng được chọn từ bảng.
     * @return giá trị ID ở cột đầu tiên của hàng được chọn; trả về null nếu chưa chọn
     */
    protected Object getSelectedItem() {
        int selectedRow = mainTable.getSelectedRow();
        if (selectedRow >= 0) {
            return tableModel.getValueAt(selectedRow, 0); // Trả về ID (cột đầu tiên)
        }
        return null;
    }
    
    /**
     * Hiển thị thông báo lỗi.
     * @param message nội dung thông báo
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Hiển thị thông báo thành công.
     * @param message nội dung thông báo
     */
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Hiển thị hộp thoại xác nhận.
     * @param message nội dung câu hỏi xác nhận
     * @return true nếu người dùng chọn Yes; ngược lại là false
     */
    protected boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirmation", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Tiêu đề cho khung panel (hiển thị trên viền tiêu đề).
     * @return tiêu đề panel
     */
    protected abstract String getPanelTitle();
    /**
     * Tên thực thể đang quản lý (dùng trong nhãn nút, v.v.).
     * @return tên thực thể, ví dụ: "User", "Document"
     */
    protected abstract String getEntityName();
    /**
     * Nhãn hiển thị trước ô tìm kiếm.
     * @return chuỗi nhãn tìm kiếm
     */
    protected abstract String getSearchLabel();
    /**
     * Gợi ý hiển thị trong ô tìm kiếm khi trống.
     * @return chuỗi gợi ý
     */
    protected abstract String getSearchPlaceholder();
    /**
     * Danh sách tên cột cho bảng dữ liệu chính.
     * @return mảng tên cột
     */
    protected abstract String[] getColumnNames();
    /**
     * Thiết lập độ rộng cột cho bảng.
     * @param table bảng cần thiết lập
     */
    protected abstract void setupColumnWidths(JTable table);
    /**
     * Thực hiện thao tác tìm kiếm theo nội dung nhập.
     */
    protected abstract void performSearch();
    /**
     * Thực hiện thao tác thêm mới.
     */
    protected abstract void performAdd();
    /**
     * Thực hiện thao tác chỉnh sửa mục đã chọn.
     */
    protected abstract void performEdit();
    /**
     * Thực hiện thao tác xóa mục đã chọn.
     */
    protected abstract void performDelete();
    
    // Các phương thức tùy chọn mà lớp con có thể override
    protected void addCustomTopPanelComponents(JPanel panel) {
        // Mặc định: không làm gì
    }
    
    protected void addCustomBottomPanelComponents(JPanel panel) {
        // Mặc định: không làm gì
    }
}