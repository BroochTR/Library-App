package com.library.repository;

import com.library.model.LoanTransaction;
import com.library.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai MySQL của LoanTransactionRepository
 * Xử lý tất cả các thao tác cơ sở dữ liệu cho giao dịch mượn sách
 */
public class MySQLLoanTransactionRepository implements LoanTransactionRepository {
    
    @Override
    public boolean save(LoanTransaction transaction) {
        String sql = "INSERT INTO loan_transactions (id, user_id, document_id, borrow_date, due_date, " +
                     "return_date, status, fine_amount, renewal_count, max_renewals) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getUserId());
            stmt.setString(3, transaction.getDocumentId());
            stmt.setDate(4, Date.valueOf(transaction.getBorrowDate()));
            stmt.setDate(5, Date.valueOf(transaction.getDueDate()));
            
            if (transaction.getReturnDate() != null) {
                stmt.setDate(6, Date.valueOf(transaction.getReturnDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setString(7, transaction.getStatus().name());
            stmt.setDouble(8, transaction.getFineAmount());
            stmt.setInt(9, transaction.getRenewalCount());
            stmt.setInt(10, transaction.getMaxRenewals());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving loan transaction: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public LoanTransaction findById(String id) {
        String sql = "SELECT * FROM loan_transactions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding loan transaction by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<LoanTransaction> findAll() {
        String sql = "SELECT * FROM loan_transactions ORDER BY borrow_date DESC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all loan transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public boolean update(LoanTransaction transaction) {
        String sql = "UPDATE loan_transactions SET user_id = ?, document_id = ?, borrow_date = ?, " +
                     "due_date = ?, return_date = ?, status = ?, " +
                     "fine_amount = ?, renewal_count = ?, max_renewals = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getUserId());
            stmt.setString(2, transaction.getDocumentId());
            stmt.setDate(3, Date.valueOf(transaction.getBorrowDate()));
            stmt.setDate(4, Date.valueOf(transaction.getDueDate()));
            
            if (transaction.getReturnDate() != null) {
                stmt.setDate(5, Date.valueOf(transaction.getReturnDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, transaction.getStatus().name());
            stmt.setDouble(7, transaction.getFineAmount());
            stmt.setInt(8, transaction.getRenewalCount());
            stmt.setInt(9, transaction.getMaxRenewals());
            stmt.setString(10, transaction.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating loan transaction: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM loan_transactions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting loan transaction: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<LoanTransaction> findByUserId(String userId) {
        String sql = "SELECT * FROM loan_transactions WHERE user_id = ? ORDER BY borrow_date DESC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding transactions by user ID: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public List<LoanTransaction> findByDocumentId(String documentId) {
        String sql = "SELECT * FROM loan_transactions WHERE document_id = ? ORDER BY borrow_date DESC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding transactions by document ID: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public List<LoanTransaction> findActiveTransactions() {
        String sql = "SELECT * FROM loan_transactions " +
                     "WHERE status IN ('ACTIVE', 'RENEWED', 'OVERDUE') AND return_date IS NULL " +
                     "ORDER BY due_date ASC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding active transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public List<LoanTransaction> findOverdueTransactions() {
        String sql = "SELECT * FROM loan_transactions " +
                     "WHERE due_date < CURDATE() AND return_date IS NULL " +
                     "ORDER BY due_date ASC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding overdue transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public List<LoanTransaction> findActiveTransactionsByUserId(String userId) {
        String sql = "SELECT * FROM loan_transactions " +
                     "WHERE user_id = ? AND status IN ('ACTIVE', 'RENEWED', 'OVERDUE') AND return_date IS NULL " +
                     "ORDER BY due_date ASC";
        List<LoanTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding active transactions by user ID: " + e.getMessage());
        }
        return transactions;
    }
    
    @Override
    public boolean isDocumentBorrowed(String documentId) {
        String sql = "SELECT COUNT(*) FROM loan_transactions " +
                     "WHERE document_id = ? AND status IN ('ACTIVE', 'RENEWED', 'OVERDUE') AND return_date IS NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if document is borrowed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ ResultSet thành đối tượng LoanTransaction
     */
    private LoanTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        LoanTransaction transaction = new LoanTransaction();
        transaction.setId(rs.getString("id"));
        transaction.setUserId(rs.getString("user_id"));
        transaction.setDocumentId(rs.getString("document_id"));
        
        Date borrowDate = rs.getDate("borrow_date");
        if (borrowDate != null) {
            transaction.setBorrowDate(borrowDate.toLocalDate());
        }
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            transaction.setDueDate(dueDate.toLocalDate());
        }
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            transaction.setReturnDate(returnDate.toLocalDate());
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            transaction.setStatus(LoanTransaction.TransactionStatus.valueOf(statusStr));
        }
        
        transaction.setFineAmount(rs.getDouble("fine_amount"));
        transaction.setRenewalCount(rs.getInt("renewal_count"));
        transaction.setMaxRenewals(rs.getInt("max_renewals"));
        
        return transaction;
    }
}
