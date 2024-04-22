package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName

data class SuKien(
    @SerializedName("gio")
    val gio: String,
    @SerializedName("maSK")
    val maSK: Int,
    @SerializedName("moTa")
    val moTa: String,
    @SerializedName("ngay")
    val ngay: String,
    @SerializedName("ngayNhac")
    val ngayNhac: String,
    @SerializedName("nhacTruoc")
    val nhacTruoc: Int,
    @SerializedName("tenSuKien")
    val tenSuKien: String
)