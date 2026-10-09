package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("status") val status: String?,
    @SerializedName("shippingAddress") val shippingAddress: String?,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("items") val items: List<OrderItemResponse>?
)
data class OrderItemResponse(
    @SerializedName("productId") val productId: Long,
    @SerializedName("productName") val productName: String?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("productImage") val productImage: String?
)