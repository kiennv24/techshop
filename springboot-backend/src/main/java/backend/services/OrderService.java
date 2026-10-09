package backend.services;

import backend.dto.request.OrderRequest; // ✅ Import thêm file OrderRequest của bạn
import backend.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    
    OrderResponse createOrder(Long userId, OrderRequest request);
    
    OrderResponse getOrderById(Long id);
    
    List<OrderResponse> getUserOrders(Long userId);
    
    Page<OrderResponse> getUserOrdersPaginated(Long userId, Pageable pageable);
    
    void updateOrderStatus(Long id, String status);
    List<OrderResponse> getUserOrdersByStatus(Long userId, String status);
    
}