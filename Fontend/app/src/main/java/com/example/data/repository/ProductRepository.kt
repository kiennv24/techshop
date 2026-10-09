package com.example.data.repository

import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.CategoryResponse
import com.example.data.dto.response.ProductResponse
import com.example.data.remote.ApiService
import retrofit2.Response

class ProductRepository(private val apiService: ApiService) {
    suspend fun getProductById(productId: Long): Response<ApiResponse<ProductResponse>> {
        return apiService.getProductDetail(productId)
    }
    suspend fun getAllCategories(): Response<ApiResponse<List<CategoryResponse>>> {
        return apiService.getAllCategories()
    }
    suspend fun searchProducts(keyword: String, page: Int, size: Int) =
        apiService.searchProducts(keyword, page, size)
}