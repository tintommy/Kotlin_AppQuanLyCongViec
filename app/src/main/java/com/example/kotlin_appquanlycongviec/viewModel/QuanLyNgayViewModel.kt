package com.example.kotlin_appquanlycongviec.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.QuanLyApiService
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class QuanLyNgayViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {
    private var _danhSachNgay: MutableStateFlow<Resource<List<NgayDaTaoResponse>>> =
        MutableStateFlow(Resource.Unspecified())
    var danhSachNgay = _danhSachNgay.asStateFlow()

    private var _danhSachCongViecNgay: MutableStateFlow<Resource<List<CongViecNgay>>> =
        MutableStateFlow(Resource.Unspecified())
    var danhSachCongViecNgay = _danhSachCongViecNgay.asStateFlow()

    private var _ngay = MutableLiveData<String>(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Calendar.getInstance().time))
    val ngay : LiveData<String> = _ngay;

    private lateinit var quanLyApiService: QuanLyApiService
    private var _soViecDaXoa: MutableStateFlow<Resource<Long>> =
        MutableStateFlow(Resource.Unspecified(0))
    var soViecDaXoa = _soViecDaXoa.asStateFlow()

    private var _congViecNgayLuu : MutableStateFlow<Resource<CongViecNgay>> =
    MutableStateFlow(Resource.Unspecified())
    var congViecNgayLuu = _congViecNgayLuu.asStateFlow()


    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var userEmail: String


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.userId = sharedPref.getInt("userId", 0)
        this.userEmail = sharedPref.getString("userEmail", "").toString()
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

    fun xoaCongViecNgayTrongNgay(ngay: String) {
        viewModelScope.launch {
            _danhSachNgay.emit(Resource.Loading())
            val response = quanLyApiService.deleteCongViecNgayTrongNgay(ngay)

            if (response.isSuccessful) {
                val count: Long = response.body()!!
                _soViecDaXoa.emit(Resource.Success(count))
                taiDanhNgay()
            } else {
                if (response.code() == 404) {
                    _soViecDaXoa.emit(Resource.Error("404"))
                }
            }
        }
    }

    fun xoaCongViecNgayTrongNgay(idCongViec: Int) {
        Log.e("bug to" , idCongViec.toString())
        viewModelScope.launch {
            _danhSachCongViecNgay.emit(Resource.Loading())
            val response = quanLyApiService.deleteCongViecNgayTheoId(idCongViec)

            if (response.isSuccessful ) {

                taiDanhSachCongViec()
            } else {
                if (response.code() == 404) {

                }
            }
        }
    }

    fun saveCongViec(congViec: CongViecRequest) {
        congViec.maNd = userId
        viewModelScope.launch {
            val response = quanLyApiService.luuCongViec(congViec)
        }
    }


    fun taiDanhSachCongViec() {

        viewModelScope.launch {
            _danhSachCongViecNgay.emit(Resource.Loading())
            val response = quanLyApiService.getCongViecNgayTrongNgay(userId, _ngay.value.toString())

            if (response.isSuccessful) {
                val dsNgay: List<CongViecNgay> = response.body()!!
                _danhSachCongViecNgay.emit(Resource.Success(dsNgay))
            } else {
                if (response.code() == 404) {
                    _danhSachCongViecNgay.emit(Resource.Error("404"))
                }
            }
        }
    }

    fun luuCongViecNgay(congViecNgay: CongViecNgay) {
        viewModelScope.launch {
            _congViecNgayLuu.emit(Resource.Loading())
            val response = quanLyApiService.luuCongViecNgay(congViecNgay)

            if (response.isSuccessful) {
                val cvNgay: CongViecNgay = response.body()!!
                _congViecNgayLuu.emit(Resource.Success(cvNgay))
            } else {
                if (response.code() == 404) {
                    _congViecNgayLuu.emit(Resource.Error("404"))
                }
            }
        }
    }

    fun setNgay(ngay: String) {
        this._ngay.value = ngay
    }


}