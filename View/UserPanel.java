package com.library.view;

import com.library.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel for managing library users
 */
public class UserPanel extends JPanel implements RefreshablePanel {
    private Library library;
    private JTable userTable;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone validation pattern (exactly 10 digits)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public UserPanel(Library library) {
        this.library = library;
        initializePanel();
        refreshData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder("User Management"));
        
        // Create top panel for search
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create center panel with table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom panel with buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = UITheme.createCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Search components
        JLabel searchLabel = new JLabel("Search Users:");
        searchLabel.setFont(UITheme.FONT_TITLE);
        searchLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(searchLabel);
        
        searchField = UITheme.createTextField("Enter user name or email...");
        searchField.setPreferredSize(new Dimension(250, UITheme.INPUT_HEIGHT));
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField);
        
        JButton searchButton = UITheme.createPrimaryButton("Search Users");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton);
        
        JButton clearButton = UITheme.createSecondaryButton("Clear Search");
        clearButton.addActionListener(e -> {
            searchField.setText("Enter user name or email...");
            searchField.setForeground(UITheme.TEXT_MUTED);
            refreshData();
        });
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columnNames = {"ID", "Name", "Email", "Type", "Borrowed", "Registration Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Type
        userTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Borrowed
        userTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Registration Date
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton addButton = UITheme.createPrimaryButton("+ Add User");
        addButton.addActionListener(e -> showAddUserDialog());
        panel.add(addButton);
        
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showUserDetails());
        panel.add(viewButton);
        
        JButton editButton = UITheme.createWarningButton("Edit User");
        editButton.addActionListener(e -> showEditUserDialog());
        panel.add(editButton);
        
        JButton deleteButton = UITheme.createDangerButton("Delete User");
        deleteButton.addActionListener(e -> deleteUser());
        panel.add(deleteButton);
        
        JButton refreshButton = UITheme.createSuccessButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format (exactly 10 digits)
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    @Override
    public void refreshData() {
        tableModel.setRowCount(0); // Clear existing data
        
        List<User> users = library.getAllUsers();
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                user.getBorrowedCount() + "/" + user.getMaxBorrowLimit(),
                user.getRegistrationDate()
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
        List<User> results = library.searchUsersByName(searchText);
        
        for (User user : results) {
            Object[] row = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                user.getBorrowedCount() + "/" + user.getMaxBorrowLimit(),
                user.getRegistrationDate()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create input fields
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField addressField = new JTextField(20);
        JComboBox<User.UserType> typeCombo = new JComboBox<>(User.UserType.values());
        
        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            // Validate required fields
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Email are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Email format is invalid!\nPlease enter a valid email address (example: user@example.com)", 
                    "Invalid Email Format", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone format (must be exactly 10 digits)
            if (!phone.isEmpty() && !isValidPhone(phone)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Phone number is invalid!\nPlease enter exactly 10 digits (example: 0123456789)", 
                    "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User user = new User("", name, email,
                               phone, addressField.getText().trim(),
                               (User.UserType) typeCombo.getSelectedItem());
            
            if (library.addUser(user)) {
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showUserDetails() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = library.getUser(userId);
        
        if (user != null) {
            StringBuilder details = new StringBuilder();
            details.append("=== USER DETAILS ===\n");
            details.append("ID: ").append(user.getId()).append("\n");
            details.append("Name: ").append(user.getName()).append("\n");
            details.append("Email: ").append(user.getEmail()).append("\n");
            details.append("Phone: ").append(user.getPhone()).append("\n");
            details.append("Address: ").append(user.getAddress()).append("\n");
            details.append("User Type: ").append(user.getUserType()).append("\n");
            details.append("Registration Date: ").append(user.getRegistrationDate()).append("\n");
            details.append("Borrowed Documents: ").append(user.getBorrowedCount()).append("/").append(user.getMaxBorrowLimit()).append("\n");
            details.append("Favorite Genres: ").append(user.getFavoriteGenres()).append("\n");
            
            // Show borrowed documents
            List<String> borrowedIds = user.getBorrowedDocumentIds();
            if (!borrowedIds.isEmpty()) {
                details.append("\n=== BORROWED DOCUMENTS ===\n");
                for (String docId : borrowedIds) {
                    Document doc = library.getDocument(docId);
                    if (doc != null) {
                        details.append("- ").append(doc.getTitle()).append(" by ").append(doc.getAuthor()).append("\n");
                    }
                }
            }
            

            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "User Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = library.getUser(userId);
        
        if (user == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create input fields with current values
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField phoneField = new JTextField(user.getPhone(), 15);
        JTextField addressField = new JTextField(user.getAddress(), 20);
        JComboBox<User.UserType> typeCombo = new JComboBox<>(User.UserType.values());
        typeCombo.setSelectedItem(user.getUserType());
        
        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            // Validate required fields
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Email are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Email format is invalid!\nPlease enter a valid email address (example: user@example.com)", 
                    "Invalid Email Format", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone format (must be exactly 10 digits)
            if (!phone.isEmpty() && !isValidPhone(phone)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Phone number is invalid!\nPlease enter exactly 10 digits (example: 0123456789)", 
                    "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(addressField.getText().trim());
            user.setUserType((User.UserType) typeCombo.getSelectedItem());
            
            if (library.updateUser(user)) {
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete user \"" + name + "\"?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            if (library.removeUser(userId)) {
                refreshData();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot delete user. They may have borrowed documents.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 