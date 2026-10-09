package com.example.data.repository

import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.ReviewResponse
import com.example.data.remote.ApiService
import retrofit2.Response

class ReviewRepository(private val apiService: ApiService) {
    suspend fun getProductReviews(productId: Long): Response<ApiResponse<List<ReviewResponse>>> {
        return apiService.getProductReviews(productId)
    }

    suspend fun addReview(request: com.example.data.dto.request.ReviewRequest) = apiService.addReview(request)
}