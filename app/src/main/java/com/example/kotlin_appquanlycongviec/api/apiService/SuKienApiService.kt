package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.request.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("SuKien/TatCaSuKien/{maNd}")
    suspend fun layTatCaSuKien(
        @Path("maNd") maNd: Int
    ): Response<List<SuKien>>

    @POST("SuKien/ThemSuKien")
    suspend fun themSuKien(@Body suKien: SuKien, @Query("maNd") maNd: Int): Response<SuKien>

    @PUT("SuKien/SuaSuKien")
    suspend fun suaSuKien(@Body suKien: SuKien, @Query("maNd") maNd: Int): Response<Status>

    @DELETE("SuKien/XoaSuKien")
    suspend fun xoaSuKien(@Query("maSuKien") maSuKien: Int): Response<Status>
    @GET("SuKien/SuKienTuNgayDenNgay/{ngayBD}/{ngayKT}/{maNd}")
    suspend fun laySuKienTuNgayDenNgay(
        @Path("maNd") maNd: Int,
        @Path("ngayBD") ngayBD: String,
        @Path("ngayKT") ngayKT: String
    ): Response<List<SuKien>>
}