package backend.services;

import backend.dto.request.UpdateProfileRequest;
import backend.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
    void updateUserProfile(Long userId, UpdateProfileRequest updateRequest);
}