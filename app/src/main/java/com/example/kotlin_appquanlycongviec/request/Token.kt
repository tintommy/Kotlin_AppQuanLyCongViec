package com.example.kotlin_appquanlycongviec.request


import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("token")
    val token: String
)