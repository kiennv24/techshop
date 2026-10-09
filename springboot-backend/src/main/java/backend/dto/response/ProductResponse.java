package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer discount;
    private Float rating;
    private Integer sold;
    private Integer stock;
    private String image;
    private String shopName;
    private String eta;
    private Long categoryId;
    private String categoryName;
    private Integer soldQuantity;
    private Double averageStars;

    private CategoryResponse category;

    public void setPrice(Double priceValue) {
        this.price = priceValue != null ? BigDecimal.valueOf(priceValue) : BigDecimal.ZERO;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setRating(Double ratingValue) {
        this.rating = ratingValue != null ? ratingValue.floatValue() : 0.0f;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}