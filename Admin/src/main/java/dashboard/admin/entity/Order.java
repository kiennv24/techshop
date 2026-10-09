package dashboard.admin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    // ĐÃ TỐI ƯU: Thiết lập giá trị mặc định rõ ràng ở tầng Database để tránh lỗi không đồng bộ dữ liệu
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'pending'")
    private String status = "pending";

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String paymentMethod;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ĐÃ FIX: Thêm @JsonManagedReference để cho phép Jackson quét xuôi dòng từ Order xuống OrderItem 
    // mà không bị lặp vô hạn khi kết hợp với @JsonBackReference ở file OrderItem.java
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();
}