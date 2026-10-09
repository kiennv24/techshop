package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class CartItemResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("productId")
    val productId: Long,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("productImage")
    val productImage: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("quantity")
    val quantity: Int
)