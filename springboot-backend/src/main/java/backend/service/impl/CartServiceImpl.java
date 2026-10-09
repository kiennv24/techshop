package backend.service.impl;
import backend.dto.request.CartRequest;
import backend.dto.response.CartItemResponse;
import backend.entity.Cart;
import backend.entity.Product;
import backend.entity.User;
import backend.exception.ResourceNotFoundException;
import backend.repository.CartRepository;
import backend.repository.ProductRepository;
import backend.repository.UserRepository;
import backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void addToCart(@NonNull Long userId, CartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Cart existingCart = cartRepository.findByUser_IdAndProduct_Id(userId, request.getProductId())
                .orElse(null);
        
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + request.getQuantity());
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(request.getQuantity());
            existingCart = cart;
        }
        cartRepository.save(existingCart);
    }
    
    @Override
    public List<CartItemResponse> getCart(Long userId) {
        List<Cart> carts = cartRepository.findByUser_Id(userId);
        return carts.stream().map(cart -> {
            Product product = cart.getProduct();
            return new CartItemResponse(
                    cart.getId(),
                    product.getId(),
                    product.getName(),
                    product.getImage(),
                    product.getPrice(),
                    cart.getQuantity()
            );
        }).collect(Collectors.toList());
    }
    
    @Override
    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
    
    @Override
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartRepository.delete(cart);
    }
    
    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteByUser_Id(userId);
    }
}
