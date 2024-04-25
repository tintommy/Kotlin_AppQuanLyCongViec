package com.example.kotlin_appquanlycongviec.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.QuanLyApiService
import com.example.kotlin_appquanlycongviec.model.CongViec
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCongViecViewModel @Inject constructor(private val sharedPref: SharedPreferences): ViewModel() {
    private lateinit var quanLyApiService: QuanLyApiService

    private var _luuCongViec: MutableStateFlow<Resource<CongViec>> =
        MutableStateFlow(Resource.Unspecified())
    var luuCongViec = _luuCongViec.asStateFlow()

    private var ngay: String? = null
    private lateinit var token: String
    private var userId: Int=0
    private lateinit var userEmail: String
    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.userId = sharedPref.getInt("userId", 0)
        this.userEmail= sharedPref.getString("userEmail","").toString()
        var retrofit = ApiInstance.getClient(token)
        quanLyApiService = retrofit.create(QuanLyApiService::class.java)
    }

    fun saveCongViec(congViec: CongViecRequest) {
        congViec.maNd = userId
        viewModelScope.launch {
            val response = quanLyApiService.luuCongViec(congViec)
            if (response.isSuccessful) {
                _luuCongViec.emit(Resource.Success(response.body()!!))
            } else {
                _luuCongViec.emit(Resource.Error("404"))
            }

        }
    }
}