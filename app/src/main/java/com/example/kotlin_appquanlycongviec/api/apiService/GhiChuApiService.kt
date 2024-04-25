package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.model.GhiChu
import com.example.kotlin_appquanlycongviec.request.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GhiChuApiService {
    @GET("GhiChu/LayDanhSachGhiChu/{maNd}")
    suspend fun layDanhSachGhiChuCuaNguoiDung(@Path("maNd") maNd: Int) : Response<List<GhiChu>>

    @POST("GhiChu/LuuGhiChu/{email}")
    suspend fun luuGhiChu(@Path("email") email: String, @Body gccn:GhiChu) : Response<GhiChu>

    @DELETE("GhiChu/XoaGhiChu/{maGhiChu}")
    suspend fun xoaGhiChu(@Path("maGhiChu") maGhiChu: Int) : Response<Status>
}