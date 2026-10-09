package backend.controller;

import backend.dto.request.CartRequest;
import backend.dto.response.ApiResponse;
import backend.dto.response.CartItemResponse;
import backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<Void>> addToCart(
            @PathVariable Long userId,
            @RequestBody CartRequest request) {
        cartService.addToCart(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product added to cart"));
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(@PathVariable Long userId) {
        List<CartItemResponse> cartItems = cartService.getCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart retrieved successfully", cartItems));
    }
    
    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse<Void>> updateCartItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart item updated"));
    }
    
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long userId,
            @RequestParam Long  productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product removed from cart"));
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared"));
    }
}
