package com.example.data.dto.request

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("shippingAddress")
    val shippingAddress: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("paymentMethod")
    val paymentMethod: String,

    @SerializedName("items")
    val items: List<CartItemRequest>
)