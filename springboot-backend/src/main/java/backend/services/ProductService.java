package backend.services;

import backend.dto.request.ProductRequest;
import backend.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    ProductResponse getProductById(Long id);
    
    // Hàm lấy sản phẩm theo danh mục chuẩn phân trang
    Page<ProductResponse> getProductsByCategoryId(Long categoryId, Pageable pageable);
    

    Page<ProductResponse> searchProducts(String name, Pageable pageable);
    
    ProductResponse createProduct(ProductRequest request);
    
    ProductResponse updateProduct(Long id, ProductRequest request);
    
    void deleteProduct(Long id);
}