package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Lop Review (danh gia) cho nguoi dung danh gia va nhan xet
 * tu do the hien duoc 1 trong nhung quy tac cua OOP: dong goi (Encapsulation)
 */
public class Review {
    private String id;
    private String userId;
    private String documentId;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime reviewDate;
    private int helpfulVotes;
    private boolean isRecommended;
    
    /**
     * Constructor 4 Review
     */
    public Review(String id, String userId, String documentId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.documentId = documentId;
        this.setRating(rating); // Su dung setter de xac thuc
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
        this.helpfulVotes = 0;
        this.isRecommended = rating >= 4; // Tu dong danh gia 4-5 sao
    }
    
    /**
     * Constructor mac dinh
     */
    public Review() {
        this.reviewDate = LocalDateTime.now();
        this.helpfulVotes = 0;
        this.isRecommended = false;
        this.comment = "";
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
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.isRecommended = rating >= 4;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public int getHelpfulVotes() {
        return helpfulVotes;
    }
    
    public void setHelpfulVotes(int helpfulVotes) {
        this.helpfulVotes = Math.max(0, helpfulVotes); // Dam bao khong phai gia tri am
    }
    
    public boolean isRecommended() {
        return isRecommended;
    }
    
    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }
    
    /**
     * Cong phieu hai long
     */
    public void addHelpfulVote() {
        this.helpfulVotes++;
    }
    
    /**
     * Bo phieu hai long (neu co the)
     */
    public void removeHelpfulVote() {
        if (this.helpfulVotes > 0) {
            this.helpfulVotes--;
        }
    }
    
    /**
     * Chuyen diem danh gia -> sao
     */
    public String getRatingAsStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
    
    /**
     * Kiem tra xem review co nhan xet khong
     */
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
       
    /**
     * Cap nhat noi dung nhan xet
     */
    public void updateReview(int newRating, String newComment) {
        setRating(newRating);
        setComment(newComment);
        this.reviewDate = LocalDateTime.now(); // Cap nhat ngay thang review
    }
    
    /**
     * Kiem tra xem neu danh gia la tich cuc hay khong
     */
    public boolean isPositive() {
        return rating >= 4;
    }
    
    /**
     * Kiem tra xem neu danh gia la tieu cuc hay khong
     */
    public boolean isNegative() {
        return rating <= 2;
    }
    
    /**
     * Kiem tra xem neu danh gia la trung binh hay khong
     */
    public boolean isNeutral() {
        return rating == 3;
    }
      
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Review review = (Review) obj;
        return Objects.equals(id, review.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Review[id=%s, user=%s, document=%s, rating=%d/5, helpful=%d]", 
                           id, userId, documentId, rating, helpfulVotes);
    }
} 