package com.library.view;

import com.library.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing loan operations
 */
public class LoanPanel extends JPanel implements RefreshablePanel {
    private Library library;
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    
    public LoanPanel(Library library) {
        this.library = library;
        initializePanel();
        refreshData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND_PRIMARY);
        setBorder(UITheme.createTitledBorder("Loan Management"));
        
        // Create top panel for filters
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
        
        JLabel filterLabel = new JLabel("Filter Loans:");
        filterLabel.setFont(UITheme.FONT_TITLE);
        filterLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(filterLabel);
        
        filterCombo = new JComboBox<>(new String[]{"All Loans", "Returned Loans", "Overdue Loans"});
        filterCombo.setFont(UITheme.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(150, UITheme.INPUT_HEIGHT));
        filterCombo.addActionListener(e -> refreshData());
        panel.add(filterCombo);
        
        panel.add(Box.createHorizontalStrut(20));
        
        JButton refreshButton = UITheme.createSecondaryButton("Refresh View");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table
        String[] columnNames = {"Transaction ID", "User", "Document", "Borrow Date", "Due Date", "Return Date", "Fine"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.setRowHeight(25);
        loanTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Transaction ID
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(150); // User
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Document
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Borrow Date
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Due Date
        loanTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Return Date
        loanTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Fine
        
        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = UITheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM, UITheme.PADDING_MEDIUM));
        
        JButton borrowButton = UITheme.createPrimaryButton("+ Borrow Document");
        borrowButton.addActionListener(e -> showBorrowDialog());
        panel.add(borrowButton);
        
        JButton returnButton = UITheme.createSuccessButton("Return Document");
        returnButton.addActionListener(e -> returnDocument());
        panel.add(returnButton);
        
        JButton renewButton = UITheme.createWarningButton("Renew Loan");
        renewButton.addActionListener(e -> renewLoan());
        panel.add(renewButton);
        
        JButton viewButton = UITheme.createSecondaryButton("View Details");
        viewButton.addActionListener(e -> showLoanDetails());
        panel.add(viewButton);
        
        JButton refreshButton = UITheme.createSecondaryButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        return panel;
    }
    
    @Override
    public void refreshData() {
        tableModel.setRowCount(0); // Clear existing data
        
        List<LoanTransaction> transactions;
        String selectedFilter = (String) filterCombo.getSelectedItem();
        
        switch (selectedFilter) {
            case "Returned Loans":
                transactions = library.getAllTransactions().stream()
                    .filter(t -> t.getStatus() == LoanTransaction.TransactionStatus.RETURNED ||
                               t.getStatus() == LoanTransaction.TransactionStatus.OVERDUE)
                    .toList();
                break;
            case "Overdue Loans":
                transactions = library.getOverdueTransactions();
                break;
            default:
                transactions = library.getAllTransactions();
                break;
        }
        
        for (LoanTransaction transaction : transactions) {
            User user = library.getUser(transaction.getUserId());
            Document document = library.getDocument(transaction.getDocumentId());
            
            String userName = user != null ? user.getName() : "Unknown";
            String documentTitle = document != null ? document.getTitle() : "Unknown";
            String returnDateStr = transaction.getReturnDate() != null ? 
                                 transaction.getReturnDate().toString() : "Not returned";
            String fineStr = transaction.getFineAmount() > 0 ? 
                           String.format("$%.2f", transaction.getFineAmount()) : "-";
            
            Object[] row = {
                transaction.getId(),
                userName,
                documentTitle,
                transaction.getBorrowDate(),
                transaction.getDueDate(),
                returnDateStr,
                fineStr
            };
            tableModel.addRow(row);
        }
    }
    
    private void showBorrowDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Borrow Document", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // User selection
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
        
        // Document selection
        List<Document> availableDocuments = library.getAvailableDocuments();
        JComboBox<Document> documentCombo = new JComboBox<>();
        for (Document document : availableDocuments) {
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
        
        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("Select User:"), gbc);
        gbc.gridx = 1;
        dialog.add(userCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Select Document:"), gbc);
        gbc.gridx = 1;
        dialog.add(documentCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton borrowButton = new JButton("Borrow");
        JButton cancelButton = new JButton("Cancel");
        
        borrowButton.addActionListener(e -> {
            User selectedUser = (User) userCombo.getSelectedItem();
            Document selectedDocument = (Document) documentCombo.getSelectedItem();
            
            if (selectedUser == null || selectedDocument == null) {
                JOptionPane.showMessageDialog(dialog, "Please select both user and document.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!selectedUser.canBorrowMore()) {
                JOptionPane.showMessageDialog(dialog, 
                    selectedUser.getName() + " has reached the borrowing limit (" + 
                    selectedUser.getBorrowedCount() + "/" + selectedUser.getMaxBorrowLimit() + ")", 
                    "Borrowing Limit Reached", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String transactionId = library.borrowDocument(selectedUser.getId(), selectedDocument.getId());
            if (transactionId != null) {
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    "Document borrowed successfully!\nTransaction ID: " + transactionId);
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to borrow document!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(borrowButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void returnDocument() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        LoanTransaction transaction = library.getAllTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst().orElse(null);
        
        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (transaction.getStatus() != LoanTransaction.TransactionStatus.ACTIVE &&
            transaction.getStatus() != LoanTransaction.TransactionStatus.RENEWED) {
            JOptionPane.showMessageDialog(this, "This document has already been returned.", "Already Returned", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User user = library.getUser(transaction.getUserId());
        Document document = library.getDocument(transaction.getDocumentId());
        
        String message = String.format("Return document?\n\nUser: %s\nDocument: %s\nBorrow Date: %s\nDue Date: %s",
            user != null ? user.getName() : "Unknown",
            document != null ? document.getTitle() : "Unknown",
            transaction.getBorrowDate(),
            transaction.getDueDate());
        
        if (transaction.isOverdue()) {
            double fine = transaction.calculateFine(library.getDailyFineRate());
            message += String.format("\n\n⚠️ OVERDUE by %d days\nFine: $%.2f", 
                                   transaction.getDaysOverdue(), fine);
        }
        
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm Return", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (library.returnDocument(transactionId)) {
                refreshData();
                String successMessage = "Document returned successfully!";
                if (transaction.isOverdue()) {
                    successMessage += String.format("\nFine applied: $%.2f", transaction.getFineAmount());
                }
                JOptionPane.showMessageDialog(this, successMessage);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return document!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void renewLoan() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to renew.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        LoanTransaction transaction = library.getAllTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst().orElse(null);
        
        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!transaction.canRenew()) {
            String message = "Cannot renew this loan.\nReason: ";
            if (transaction.getRenewalCount() >= transaction.getMaxRenewals()) {
                message += "Maximum renewals reached (" + transaction.getRenewalCount() + "/" + transaction.getMaxRenewals() + ")";
            } else {
                message += "Loan status: " + transaction.getStatus();
            }
            JOptionPane.showMessageDialog(this, message, "Cannot Renew", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User user = library.getUser(transaction.getUserId());
        Document document = library.getDocument(transaction.getDocumentId());
        
        String message = String.format("Renew loan for %d days?\n\nUser: %s\nDocument: %s\nCurrent Due Date: %s\nRenewals: %d/%d",
            library.getDefaultLoanDays(),
            user != null ? user.getName() : "Unknown",
            document != null ? document.getTitle() : "Unknown",
            transaction.getDueDate(),
            transaction.getRenewalCount(),
            transaction.getMaxRenewals());
        
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm Renewal", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (library.renewLoan(transactionId)) {
                refreshData();
                JOptionPane.showMessageDialog(this, 
                    "Loan renewed successfully!\nNew due date: " + transaction.getDueDate());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to renew loan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showLoanDetails() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        LoanTransaction transaction = library.getAllTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst().orElse(null);
        
        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = library.getUser(transaction.getUserId());
        Document document = library.getDocument(transaction.getDocumentId());
        
        StringBuilder details = new StringBuilder();
        details.append("=== LOAN TRANSACTION DETAILS ===\n");
        details.append("Transaction ID: ").append(transaction.getId()).append("\n");
        details.append("Status: ").append(transaction.getStatus()).append("\n\n");
        
        details.append("=== USER INFORMATION ===\n");
        if (user != null) {
            details.append("Name: ").append(user.getName()).append("\n");
            details.append("Email: ").append(user.getEmail()).append("\n");
            details.append("Type: ").append(user.getUserType()).append("\n");
        } else {
            details.append("User information not available\n");
        }
        
        details.append("\n=== DOCUMENT INFORMATION ===\n");
        if (document != null) {
            details.append("Title: ").append(document.getTitle()).append("\n");
            details.append("Author: ").append(document.getAuthor()).append("\n");
            details.append("Type: ").append(document.getDocumentType()).append("\n");
        } else {
            details.append("Document information not available\n");
        }
        
        details.append("\n=== LOAN DETAILS ===\n");
        details.append("Borrow Date: ").append(transaction.getBorrowDate()).append("\n");
        details.append("Due Date: ").append(transaction.getDueDate()).append("\n");
        details.append("Return Date: ").append(
            transaction.getReturnDate() != null ? transaction.getReturnDate() : "Not returned").append("\n");
        details.append("Renewals: ").append(transaction.getRenewalCount()).append("/").append(transaction.getMaxRenewals()).append("\n");
        
        if (transaction.isOverdue()) {
            details.append("⚠️ OVERDUE by ").append(transaction.getDaysOverdue()).append(" days\n");
        } else if (transaction.getReturnDate() == null) {
            details.append("Days until due: ").append(transaction.getDaysUntilDue()).append("\n");
        }
        
        if (transaction.getFineAmount() > 0) {
            details.append("Fine Amount: $").append(String.format("%.2f", transaction.getFineAmount())).append("\n");
        }
        

        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Loan Details", JOptionPane.INFORMATION_MESSAGE);
    }
} 