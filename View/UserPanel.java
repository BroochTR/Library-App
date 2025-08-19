package com.library.view;

import com.library.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Bảng quản lý người dùng thư viện — được tái cấu trúc để kế thừa {@link BasePanel}.
 */
public class UserPanel extends BasePanel {
    
    // Mẫu kiểm tra hợp lệ của email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Mẫu kiểm tra số điện thoại (đúng 10 chữ số)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    
    /**
     * Khởi tạo panel quản lý người dùng.
     * @param library đối tượng thư viện sử dụng để lấy và cập nhật dữ liệu người dùng
     */
    public UserPanel(Library library) {
        super(library);
    }
    
    // Ghi đè các phương thức trừu tượng từ BasePanel
    @Override
    protected String getPanelTitle() {
        return "User Management";
    }
    
    @Override
    protected String getEntityName() {
        return "User";
    }
    
    @Override
    protected String getSearchLabel() {
        return "Search Users:";
    }
    
    @Override
    protected String getSearchPlaceholder() {
        return "Enter user name or email...";
    }
    
    @Override
    protected String[] getColumnNames() {
        return new String[]{"ID", "Name", "Email", "Type", "Borrowed", "Registration Date"};
    }
    
    @Override
    protected void setupColumnWidths(JTable table) {
        int[] widths = {80, 150, 200, 80, 80, 120};
        TableHelper.setStandardColumnWidths(table, widths);
    }
    
