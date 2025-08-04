package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lớp User đại diện cho thành viên của thư viện.
 * Thể hiện nguyên lý lập trình hướng đối tượng: Đóng gói (Encapsulation).
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private UserType userType;
    private boolean isActive;
    private List<String> borrowedDocumentIds;
    private List<String> favoriteGenres;
    private int maxBorrowLimit;

    /**
     * Enum biểu diễn các loại người dùng với giới hạn mượn tài liệu mặc định.
     */
    public enum UserType {
        STUDENT(5),
        FACULTY(10),
        STAFF(7),
        GUEST(3);

        private final int defaultBorrowLimit;

        UserType(int defaultBorrowLimit) {
            this.defaultBorrowLimit = defaultBorrowLimit;
        }

        /**
         * Lấy giới hạn mượn mặc định của loại người dùng.
         * @return số tài liệu tối đa có thể mượn
         */
        public int getDefaultBorrowLimit() {
            return defaultBorrowLimit;
        }
    }

    /**
     * Hàm tạo người dùng với các thông tin cơ bản.
     * @param id mã người dùng
     * @param name tên người dùng
     * @param email địa chỉ email
     * @param phone số điện thoại
     * @param userType loại người dùng
     */
    public User(String id, String name, String email, String phone, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.registrationDate = LocalDate.now();
        this.isActive = true;
        this.borrowedDocumentIds = new ArrayList<>();
        this.favoriteGenres = new ArrayList<>();
        this.maxBorrowLimit = userType.getDefaultBorrowLimit();
        this.address = "";
    }

    /**
     * Hàm tạo người dùng với đầy đủ thông tin, bao gồm địa chỉ.
     * @param id mã người dùng
     * @param name tên người dùng
     * @param email địa chỉ email
     * @param phone số điện thoại
     * @param address địa chỉ
     * @param userType loại người dùng
     */
    public User(String id, String name, String email, String phone, String address, UserType userType) {
        this(id, name, email, phone, userType);
        this.address = address;
    }

    /**
     * Hàm tạo mặc định cho người dùng loại khách (GUEST).
     */
    public User() {
        this.registrationDate = LocalDate.now();
        this.isActive = true;
        this.borrowedDocumentIds = new ArrayList<>();
        this.favoriteGenres = new ArrayList<>();
        this.userType = UserType.GUEST;
        this.maxBorrowLimit = UserType.GUEST.getDefaultBorrowLimit();
        this.address = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
        this.maxBorrowLimit = userType.getDefaultBorrowLimit();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getBorrowedDocumentIds() {
        return new ArrayList<>(borrowedDocumentIds);
    }

    public void setBorrowedDocumentIds(List<String> borrowedDocumentIds) {
        this.borrowedDocumentIds = new ArrayList<>(borrowedDocumentIds);
    }

    public List<String> getFavoriteGenres() {
        return new ArrayList<>(favoriteGenres);
    }

    public void setFavoriteGenres(List<String> favoriteGenres) {
        this.favoriteGenres = new ArrayList<>(favoriteGenres);
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public void setMaxBorrowLimit(int maxBorrowLimit) {
        this.maxBorrowLimit = maxBorrowLimit;
    }

    /**
     * Mượn một tài liệu nếu chưa mượn quá giới hạn và chưa mượn tài liệu này.
     * @param documentId mã tài liệu
     * @return true nếu mượn thành công, false nếu không thể mượn
     */
    public boolean borrowDocument(String documentId) {
        if (canBorrowMore() && !borrowedDocumentIds.contains(documentId)) {
            borrowedDocumentIds.add(documentId);
            return true;
        }
        return false;
    }

    /**
     * Trả lại tài liệu đã mượn.
     * @param documentId mã tài liệu
     * @return true nếu trả thành công, false nếu không có trong danh sách mượn
     */
    public boolean returnDocument(String documentId) {
        return borrowedDocumentIds.remove(documentId);
    }

    /**
     * Kiểm tra người dùng còn có thể mượn thêm tài liệu hay không.
     * @return true nếu còn quyền mượn, ngược lại false
     */
    public boolean canBorrowMore() {
        return isActive && borrowedDocumentIds.size() < maxBorrowLimit;
    }

    /**
     * Lấy số lượng tài liệu đang được người dùng mượn.
     * @return số tài liệu đã mượn
     */
    public int getBorrowedCount() {
        return borrowedDocumentIds.size();
    }

    /**
     * Thêm một thể loại yêu thích nếu chưa có.
     * @param genre thể loại
     */
    public void addFavoriteGenre(String genre) {
        if (!favoriteGenres.contains(genre)) {
            favoriteGenres.add(genre);
        }
    }

    /**
     * Xóa một thể loại yêu thích.
     * @param genre thể loại
     */
    public void removeFavoriteGenre(String genre) {
        favoriteGenres.remove(genre);
    }

    /**
     * Kiểm tra xem người dùng có đang mượn tài liệu cụ thể hay không.
     * @param documentId mã tài liệu
     * @return true nếu đang mượn, ngược lại false
     */
    public boolean hasBorrowedDocument(String documentId) {
        return borrowedDocumentIds.contains(documentId);
    }

    /**
     * So sánh người dùng dựa trên ID.
     * @param obj đối tượng khác
     * @return true nếu cùng ID, ngược lại false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    /**
     * Tính toán mã băm dựa trên ID.
     * @return mã băm
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Trả về chuỗi đại diện cho thông tin người dùng.
     * @return chuỗi mô tả
     */
    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, type=%s, borrowed=%d/%d]",
                id, name, userType, getBorrowedCount(), maxBorrowLimit);
    }
}
