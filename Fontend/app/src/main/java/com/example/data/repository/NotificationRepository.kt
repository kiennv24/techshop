package com.example.data.repository

import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.NotificationResponse
import com.example.data.remote.ApiService
import retrofit2.Response

class NotificationRepository(private val apiService: ApiService) {

    suspend fun getUserNotifications(userId: Long): Response<ApiResponse<List<NotificationResponse>>> {
        return apiService.getUserNotifications(userId)
    }

    suspend fun markNotificationAsRead(notificationId: Long): Response<ApiResponse<Void>> {
        return apiService.markNotificationAsRead(notificationId)
    }
}