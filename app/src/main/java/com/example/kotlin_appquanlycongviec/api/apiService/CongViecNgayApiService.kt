package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CongViecNgayApiService {
    @GET("CongViecNgay/NguoiDung/{maNd}/{ngay}")
    suspend fun layDanhSachCongViecNgay(
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<CongViecNgay>>

    @GET("CongViecNgay/{maCvNgay}")
    suspend fun layCongViecNgay(
        @Path("maCvNgay") maCvNgay: Int
    ): Response<CongViecNgay>

    @GET("CongViecNgay/CapNhatTrangThai/{maCvNgay}/{maNd}/{ngay}")
    suspend fun capNhatTrangThaiCongViecNgay(
        @Path("maCvNgay") maCvNgay: Int,
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<CongViecNgay>>

    @GET("CongViecNgay/XoaCongViecNgay/{maCvNgay}/{maNd}/{ngay}")
    suspend fun xoaCongViecNgay(
        @Path("maCvNgay") maCvNgay: Int,
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<CongViecNgay>>

    @POST("CongViecNgay/LuuCongViecNgay")
    suspend fun luuCongViecNgay(
        @Body congViecNgay: CongViecNgay
    ): Response<CongViecNgay>
}