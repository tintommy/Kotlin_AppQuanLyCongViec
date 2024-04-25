package com.example.kotlin_appquanlycongviec.api.apiService

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.model.NguoiDung
import com.example.kotlin_appquanlycongviec.request.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NguoiDungApiService {

    @GET("/NguoiDung/{email}")
    suspend fun getUser(@Path("email") email: String): Response<NguoiDung>

    @POST("/NguoiDung/KiemTraMaPin/{email}/{pin}")
    suspend fun kiemTraPin(@Path("email") email: String,@Path("pin") pin: String): Response<Status>

    @POST("/NguoiDung/LuuMaPin/{email}/{pin}")
    suspend fun luuPin(@Path("email") email: String,@Path("pin") pin: String): Response<Status>
    @POST("/NguoiDung/DoiMaPin/{email}/{matKhau}/{pin}")
    suspend fun doiPin(@Path("email") email: String,@Path("matKhau") matKhau: String,@Path("pin") pin: String): Response<Status>
}