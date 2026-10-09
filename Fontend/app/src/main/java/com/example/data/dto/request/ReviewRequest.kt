package com.example.data.dto.request

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("productId") val productId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String
)