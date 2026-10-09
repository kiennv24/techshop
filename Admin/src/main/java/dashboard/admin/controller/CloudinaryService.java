package dashboard.admin.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Tự động nạp thông số cấu hình từ file application.properties của bạn
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
            
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Hàm đẩy dữ liệu file ảnh trực tiếp lên cloud mây Cloudinary
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Đẩy file lên và gom nhóm vào thư mục tên "products" trên mây Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "products"
        ));
        
        // Trích xuất trả về URL bảo mật tuyệt đối (https://res.cloudinary.com/...)
        return (String) uploadResult.get("secure_url");
    }
}