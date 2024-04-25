package com.example.kotlin_appquanlycongviec.request

import com.example.kotlin_appquanlycongviec.model.NguoiDung
import com.google.gson.annotations.SerializedName

data class CongViecRequest(
    var maCV: Int,
    var tieuDe: String,
    var noiDung: String,
    var ngayBatDau: String,
    var tinhChat: Int,
    var chuKi: String,
    var dungSauNgay: String,
    var maNd: Int
) {
    constructor() : this(0, "", "", "", 0,"", "", 0)

}

//@SerializedName("maCV")
//maCV: Int,
//@SerializedName("tieuDe")
//var tieuDe: String,
//@SerializedName("noiDung")
//var noiDung: String,
//@SerializedName("ngayBatDau")
//var ngayBatDau: String,
//@SerializedName("tinhChat")
//var tinhChat: String,
//@SerializedName("chuKi")
//var chuKi: String,
//@SerializedName("dungSauNgay")
//var dungSauNgay: String,
//@SerializedName("maCV")
//var maNd: Int