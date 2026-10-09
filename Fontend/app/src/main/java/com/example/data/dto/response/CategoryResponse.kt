package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("icon") val icon: String?,

    @SerializedName("createdAt", alternate = ["created_at"])
    val createdAt: String? = null,

    @SerializedName("updatedAt", alternate = ["updated_at"])
    val updatedAt: String? = null
)