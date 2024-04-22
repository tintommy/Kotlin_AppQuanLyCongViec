package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.SuKien
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SuKienApiService {
    @GET("SuKien/SuKienHomNay/{ngay}/{maNd}")
    suspend fun layDanhSachSuKienHomNay(
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<SuKien>>

    @GET("SuKien/SuKienSapToi/{ngay}/{maNd}")
    suspend fun layDanhSachSuKienSapToi(
        @Path("maNd") maNd: Int,
        @Path("ngay") ngay: String
    ): Response<List<SuKien>>
}