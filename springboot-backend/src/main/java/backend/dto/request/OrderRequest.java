package backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList; // Thêm import này
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @JsonProperty("shippingAddress")
    private String shippingAddress = "";

    @JsonProperty("phoneNumber")
    private String phoneNumber = "";

    @JsonProperty("paymentMethod")
    private String paymentMethod = "COD";

    @JsonProperty("items")
    private List<CartItemRequest> items = new ArrayList<>();
}