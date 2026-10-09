package backend.service.impl;

import backend.dto.response.CategoryResponse;
import backend.entity.Category;
import backend.exception.ResourceNotFoundException;
import backend.repository.CategoryRepository;
import backend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> {
            category.setProducts(null); // Cắt đứt vòng lặp JSON lồng nhau
            return category;
        }).collect(Collectors.toList());
    }

   @Override
public List<CategoryResponse> getAllCategoriesDto() {
    // Sắp xếp tăng dần theo ID (1, 2, 3...) trước khi trả về cho Android
    List<Category> categories = categoryRepository.findAllByOrderByIdAsc(); 
    
    return categories.stream().map(cat -> {
        CategoryResponse response = new CategoryResponse();
        response.setId(cat.getId());
        response.setName(cat.getName());
        response.setDescription(cat.getDescription());
        response.setIcon(cat.getIcon());
        return response;
    }).collect(Collectors.toList());
}

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        category.setProducts(null);
        return category;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // =========================================================================
    // 🟢 ĐÃ FIX: Bổ sung cập nhật trường Icon (URL) khi thực hiện sửa danh mục
    // =========================================================================
    @Override
    public Category updateCategory(Long id, Category categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setIcon(categoryRequest.getIcon()); // Lưu cập nhật link ảnh icon mới
        
        Category updatedCategory = categoryRepository.save(category);
        updatedCategory.setProducts(null);
        return updatedCategory;
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }
}