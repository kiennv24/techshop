package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("productId") val productId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?,
    @SerializedName("createdAt") val createdAt: String?
)