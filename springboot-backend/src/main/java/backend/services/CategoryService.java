package backend.services;
import backend.entity.Category;
import java.util.List;
import backend.dto.response.CategoryResponse;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategoriesDto();
}
