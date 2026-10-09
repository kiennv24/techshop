package backend.service.impl;

import backend.dto.request.CartItemRequest;
import backend.dto.request.OrderRequest;
import backend.dto.response.OrderResponse;
import backend.entity.Order;
import backend.entity.OrderItem;
import backend.entity.Product;
import backend.entity.User;
import backend.exception.ResourceNotFoundException;
import backend.repository.CartRepository;
import backend.repository.OrderItemRepository;
import backend.repository.OrderRepository;
import backend.repository.ProductRepository;
import backend.repository.UserRepository;
import backend.services.OrderService;
import backend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("No products selected for order");
        }
        
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> productIdsToClear = new ArrayList<>();
        
        for (CartItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemReq.getProductId()));
            
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            item.setCreatedAt(LocalDateTime.now());
            
            orderItems.add(item);
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
            
            productIdsToClear.add(product.getId());
        }
        
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        
        order = orderRepository.save(order);
        
        if (!productIdsToClear.isEmpty()) {
            cartRepository.deleteByUser_IdAndProduct_IdIn(userId, productIdsToClear);
        }

        try {
            notificationService.createNotification(
                userId,
                "🎉 Đặt hàng thành công!",
                "Đơn hàng mã số #" + order.getId() + " với tổng trị giá " + String.format("%,.0f", totalPrice) + "đ đã được hệ thống tiếp nhận thành công và đang chờ xử lý."
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return convertToResponse(order);
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToResponse(order);
    }
    
    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUser_Id(userId);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrdersByStatus(Long userId, String status) {
        List<Order> orders = orderRepository.findByUserIdAndStatusIgnoreCaseOrderByCreatedAtDesc(userId, status);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public Page<OrderResponse> getUserOrdersPaginated(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser_Id(userId, pageable);
        return orders.map(this::convertToResponse);
    }
    
    @Override
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        
        // Đảm bảo ép kiểu an toàn từ BigDecimal sang Double nếu DTO yêu cầu
        if (order.getTotalPrice() != null) {
            response.setTotalPrice(order.getTotalPrice().doubleValue());
        }
        
        response.setStatus(order.getStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        
        if (order.getCreatedAt() != null) {
            response.setCreatedAt(order.getCreatedAt().toString());
        }
    
        if (order.getOrderItems() != null) {
            List<OrderResponse.OrderItemResponse> items = order.getOrderItems().stream()
                    .map(item -> {
                        // Ép kiểu giá từ BigDecimal sang Double để khớp DTO Android
                        double itemPrice = (item.getPrice() != null) ? item.getPrice().doubleValue() : 0.0;
                        
                        // Lấy tên ảnh của sản phẩm (Hãy đảm bảo trường này trùng với biến trong Product.java)
                        String imgName = (item.getProduct() != null) ? item.getProduct().getImage() : "";
                        
                        return new OrderResponse.OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                itemPrice,
                                imgName
                        );
                    }).collect(Collectors.toList());
            response.setItems(items);
        }
        
        return response;
    }
}