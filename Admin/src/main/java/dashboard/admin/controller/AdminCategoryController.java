package dashboard.admin.controller;

import dashboard.admin.entity.Category;
import dashboard.admin.repository.CategoryRepository;
import dashboard.admin.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;
    // 1. Hiển thị danh sách danh mục
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    // 2. Mở form thêm mới
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    // 3. Xử lý Lưu (Thêm mới hoặc Cập nhật)
    @PostMapping("/save")
    public String save(@ModelAttribute("category") Category category) {
        try {
            // ĐA FIX: Nếu có ID truyền lên từ form -> Đây là hành động SỬA (UPDATE)
            if (category.getId() != null) {
                Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
                if (existingCategory != null) {
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    
                    categoryRepository.save(existingCategory);
                    System.out.println("--> Cập nhật thành công danh mục ID: " + category.getId());
                    return "redirect:/admin/categories";
                }
            }
            
            // Nếu ID bằng null -> Đây là hành động THÊM MỚI (INSERT)
            categoryRepository.save(category);
            System.out.println("--> Thêm mới danh mục thành công!");
            return "redirect:/admin/categories";
            
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý lưu danh mục: " + e.getMessage());
            e.printStackTrace();
            return "admin/category-form";
        }
    }

    // 4. Mở form sửa danh mục cũ
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    // 5. Xử lý xóa danh mục an toàn
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            Category category = categoryRepository.findById(id).orElse(null);
            if (category != null) {
                // ĐA FIX AN TOÀN: Gỡ liên kết tất cả sản phẩm đang thuộc danh mục này trước khi xóa 
                // để tránh lỗi ràng buộc khóa ngoại (Foreign Key Constraint Violation) của MySQL
                if (category.getProducts() != null) {
                    category.getProducts().forEach(product -> {
                        product.setCategory(null);
                        productRepository.save(product);
                    });
                }
                categoryRepository.delete(category);
                System.out.println("--> Xóa thành công danh mục ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Lỗi hệ thống khi xóa danh mục: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/categories";
    }
}