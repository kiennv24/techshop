package com.example.data.dto.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("read")
    val isRead: Boolean,

    @SerializedName("createdAt")
    val createdAt: String
)