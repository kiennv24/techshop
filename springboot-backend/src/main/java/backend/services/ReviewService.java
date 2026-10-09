package backend.services;
import backend.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(Long productId, Long userId, Integer rating, String comment);
    List<ReviewResponse> getProductReviews(Long productId);
    void deleteReview(Long id);
}
