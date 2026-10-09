package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("image") val image: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("sold") val sold: Int?,
    @SerializedName("category") val category: CategoryResponse?
)