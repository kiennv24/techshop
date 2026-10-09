package backend.service.impl;

import backend.entity.Review;
import backend.repository.ReviewRepository;
import backend.repository.UserRepository; // Có thể giữ hoặc xóa nếu không dùng
import backend.dto.response.ReviewResponse; // Đảm bảo đúng package response của bạn
import backend.exception.ResourceNotFoundException;
import  backend.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewResponse addReview(Long productId, Long userId, Integer rating, String comment) {
       
        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        // Nếu bạn muốn lấy thêm tên User lưu vào bảng:
        // Bạn có thể truyền thêm tham số String userName vào hàm addReview từ Controller gửi xuống,
        // Sau đó chỉ cần gọi: review.setUserName(userName); cực kỳ tiện lợi!

        Review savedReview = reviewRepository.save(review);
        return toReviewResponse(savedReview);
    }

    @Override
    public List<ReviewResponse> getProductReviews(Long productId) {
        // 🟢 SỬA TẠI ĐÂY: Đổi từ findByProduct_Id thành findByProductId cho đúng chuẩn đặt tên JPA
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    // Hàm chuyển đổi Model Entity sang DTO Response để trả về cho Frontend
    private ReviewResponse toReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setUserId(review.getUserId());
        response.setUserName(review.getUserName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null);
        return response;
    }
}