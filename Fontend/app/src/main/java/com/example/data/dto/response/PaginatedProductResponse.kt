package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class PaginatedProductResponse(
    @SerializedName("content")
    val content: List<ProductResponse>,

    @SerializedName("totalElements", alternate = ["total_elements"])
    val totalElements: Int,

    @SerializedName("totalPages", alternate = ["total_pages"])
    val totalPages: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("number")
    val number: Int
)