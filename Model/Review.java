package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Lớp Review cho đánh giá và bình luận của người dùng trên tài liệu
 */
public class Review {
    private String id;
    private String userId;
    private String documentId;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;
    private int helpfulVotes;
    private boolean isRecommended;
    
    /**
     * Constructor cho Review
     */
    public Review(String id, String userId, String documentId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.documentId = documentId;
        this.setRating(rating);
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
        this.helpfulVotes = 0;
        this.isRecommended = rating >= 4;
    }
    
    /**
     * Constructor mặc định
     */
    public Review() {
        this.reviewDate = LocalDateTime.now();
        this.helpfulVotes = 0;
        this.isRecommended = false;
        this.comment = "";
    }
    
    //Getter và Setter
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
    
    /**
     * Đặt đánh giá số sao cho review
     * @param rating điểm đánh giá từ 1 đến 5 sao
     * @throws IllegalArgumentException nếu rating không nằm trong khoảng 1-5
     */
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
        this.helpfulVotes = Math.max(0, helpfulVotes);
    }
    
    public boolean isRecommended() {
        return isRecommended;
    }
    
    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }
    
    /**
     * Thêm một lượt bình chọn hữu ích
     */
    public void addHelpfulVote() {
        this.helpfulVotes++;
    }
    
    /**
     * Gỡ một lượt bình chọn hữu ích 
     */
    public void removeHelpfulVote() {
        if (this.helpfulVotes > 0) {
            this.helpfulVotes--;
        }
    }
    
    /**
     * Lấy đánh giá dưới dạng chuỗi ngôi sao
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
     * Kiểm tra xem đánh giá có bình luận không
     */
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
       
    /**
     * Cập nhật nội dung đánh giá
     */
    public void updateReview(int newRating, String newComment) {
        setRating(newRating);
        setComment(newComment);
        this.reviewDate = LocalDateTime.now();
    }
    
    /**
     * Kiểm tra xem đánh giá có tích cực không (4-5 sao)
     */
    public boolean isPositive() {
        return rating >= 4;
    }
    
    /**
     * Kiểm tra xem đánh giá có tiêu cực không (1-2 sao)
     */
    public boolean isNegative() {
        return rating <= 2;
    }
    
    /**
     * Kiểm tra xem đánh giá có trung lập không (3 sao)
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
