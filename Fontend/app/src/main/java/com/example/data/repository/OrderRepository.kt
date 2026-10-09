package com.example.data.repository

import com.example.data.dto.request.CartItemRequest
import com.example.data.dto.request.OrderRequest
import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.OrderResponse
import com.example.data.remote.ApiService
import retrofit2.Response

class OrderRepository(private val apiService: ApiService) {

    suspend fun placeOrder(
        userId: Long,
        shippingAddress: String,
        phoneNumber: String,
        paymentMethod: String,
        cartItems: List<CartItemRequest>
    ): Response<ApiResponse<OrderResponse>> {

        val orderRequest = OrderRequest(
            shippingAddress = shippingAddress,
            phoneNumber = phoneNumber,
            paymentMethod = paymentMethod,
            items = cartItems
        )
        return apiService.placeOrder(userId, orderRequest)
    }

    suspend fun getMyOrders(userId: Long): Response<ApiResponse<List<OrderResponse>>> {
        return apiService.getMyOrders(userId)
    }
    suspend fun getUserOrders(userId: Long, status: String) =
        apiService.getUserOrders(userId, status)
    suspend fun getOrderById(orderId: Long) = apiService.getOrderById(orderId)
    suspend fun updateOrderStatus(orderId: Long, status: String) = apiService.updateOrderStatus(orderId, status)
}