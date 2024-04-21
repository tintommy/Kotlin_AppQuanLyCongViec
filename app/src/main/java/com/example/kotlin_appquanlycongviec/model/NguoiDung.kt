package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName

data class NguoiDung(
    @SerializedName("email")
    val email: String,
    @SerializedName("gioiTinh")
    val gioiTinh: Boolean,
    @SerializedName("ho")
    val ho: String,
    @SerializedName("maNguoiDung")
    val maNguoiDung: Int,
    @SerializedName("maPin")
    val maPin: String,
    @SerializedName("matKhau")
    val matKhau: String,
    @SerializedName("ngaySinh")
    val ngaySinh: String,
    @SerializedName("ten")
    val ten: String
)