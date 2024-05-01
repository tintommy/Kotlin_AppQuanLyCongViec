package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.request.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CongViecNgayApiService {
    @GET("CongViecNgay/NguoiDung/{maNd}/{ngay}")
    suspend fun layDanhSachCongViecNgay(
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<CongViecNgay>>


    @GET("CongViecNgay/NguoiDung/DtD/{maNd}/{ngayBatDau}/{ngayKetThuc}")
    suspend fun layDanhSachCongViecTuNgayDenNgay(
        @Path("maNd") maNd: Int,
        @Path("ngayBatDau") ngayBatDau: String,
        @Path("ngayKetThuc") ngayKetThuc: String
    ): Response<List<CongViecNgay>>
    @GET("CongViecNgay/NguoiDung/{maNd}/{thang}/{nam}")
    suspend fun layDanhSachCongViecNgayThangNam(
        @Path("maNd") maNd: Int,
        @Path("thang") thang: Int,
        @Path("nam") nam: Int
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
    @PUT("CongViecNgay/CapNhatCongViecNgay/{maCv}")
    suspend fun capNhatCongViecNgay(
       @Body cvNgay: CongViecNgay,
       @Path("maCv") maCv: Int
    ): Response<Status>
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