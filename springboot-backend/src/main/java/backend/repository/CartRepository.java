package backend.repository;

import backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // ✅ Thêm import này
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser_Id(Long userId);
    Optional<Cart> findByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteByUser_Id(Long userId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
    @Modifying
    void deleteByUser_IdAndProduct_IdIn(Long userId, List<Long> productIds);
}