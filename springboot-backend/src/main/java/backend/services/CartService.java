package backend.services;
import backend.dto.request.CartRequest;
import backend.dto.response.CartItemResponse;
import java.util.List;

public interface CartService {
    void addToCart(Long userId, CartRequest request);
    List<CartItemResponse> getCart(Long userId);
    void updateCartItem(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
