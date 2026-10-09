package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("avatar")
    val avatar: String?,

    @SerializedName("role")
    val role: String,

    @SerializedName("token")
    val token: String
)