# Library-App
BTL-OOP-INT2204 60
## Các thành viên và mức độ đóng góp:
- Trần Trọng Thịnh: 33.3% 
- Hoàng Đức Minh: 33.3%  
- Đặng Hoàng Minh Nghĩa: 33.3%
## Link video giới thiệu app:
https://drive.google.com/file/d/1OhFKs-HRmaXgARzOeAQf5IaO_4U0ugBN/view?usp=sharing
## UML Diagram: 
https://lucid.app/lucidchart/3aa52b9d-8b18-4c73-be65-1495eea71409/edit?viewport_loc=-3486%2C-2684%2C10194%2C4130%2C0_0&invitationId=inv_e23030b8-8f4a-457a-99d5-1692e5a9bdba
## 📚 Hệ Thống Quản Lý Thư Viện - Ứng Dụng Desktop Java
Một ứng dụng desktop được phát triển bằng Java Swing để quản lý hoạt động thư viện một cách hiệu quả và chuyên nghiệp. Dự án này áp dụng các nguyên tắc lập trình hướng đối tượng (OOP) và các mẫu thiết kế phần mềm hiện đại, tạo ra một giải pháp quản lý thư viện đầy đủ tính năng với giao diện thân thiện và kiến trúc mở rộng.
## 🎯 Mục Tiêu Dự Án
Hệ thống được thiết kế để đáp ứng nhu cầu quản lý thư viện từ cơ bản đến nâng cao, bao gồm quản lý tài liệu, người dùng, giao dịch mượn/trả, và hệ thống đánh giá. Ứng dụng tích hợp với Google Books API để tự động hóa việc nhập liệu thông tin sách, đồng thời cung cấp các báo cáo thống kê chi tiết giúp thư viện vận hành hiệu quả hơn.
## 🌟 Tính Năng Chính
## 📖 Quản Lý Tài Liệu
- Thao tác CRUD hoàn chỉnh cho sách và tài liệu
- Chức năng tìm kiếm theo tiêu đề, tác giả hoặc thể loại
- Truy xuất thông tin sách qua ISBN với Google Books API
- Quản lý kho với theo dõi số lượng
- Hỗ trợ nhiều bản copy với trạng thái có sẵn
## 👥 Quản Lý Người Dùng
- Đăng ký người dùng với các loại khác nhau (Sinh viên, Giảng viên, Nhân viên, Khách)
- Giới hạn số lượng sách mượn dựa trên vai trò
- Quản lý hồ sơ người dùng với thông tin liên lạc
## 💳 Hệ Thống Giao Dịch Mượn
- Theo dõi ngày đến hạn với tính toán quá hạn
- Hệ thống tính phạt (mặc định $0.50/ngày)
- Chức năng gia hạn mượn (tối đa 2 lần)
- Lịch sử giao dịch và theo dõi trạng thái
## ⭐ Hệ Thống Đánh Giá & Xếp Hạng
- Hệ thống đánh giá 5 sao cho tài liệu
- Bình luận và đánh giá của người dùng
- Tính toán điểm đánh giá trung bình
## 📊 Thống Kê & Báo Cáo
- Bảng điều khiển thống kê thư viện toàn diện
- Báo cáo lưu thông tài liệu
- Giám sát giao dịch quá hạn
## 🏗️ Tổng Quan Kiến Trúc
### Mẫu Model-View-Controller (MVC)
<pre>
src/main/java/com/library/
├── model/                  # Tầng Logic Nghiệp Vụ
│   ├── Library.java       # Logic nghiệp vụ chính 
│   ├── Document.java      # Entity tài liệu
│   ├── User.java         # Entity người dùng với enum types
│   ├── LoanTransaction.java # Theo dõi giao dịch
│   └── Review.java       # Hệ thống đánh giá và xếp hạng
├── view/                  # Tầng Trình Bày
│   ├── MainFrame.java    # Cửa sổ ứng dụng chính
│   ├── BasePanel.java    # Base trừu tượng cho tất cả panels
│   ├── DocumentPanel.java # UI quản lý tài liệu
│   ├── UserPanel.java    # UI quản lý người dùng
│   ├── LoanPanel.java    # UI xử lý mượn
│   ├── ReviewPanel.java  # UI quản lý đánh giá
│   ├── StatisticsPanel.java # Bảng điều khiển phân tích
│   └── UITheme.java      # Styling UI nhất quán
├── repository/            # Tầng Truy Cập Dữ Liệu
│   ├── *Repository.java  # Interfaces repository
│   └── MySQL*Repository.java # Implementations MySQL
├── service/              # Tầng Dịch Vụ Ngoài
│   └── GoogleBooksService.java # Tích hợp Google Books API
└── database/             # Tầng Cơ Sở Dữ Liệu
    └── DatabaseConnection.java # Quản lý kết nối 
    </pre>

