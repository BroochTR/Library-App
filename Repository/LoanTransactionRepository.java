package com.library.repository;

import com.library.model.LoanTransaction;
import java.util.List;

/**
 * Interface repository cho các thao tác CRUD với giao dịch mượn/trả
 * Định nghĩa các phương thức thao tác với cơ sở dữ liệu giao dịch mượn/trả
 */
public interface LoanTransactionRepository {
    
    /**
     * Lưu giao dịch mượn/trả mới vào cơ sở dữ liệu
     */
    boolean save(LoanTransaction transaction);
    
    /**
     * Tìm giao dịch mượn/trả theo ID
     */
    LoanTransaction findById(String id);
    
    /**
     * Lấy danh sách tất cả giao dịch mượn/trả
     */
    List<LoanTransaction> findAll();
    
    /**
     * Cập nhật thông tin giao dịch mượn/trả
     */
    boolean update(LoanTransaction transaction);
    
    /**
     * Xóa giao dịch mượn/trả theo ID
     */
    boolean delete(String id);
    
    /**
     * Lấy tất cả giao dịch của một người dùng cụ thể
     */
    List<LoanTransaction> findByUserId(String userId);
    
    /**
     * Lấy tất cả giao dịch của một tài liệu cụ thể
     */
    List<LoanTransaction> findByDocumentId(String documentId);
    
    /**
     * Lấy tất cả giao dịch đang hoạt động (chưa trả)
     */
    List<LoanTransaction> findActiveTransactions();
    
    /**
     * Lấy tất cả giao dịch quá hạn
     */
    List<LoanTransaction> findOverdueTransactions();
    
    /**
     * Lấy tất cả giao dịch đang hoạt động của một người dùng cụ thể
     */
    List<LoanTransaction> findActiveTransactionsByUserId(String userId);
    
    /**
     * Kiểm tra tài liệu có đang được mượn (có giao dịch hoạt động hay không)
     */
    boolean isDocumentBorrowed(String documentId);
}
