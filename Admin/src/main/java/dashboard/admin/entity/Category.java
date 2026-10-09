package dashboard.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // ĐÃ THÊM: Thư viện hỗ trợ loại trừ toString
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Đảm bảo tên danh mục không bị rỗng ở tầng DB
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Chặn Jackson quét ngược xuôi vô hạn giữa Danh mục và Sản phẩm khi xuất chuỗi JSON
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    @ToString.Exclude
    private List<Product> products;
}