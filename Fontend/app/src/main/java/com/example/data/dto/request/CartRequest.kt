package com.example.data.dto.request

import com.google.gson.annotations.SerializedName

data class CartRequest(
    @SerializedName(" productId")
    val  productId: Long,

    @SerializedName("password")
    val password: Integer

)
