package com.example.data.repository

import com.example.data.dto.request.UpdateProfileRequest
import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.UserResponse
import com.example.data.remote.ApiService
import retrofit2.Response // ✅ Đảm bảo có import này để bọc phản hồi mạng chuẩn Retrofit

class ProfileRepository(private val apiService: ApiService) {


    suspend fun getUserProfile(userId: Long): Response<ApiResponse<UserResponse>> {
        return apiService.getUserProfile(userId)
    }

    suspend fun updateProfile(
        userId: Long,
        updateRequest: UpdateProfileRequest
    ): Response<ApiResponse<Void>> {
        return apiService.updateUserProfile(userId, updateRequest)
    }
}