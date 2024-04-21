package com.example.kotlin_appquanlycongviec.api.apiService

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.model.NguoiDung
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NguoiDungApiService {

    @GET("/NguoiDung/{email}")
    suspend fun getUser(@Path("email") email: String): Response<NguoiDung>

}