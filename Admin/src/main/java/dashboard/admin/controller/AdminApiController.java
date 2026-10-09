package dashboard.admin.controller;

import dashboard.admin.entity.Order;
import dashboard.admin.entity.Product;
import dashboard.admin.entity.User;
import dashboard.admin.repository.OrderRepository;
import dashboard.admin.repository.ProductRepository;
import dashboard.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> m = new HashMap<>();
        long userCount = userRepository.count();
        long orderCount = orderRepository.count();
        long inventoryCount = productRepository.count();
        
        BigDecimal sales = orderRepository.findAll().stream()
                .map(Order::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        m.put("userCount", userCount);
        m.put("orderCount", orderCount);
        m.put("inventoryCount", inventoryCount);
        m.put("salesThisMonth", sales);
        return m;
    }

    @GetMapping("/users")
    public List<User> users() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User user(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> orders() {
        return ordersSummary();
    }

    @GetMapping("/orders-summary")
    public List<Map<String, Object>> ordersSummary() {
        List<Map<String, Object>> out = new java.util.ArrayList<>();
        orderRepository.findAll().forEach(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", o.getId());
            m.put("userName", o.getUser() != null ? o.getUser().getName() : "Khách");
            m.put("status", o.getStatus());
            m.put("totalPrice", o.getTotalPrice());
            m.put("createdAt", o.getCreatedAt() != null ? o.getCreatedAt().toString() : null);
            out.add(m);
        });
        return out;
    }

    @GetMapping("/orders/{id}")
    public Order order(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @GetMapping("/inventory")
    public List<Product> inventory() {
        return productRepository.findAll();
    }

    @GetMapping("/inventory/{id}")
    public Product product(@PathVariable Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @GetMapping("/revenue-months")
    public Map<String, java.math.BigDecimal> revenueMonths() {
        Map<String, java.math.BigDecimal> map = new java.util.LinkedHashMap<>();
        java.time.LocalDate now = java.time.LocalDate.now();
        
        // Khởi tạo mốc thời gian 6 tháng gần đây
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth ym = java.time.YearMonth.from(now.minusMonths(i));
            map.put(ym.toString(), java.math.BigDecimal.ZERO);
        }
        
        // Duyệt đơn hàng và cộng dồn doanh thu
        orderRepository.findAll().forEach(o -> {
            if (o.getCreatedAt() != null) {
                java.time.YearMonth ym = java.time.YearMonth.from(o.getCreatedAt().toLocalDate());
                String key = ym.toString();
                if (map.containsKey(key)) {
                    java.math.BigDecimal cur = map.get(key);
                    if (o.getTotalPrice() != null) {
                        cur = cur.add(o.getTotalPrice());
                    }
                    map.put(key, cur);
                }
            }
        });
        return map;
    }

    @GetMapping("/top-products")
    public Map<String, Object> topProducts() {
        Map<Long, Integer> counts = new HashMap<>();
        orderRepository.findAll().forEach(o -> {
            if (o.getOrderItems() != null) {
                o.getOrderItems().forEach(it -> {
                    if (it.getProduct() != null) {
                        counts.put(it.getProduct().getId(), counts.getOrDefault(it.getProduct().getId(), 0) + (it.getQuantity() == null ? 0 : it.getQuantity()));
                    }
                });
            }
        });
        
        List<String> labels = new java.util.ArrayList<>();
        List<Integer> data = new java.util.ArrayList<>();
        
        counts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(8)
                .forEach(e -> {
                    productRepository.findById(e.getKey()).ifPresent(p -> {
                        labels.add(p.getName());
                        data.add(e.getValue());
                    });
                });
                
        Map<String, Object> out = new HashMap<>();
        out.put("labels", labels);
        out.put("data", data);
        return out;
    }
}