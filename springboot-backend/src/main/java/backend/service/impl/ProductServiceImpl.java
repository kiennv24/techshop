package backend.service.impl;

import backend.dto.request.ProductRequest;
import backend.dto.response.ProductResponse;
import backend.dto.response.CategoryResponse;
import backend.entity.Category;
import backend.entity.Product;
import backend.entity.OrderItem;
import backend.entity.Review;
import backend.exception.ResourceNotFoundException;
import backend.repository.CategoryRepository;
import backend.repository.ProductRepository;
import backend.repository.OrderItemRepository;
import backend.repository.ReviewRepository;
import backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return convertToResponse(product);
    }

    // =========================================================================
    // 🟢 ĐÃ SỬA CHUẨN PHÂN TRANG: Khớp 100% với hàm dòng số 3 trong Repository
    // =========================================================================
    @Override
    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        // Gọi hàm dòng 3 xử lý tìm kiếm không phân biệt hoa thường và phân trang tự động từ DB
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return products.map(this::convertToResponse);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0);
        product.setStock(request.getStock() != null ? request.getStock() : 100);
        product.setImage(request.getImage());
        product.setShopName(request.getShopName());
        product.setEta(request.getEta());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);
        return convertToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0);
        product.setStock(request.getStock() != null ? request.getStock() : 100);
        product.setImage(request.getImage());
        product.setShopName(request.getShopName());
        product.setEta(request.getEta());
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);
        return convertToResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::convertToResponse);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        
        // Gọi hàm Overload setter nhận Double -> Tự chuyển đổi sang BigDecimal lưu vào DTO
        response.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
        response.setDiscount(product.getDiscount());
        response.setStock(product.getStock());
        response.setImage(product.getImage());
        response.setShopName(product.getShopName());
        response.setEta(product.getEta());

        // ĐỒNG BỘ SONG SONG: Gán dữ liệu cho cả Object lồng nhau và các trường phẳng cũ
        if (product.getCategory() != null) {
            // 1. Đổ dữ liệu vào Object lồng nhau cho Android Home nhận diện
            CategoryResponse catResp = new CategoryResponse();
            catResp.setId(product.getCategory().getId());
            catResp.setName(product.getCategory().getName());
            catResp.setDescription(product.getCategory().getDescription());
            catResp.setIcon(product.getCategory().getIcon());
            response.setCategory(catResp); 

            // 2. Giữ nguyên gán cho trường biến phẳng cũ đề phòng lỗi màn hình khác
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        // Tính toán số lượng sản phẩm đã bán thực tế
        int totalSold = 0;
        if (orderItemRepository != null) {
            List<OrderItem> soldItems = orderItemRepository.findByProductId(product.getId());
            if (soldItems != null) {
                totalSold = soldItems.stream()
                        .filter(item -> item.getOrder() != null
                                && "delivered".equalsIgnoreCase(item.getOrder().getStatus()))
                        .mapToInt(OrderItem::getQuantity)
                        .sum();
            }
        }
        // Gán cho cả 2 trường sold cũ và mới
        response.setSold(totalSold);
        response.setSoldQuantity(totalSold);

        // Tính toán rating trung bình thực tế
        float finalRating = 5.0f;
        if (reviewRepository != null) {
            List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
            if (reviews != null && !reviews.isEmpty()) {
                double avgRating = reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(5.0);
                finalRating = (float) (Math.round(avgRating * 10.0) / 10.0);
            }
        }
        // Gọi hàm Overload setter nhận Double -> Tự đổi sang Float gán vào biến rating của DTO
        response.setRating((double) finalRating);
        response.setAverageStars((double) finalRating); // Giữ biến phẳng cũ

        return response;
    }
}