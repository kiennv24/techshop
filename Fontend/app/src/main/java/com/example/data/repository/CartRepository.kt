package com.example.data.repository

import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.CartItemResponse
import com.example.data.remote.ApiService
import retrofit2.Response

class CartRepository(private val apiService: ApiService) {

    suspend fun getCart(userId: Long): Response<ApiResponse<List<CartItemResponse>>> {
        return apiService.getCartItems(userId)
    }


    suspend fun addToCart(userId: Long, mapBody: Map<String, Any>): Response<ApiResponse<Void>> {
        return apiService.addToCart(userId, mapBody)
    }

    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int): Response<ApiResponse<Void>> {
        return apiService.updateCartQuantity(userId, productId, quantity)
    }


    suspend fun deleteItem(userId: Long, productId: Long): Response<ApiResponse<Void>> {
        return apiService.deleteCartItem(userId, productId)
    }
}