package dashboard.admin.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ĐÃ FIX: Thêm @JsonBackReference để ngăn chặn Jackson quét ngược từ dòng OrderItem lên Order,
    // xử lý triệt để lỗi sập bộ nhớ (StackOverflowError) khi truy cập API /admin/api/orders
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal price;
}