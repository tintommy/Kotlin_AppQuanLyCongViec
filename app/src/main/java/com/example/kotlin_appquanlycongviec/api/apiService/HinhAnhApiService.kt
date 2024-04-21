package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.HinhAnh
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HinhAnhApiService {
    @GET("HinhAnh/LayHinhAnh/{maCvNgay}")
    suspend fun layDanhSachHinhAnh(@Path("maCvNgay") maCvNgay: Int): Response<MutableList<HinhAnh>>

    @POST("HinhAnh/LuuHinhAnh/{maCvNgay}")
    suspend fun luuDanhSachAnh(@Path("maCvNgay") maCvNgay: Int, @Body hinhAnhList: List<String>): Response<Void>

    @GET("/HinhAnh/XoaHinhAnh/{maHinhAnh}")
    suspend fun xoaHinhAnh(@Path("maHinhAnh") maHinhAnh: Int): Response<Void>
}