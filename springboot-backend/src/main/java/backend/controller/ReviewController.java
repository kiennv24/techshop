package backend.controller;

import backend.entity.Review;
import backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductReviews(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
            response.put("success", true);
            response.put("message", "Tải đánh giá thành công");
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody Review review) {
        Map<String, Object> response = new HashMap<>();
        try{
            Review savedReview = reviewRepository.save(review);
            response.put("success", true);
            response.put("message", "Gửi bình luận thành công!");
            response.put("data", savedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}