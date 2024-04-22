package com.example.kotlin_appquanlycongviec.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.QuanLyApiService
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuanLyViewModel @Inject constructor(private val sharedPref: SharedPreferences): ViewModel() {
    private var _danhSachNgay: MutableStateFlow<Resource<List<NgayDaTaoResponse>>> = MutableStateFlow(Resource.Unspecified())
    var danhSachNgay = _danhSachNgay.asStateFlow()
    private lateinit var quanLyApiService: QuanLyApiService
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

    fun taiDanhNgay() {

        viewModelScope.launch {
            _danhSachNgay.emit(Resource.Loading())
            val response = quanLyApiService.getDanhSachNgay(userId)

            if (response.isSuccessful) {
                val dsNgay: List<NgayDaTaoResponse> = response.body()!!
                _danhSachNgay.emit(Resource.Success(dsNgay))
            } else {
                if (response.code() == 404) {
                    _danhSachNgay.emit(Resource.Error("404"))
                }
            }
        }
    }

}