package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.CongViec
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuanLyApiService {
    @GET("/CongViec/quanlyngay/{maNd}")
    suspend fun getDanhSachNgay(@Path("maNd") maNd: Int): Response<List<NgayDaTaoResponse>>

    @DELETE("/CongViec/quanlyngay/{ngay}")
    suspend fun deleteCongViecNgayTrongNgay(@Path("ngay") maNd: String): Response<Long>

    @DELETE("/CongViec/quanlyngay/id/{id}")
    suspend fun deleteCongViecNgayTheoId(@Path("id") id: Int): Response<Long>

    @GET("/CongViec/quanlyngay/{maNd}/{ngay}")
    suspend fun getCongViecNgayTrongNgay(@Path("maNd") maNd: Int, @Path("ngay") ngay: String): Response<List<CongViecNgay>>

    @POST("/CongViec")
    suspend fun luuCongViec(@Body congViec:CongViecRequest) : Response<CongViec>

    @POST("/CongViec/congViecNgay")
    suspend fun luuCongViecNgay(@Body congViecNgay: CongViecNgay) : Response<CongViecNgay>
}