package com.example.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("currentPassword")
    val currentPassword: String? = null,

    @SerializedName("newPassword")
    val newPassword: String? = null,
    @SerializedName("avatar")
    val avatar: String?,

)