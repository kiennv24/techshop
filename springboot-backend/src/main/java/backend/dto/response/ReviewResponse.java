package backend.dto.response;
import lombok.Data;

@Data //
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private String userName;
    private int rating;
    private String comment;
    private String createdAt;
}