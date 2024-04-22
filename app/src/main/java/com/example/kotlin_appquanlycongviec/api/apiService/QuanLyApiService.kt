package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuanLyApiService {
    @GET("/CongViec/quanlyngay/{maNd}")
    suspend fun getDanhSachNgay(@Path("maNd") maNd: Int): Response<List<NgayDaTaoResponse>>

    @POST("/CongViec")
    suspend fun luuCongViec(@Body congViec:CongViecRequest)
}