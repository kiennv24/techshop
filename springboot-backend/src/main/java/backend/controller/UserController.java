package backend.controller;

import backend.dto.request.UpdateProfileRequest; // Đảm bảo đã import đúng file Request của bạn
import backend.dto.response.ApiResponse;
import backend.dto.response.UserResponse;
import backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable("id") Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Lấy thông tin người dùng thành công");
        response.setData(userResponse);
        
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(
            @PathVariable("id") Long userId,
            @RequestBody UpdateProfileRequest updateRequest) {
        
        // Gọi xuống tầng Service để xử lý cập nhật cơ sở dữ liệu
        userService.updateUserProfile(userId, updateRequest);
        
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Cập nhật thông tin cá nhân thành công!");
        response.setData(null);
        
        return ResponseEntity.ok(response);
    }
}