package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * lop LoanTransaction dung de theo doi viec vay muon va tra tai lieu nguoi dung
 * tu do the hien duoc 1 trong nhung quy tac cua OOP: dong goi (Encapsulation)
 */
public class LoanTransaction {
    private String id;
    private String userId;
    private String documentId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private TransactionStatus status;
    private double fineAmount;
    private int renewalCount;
    private int maxRenewals;
    
    /**
     * Liet ke cac trang thai giao dich
     */
    public enum TransactionStatus {
        ACTIVE,      // Hien dang vay
        RETURNED,    // Tra dung han
        OVERDUE,     // Qua han
        RENEWED      // Gia han thoi gian tra no
    }
    
    /**
     * Constructor cho LoanTransaction
     */
    public LoanTransaction(String id, String userId, String documentId, int loanDays) {
        this.id = id;
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanDays);
        this.returnDate = null;
        this.status = TransactionStatus.ACTIVE;
        this.fineAmount = 0.0;
        this.renewalCount = 0;
        this.maxRenewals = 2;
    }
    
    /**
     * Constructor the hien ngay thang cu the
     */
    public LoanTransaction(String id, String userId, String documentId, 
                          LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.status = TransactionStatus.ACTIVE;
        this.fineAmount = 0.0;
        this.renewalCount = 0;
        this.maxRenewals = 2;
    }
    
    /**
     * constructor mac dinh
     */
    public LoanTransaction() {
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14); // Mac dinh 14 ngay
        this.status = TransactionStatus.ACTIVE;
        this.fineAmount = 0.0;
        this.renewalCount = 0;
        this.maxRenewals = 2;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    public int getRenewalCount() {
        return renewalCount;
    }
    
    public void setRenewalCount(int renewalCount) {
        this.renewalCount = renewalCount;
    }
    
    public int getMaxRenewals() {
        return maxRenewals;
    }
    
    public void setMaxRenewals(int maxRenewals) {
        this.maxRenewals = maxRenewals;
    }
    
    /**
     * kiem tra xem viec tra no co bi qua han
     */
    public boolean isOverdue() {
        if (status == TransactionStatus.RETURNED) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * ngay den han tra (se co gia tri am neu da qua han) 
     */
    public long getDaysUntilDue() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    /**
     * Dem so ngay qua han ( = 0 neu nhu khong no)
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    /**
     * Tinh tien phat dua tren so ngay qua han
     */
    public double calculateFine(double dailyFineRate) {
        long daysOverdue = getDaysOverdue();
        return daysOverdue > 0 ? daysOverdue * dailyFineRate : 0.0;
    }
    
    /**
     * Gia han khoang no
     */
    public boolean renew(int additionalDays) {
        if (renewalCount < maxRenewals && status == TransactionStatus.ACTIVE) {
            dueDate = dueDate.plusDays(additionalDays);
            renewalCount++;
            status = TransactionStatus.RENEWED;
            return true;
        }
        return false;
    }
    
    /**
     * Tra lai tai lieu
     */
    public void returnDocument() {
        this.returnDate = LocalDate.now();
        if (isOverdue()) {
            this.status = TransactionStatus.OVERDUE;
        } else {
            this.status = TransactionStatus.RETURNED;
        }
    }
    
    /**
     * Kiem tra xem viec gia han co duoc chap nhan
     */
    public boolean canRenew() {
        return renewalCount < maxRenewals && 
               (status == TransactionStatus.ACTIVE || status == TransactionStatus.RENEWED);
    }
    
    /**
     * Chuyen thoi han tra no thanh ngay
     */
    public long getLoanDuration() {
        LocalDate endDate = returnDate != null ? returnDate : LocalDate.now();
        return ChronoUnit.DAYS.between(borrowDate, endDate);
    }
     
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LoanTransaction that = (LoanTransaction) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("LoanTransaction[id=%s, user=%s, document=%s, status=%s]", 
                           id, userId, documentId, status);
    }
} 