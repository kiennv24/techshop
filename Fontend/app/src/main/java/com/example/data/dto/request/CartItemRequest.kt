package com.example.data.dto.request

import com.google.gson.annotations.SerializedName

data class CartItemRequest(
    @SerializedName("productId")
    val productId: Long,

    @SerializedName("quantity")
    val quantity: Int
)