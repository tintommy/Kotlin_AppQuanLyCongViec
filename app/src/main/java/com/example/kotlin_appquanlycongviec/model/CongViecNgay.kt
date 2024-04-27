package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CongViecNgay(
    @SerializedName("congViec")
    val congViec: CongViec,
    @SerializedName("maCvNgay")
    val maCvNgay: Int,
    @SerializedName("ngayLam")
    val ngayLam: String,
    @SerializedName("trangThai")
    var trangThai: Boolean,
    @SerializedName("phanTramHoanThanh")
    var phanTramHoanThanh: Int
):Serializable