    @Override
    protected void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.equals(getSearchPlaceholder())) {
            searchText = "";
        }
        
        if (searchText.isEmpty()) {
            refreshData();
            return;
        }
        
        // Tìm kiếm theo tên hoặc email
        List<User> users = library.searchUsersByName(searchText);
        updateUserTable(users);
    }
    
    @Override
    protected void performAdd() {
        showAddUserDialog();
    }
    
    @Override
    protected void performEdit() {
        showEditUserDialog();
    }
    
    @Override
    protected void performDelete() {
        deleteUser();
    }
    
    @Override
    protected void addCustomBottomPanelComponents(JPanel panel) {
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showUserDetails());
        panel.add(viewButton, 1); // Chèn sau nút Add
    }
    
    @Override
    public void refreshData() {
        List<User> users = library.getAllUsers();
        updateUserTable(users);
    }
    
    /**
     * Cập nhật dữ liệu bảng người dùng theo danh sách truyền vào.
     * @param users danh sách người dùng cần hiển thị
     */
    private void updateUserTable(List<User> users) {
        tableModel.setRowCount(0); // Xóa dữ liệu hiện có
        
        for (User user : users) {
            Object[] rowData = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                user.getBorrowedCount() + "/" + user.getMaxBorrowLimit(),
                user.getRegistrationDate()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Hiển thị hộp thoại thêm người dùng mới.
     */
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout());
        
        // Tạo panel biểu mẫu
        JPanel formPanel = UITheme.createCard();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Trường biểu mẫu - sử dụng ô nhập đơn giản không placeholder
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        
        // Áp dụng kiểu hiển thị cơ bản
        nameField.setFont(UITheme.FONT_BODY);
        emailField.setFont(UITheme.FONT_BODY);
        phoneField.setFont(UITheme.FONT_BODY);
        addressField.setFont(UITheme.FONT_BODY);
        
        nameField.setBorder(UITheme.createInputBorder());
        emailField.setBorder(UITheme.createInputBorder());
        phoneField.setBorder(UITheme.createInputBorder());
        addressField.setBorder(UITheme.createInputBorder());
        JComboBox<User.UserType> typeCombo = new JComboBox<>(User.UserType.values());
        
        // Thêm các thành phần vào biểu mẫu
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Panel nút thao tác
        JPanel buttonPanel = new JPanel();
        JButton saveButton = UITheme.createPrimaryButton("Save User");
        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> {
            if (validateUserInput(nameField.getText(), emailField.getText(), phoneField.getText())) {
                User newUser = new User("", nameField.getText(), emailField.getText(), 
                                      phoneField.getText(), addressField.getText(), 
                                      (User.UserType) typeCombo.getSelectedItem());
                
                if (library.addUser(newUser)) {
                    showSuccess("User added successfully!");
                    refreshData();
                    dialog.dispose();
                } else {
                    showError("Failed to add user. Please try again.");
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Hiển thị hộp thoại chỉnh sửa người dùng đã chọn.
     */
    private void showEditUserDialog() {
        String selectedId = TableHelper.getSelectedId(mainTable);
        if (selectedId == null) {
            showError("Please select a user to edit.");
            return;
        }
        
        User user = library.getUser(selectedId);
        if (user == null) {
            showError("User not found.");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new BorderLayout());
        
        // Tạo panel biểu mẫu
        JPanel formPanel = UITheme.createCard();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Trường biểu mẫu được điền sẵn từ dữ liệu hiện tại (không dùng placeholder)
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField phoneField = new JTextField(user.getPhone(), 20);
        JTextField addressField = new JTextField(user.getAddress(), 20);
        
        // Áp dụng kiểu hiển thị cơ bản
        nameField.setFont(UITheme.FONT_BODY);
        emailField.setFont(UITheme.FONT_BODY);
        phoneField.setFont(UITheme.FONT_BODY);
        addressField.setFont(UITheme.FONT_BODY);
        
        nameField.setBorder(UITheme.createInputBorder());
        emailField.setBorder(UITheme.createInputBorder());
        phoneField.setBorder(UITheme.createInputBorder());
        addressField.setBorder(UITheme.createInputBorder());
        
        JComboBox<User.UserType> typeCombo = new JComboBox<>(User.UserType.values());
        typeCombo.setSelectedItem(user.getUserType());
        JCheckBox activeCheckBox = new JCheckBox("Active", user.isActive());
        
        // Thêm các thành phần vào biểu mẫu
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(activeCheckBox, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Panel nút thao tác
        JPanel buttonPanel = new JPanel();
        JButton saveButton = UITheme.createPrimaryButton("Update User");
        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        
        saveButton.addActionListener(e -> {
            if (validateUserInput(nameField.getText(), emailField.getText(), phoneField.getText())) {
                // Cập nhật đối tượng người dùng với giá trị mới
                user.setName(nameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setAddress(addressField.getText().trim());
                user.setUserType((User.UserType) typeCombo.getSelectedItem());
                user.setActive(activeCheckBox.isSelected());
                
                if (library.updateUser(user)) {
                    showSuccess("User updated successfully!");
                    refreshData();
                    dialog.dispose();
                } else {
                    showError("Failed to update user. Please try again.");
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Hiển thị chi tiết người dùng đang chọn.
     */
    private void showUserDetails() {
        String selectedId = TableHelper.getSelectedId(mainTable);
        if (selectedId == null) {
            showError("Please select a user to view details.");
            return;
        }
        
        User user = library.getUser(selectedId);
        if (user == null) {
            showError("User not found.");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("User Details:\n\n");
        details.append("ID: ").append(user.getId()).append("\n");
        details.append("Name: ").append(user.getName()).append("\n");
        details.append("Email: ").append(user.getEmail()).append("\n");
        details.append("Phone: ").append(user.getPhone()).append("\n");
        details.append("Address: ").append(user.getAddress()).append("\n");
        details.append("User Type: ").append(user.getUserType()).append("\n");
        details.append("Registration Date: ").append(user.getRegistrationDate()).append("\n");
        details.append("Status: ").append(user.isActive() ? "Active" : "Inactive").append("\n");
        details.append("Borrowed: ").append(user.getBorrowedCount()).append("/").append(user.getMaxBorrowLimit()).append("\n");
        
        JOptionPane.showMessageDialog(this, details.toString(), "User Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Xóa người dùng đang chọn sau khi xác nhận.
     */
    private void deleteUser() {
        String selectedId = TableHelper.getSelectedId(mainTable);
        if (selectedId == null) {
            showError("Please select a user to delete.");
            return;
        }
        
        User user = library.getUser(selectedId);
        if (user == null) {
            showError("User not found.");
            return;
        }
        
        // Kiểm tra xem người dùng có thể bị xóa không (không có giao dịch mượn đang hoạt động)
        if (!library.canDeleteUser(selectedId)) {
            showError("Cannot delete user with active loan transactions. Please ensure all documents are returned first.");
            return;
        }
        
        String confirmMessage = "Are you sure you want to delete user: " + user.getName() + "?\n\n" +
                               "WARNING: This will permanently delete:\n" +
                               "- All loan transaction history\n" +
                               "- All reviews by this user\n" +
                               "- User account and personal information\n\n" +
                               "This action cannot be undone!";
        
        if (showConfirmation(confirmMessage)) {
            if (library.removeUser(selectedId)) {
                showSuccess("User deleted successfully!");
                refreshData();
            } else {
                showError("Failed to delete user. Please try again.");
            }
        }
    }
    
    /**
     * Kiểm tra tính hợp lệ dữ liệu nhập của người dùng.
     * @param name tên
     * @param email email
     * @param phone số điện thoại
     * @return true nếu hợp lệ, ngược lại false
     */
    private boolean validateUserInput(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            showError("Name is required.");
            return false;
        }
        
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return false;
        }
        
        if (!isValidPhone(phone)) {
            showError("Please enter a valid 10-digit phone number.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Kiểm tra định dạng email hợp lệ.
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Kiểm tra định dạng số điện thoại hợp lệ (10 chữ số).
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
}