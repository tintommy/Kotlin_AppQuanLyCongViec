package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName

data class HinhAnh(
    @SerializedName("link")
    val link: String,
    @SerializedName("maHinh")
    val maHinh: Int
)