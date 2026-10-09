package dashboard.admin.controller;
import dashboard.admin.entity.Product;
import dashboard.admin.repository.ProductRepository;
import dashboard.admin.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            String savedImagePath = null;

            if (imageFile != null && !imageFile.isEmpty()) {
                // Nhận về URL tuyệt đối có tiền tố https://res.cloudinary.com/...
                savedImagePath = cloudinaryService.uploadImage(imageFile);
                System.out.println("Upload ảnh lên Cloudinary thành công: " + savedImagePath);
            }

            // 2. Phân tách logic UPDATE thông minh bảo toàn dữ liệu ngầm
            if (product.getId() != null) {
                Product existing = productRepository.findById(product.getId()).orElse(null);
                if (existing != null) {
                    existing.setName(product.getName());
                    existing.setDescription(product.getDescription());
                    existing.setPrice(product.getPrice());
                    existing.setStock(product.getStock());
                    existing.setCategory(product.getCategory());
                    
                    // Nếu người dùng tải ảnh mới lên thì ghi đè URL mới, ngược lại giữ nguyên URL cũ
                    if (savedImagePath != null) {
                        existing.setImage(savedImagePath);
                    }
                    
                    productRepository.save(existing);
                    System.out.println("Cập nhật thành công sản phẩm ID: " + product.getId());
                    return "redirect:/admin/products";
                }
            }

            // 3. Logic xử lý INSERT (Thêm mới sản phẩm hoàn toàn)
            if (savedImagePath != null) {
                product.setImage(savedImagePath);
            } else {
                // Đặt ảnh mặc định nếu người dùng không chọn ảnh khi thêm mới
                product.setImage("/images/no-image.png");
            }
            
            productRepository.save(product);
            System.out.println("Thêm mới sản phẩm thành công!");
            return "redirect:/admin/products";

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu sản phẩm lên Cloudinary: " + e.getMessage());
            e.printStackTrace();
            return "admin/product-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product != null ? product : new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
            System.out.println("Xóa sản phẩm ID: " + id + " thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/products";
    }
}