package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName

data class CongViec(
    @SerializedName("chuKi")
    val chuKi: String,
    @SerializedName("dungSauNgay")
    val dungSauNgay: Any,
    @SerializedName("maCV")
    val maCV: Int,
    @SerializedName("ngayBatDau")
    val ngayBatDau: String,
    @SerializedName("nguoiDung")
    val nguoiDung: NguoiDung,
    @SerializedName("noiDung")
    val noiDung: String,
    @SerializedName("soLan")
    val soLan: Int,
    @SerializedName("tieuDe")
    val tieuDe: String,
    @SerializedName("tinhChat")
    val tinhChat: Int
)