package com.example.kotlin_appquanlycongviec.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GhiChu(
    @SerializedName("maGCCN")
    val maGCCN: Int,
    @SerializedName("ngayChinhSua")
    val ngayChinhSua: String,
    @SerializedName("noiDung")
    val noiDung: String,
    @SerializedName("tieuDe")
    val tieuDe: String
):Serializable