package backend.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer discount;
    private Integer stock;
    private Long categoryId;
    private String image;
    private String shopName;
    private String eta;
}
