package com.example.kotlin_appquanlycongviec.viewModel

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.CongViecNgayApiService
import com.example.kotlin_appquanlycongviec.api.apiService.LoginApiService
import com.example.kotlin_appquanlycongviec.model.CongViec

import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.request.Status
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class CongViecNgayViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {
    private var _danhSachCongViecNgay: MutableSharedFlow<Resource<List<CongViecNgay>>> =
        MutableStateFlow(Resource.Unspecified())
    var danhSachCongViecNgay = _danhSachCongViecNgay.asSharedFlow()

    private var _capNhatCongViecNgay: MutableStateFlow<Resource<Status>> =
        MutableStateFlow(Resource.Unspecified())
    var capNhatCongViecNgay = _capNhatCongViecNgay.asStateFlow()


    private var _thongTinCongViec = MutableLiveData<CongViec>()
    var thongTinCongViec:  LiveData<CongViec> = _thongTinCongViec



    private lateinit var congViecservice: CongViecNgayApiService
    var soViecCanLam = MutableLiveData("")
    var phanTramHoanThanh = MutableLiveData("")
    private val congViecNgayList: MutableList<CongViecNgay> = ArrayList<CongViecNgay>()
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
        congViecservice = retrofit.create(CongViecNgayApiService::class.java)


    }
    fun taiDanhSachCongViecNgay( ngay: String?) {
        this.ngay = ngay

        viewModelScope.launch {
            _danhSachCongViecNgay.emit(Resource.Loading())
            val response = congViecservice.layDanhSachCongViecNgay(userId, ngay!!)

            if (response.isSuccessful) {
                val dsCv: List<CongViecNgay> = response.body()!!
                _danhSachCongViecNgay.emit(Resource.Success(dsCv))
                capNhatSoViecVaPhanTram(dsCv, 200)
                congViecNgayList.clear()
                congViecNgayList.addAll(dsCv)
            } else {
                if (response.code() == 404) {
                    _danhSachCongViecNgay.emit(Resource.Error("404"))
                    soViecCanLam.postValue("")
                    phanTramHoanThanh.postValue("")
                }
            }
        }
    }

    fun taiDanhSachCongViecTuNgayDenNgay( ngayBatDau:String, ngayKetThuc:String) {

        viewModelScope.launch {
            _danhSachCongViecNgay.emit(Resource.Loading())
            val response = congViecservice.layDanhSachCongViecTuNgayDenNgay(userId, ngayBatDau, ngayKetThuc)

            if (response.isSuccessful) {
                val dsCv: List<CongViecNgay> = response.body()!!
                _danhSachCongViecNgay.emit(Resource.Success(dsCv))

            } else {
                if (response.code() == 404) {
                    _danhSachCongViecNgay.emit(Resource.Error("404"))

                }
            }
        }
    }
    fun taiDanhSachCongViecNgayTheoThangNam( thang:Int, nam:Int) {

        viewModelScope.launch {
            _danhSachCongViecNgay.emit(Resource.Loading())
            val response = congViecservice.layDanhSachCongViecNgayThangNam(userId, thang, nam)

            if (response.isSuccessful) {
                val dsCv: List<CongViecNgay> = response.body()!!
                _danhSachCongViecNgay.emit(Resource.Success(dsCv))

            } else {
                if (response.code() == 404) {
                    _danhSachCongViecNgay.emit(Resource.Error("404"))

                }
            }
        }
    }

    fun capNhatTrangThaiCongViecNgay(maCvNgay: Int, ngay: String?) {
        viewModelScope.launch {
            val response = congViecservice.capNhatTrangThaiCongViecNgay(maCvNgay, userId, ngay!!)
            if (response.isSuccessful) {
                val cvnList: List<CongViecNgay> = response.body()!!
                capNhatSoViecVaPhanTram(cvnList, 200)
            }
        }
    }

    fun capNhatCongViecNgay(congViecNgay: CongViecNgay,ngay:String) {
        viewModelScope.launch {
            val response = congViecservice.capNhatCongViecNgay(congViecNgay, congViecNgay.congViec.maCV)
            if (response.isSuccessful) {
                    _capNhatCongViecNgay.emit(Resource.Success(response.body()!!))
                capNhatSoViecVaPhanTram2(ngay)

            }
            else{
                _capNhatCongViecNgay.emit(Resource.Error("Lỗi"))
            }
        }
    }

    fun xoaCongViecNgay(maCvNgay: Int, ngay: String?) {
        viewModelScope.launch {
            val response = congViecservice.xoaCongViecNgay(maCvNgay, userId, ngay!!)
            if (response.isSuccessful) {
                val cvnList: List<CongViecNgay> = response.body()!!
                capNhatSoViecVaPhanTram(cvnList, 200)
            } else {
                if (response.code() == 404) {
                    val cvnList: List<CongViecNgay> = response.body()!!
                    _danhSachCongViecNgay.emit(Resource.Error("404"))
                    capNhatSoViecVaPhanTram(cvnList, 404)
                }
            }

        }
    }

    fun luuCongViecNgay(congViecNgay: CongViecNgay?) {
        viewModelScope.launch {
            val response = congViecservice.luuCongViecNgay(congViecNgay!!)
        }


    }

    fun capNhatSoViecVaPhanTram(cvnList: List<CongViecNgay>, code: Int) {
        viewModelScope.launch {
            var hoanThanh = 0
            var chuaHoanThanh = 0
            if (code == 200) {
                if (cvnList.size > 0) {
                    for (i in cvnList.indices) {
                        if (cvnList[i].trangThai) {
                            hoanThanh += 1
                        } else chuaHoanThanh += 1
                    }
                    if (hoanThanh == cvnList.size) {
                        phanTramHoanThanh.postValue("Hoàn thành: 100%")
                    } else if (chuaHoanThanh == cvnList.size) {
                        phanTramHoanThanh.postValue("Hoàn thành: 0%")
                    }
                    val phanTram = hoanThanh.toDouble() / cvnList.size
                    phanTramHoanThanh.postValue(
                        "Hoàn thành: " + DecimalFormat("#.##").format(
                            phanTram * 100
                        ) + "%"
                    )
                }
                soViecCanLam.postValue("Bạn có " + (cvnList.size - hoanThanh) + " việc cần làm")
            }
            if (code == 404) {
                phanTramHoanThanh.postValue("")
                soViecCanLam.postValue("")
            }
        }
    }

    fun capNhatSoViecVaPhanTram2(ngay: String?) {
        viewModelScope.launch {
            val response = congViecservice.layDanhSachCongViecNgay(userId, ngay!!)
            var cvnList= response.body()
            var hoanThanh = 0
            var chuaHoanThanh = 0
            if (response.code() == 200) {
                if (cvnList != null) {
                    if (cvnList.size > 0) {
                        for (i in cvnList.indices) {
                            if (cvnList[i].trangThai) {
                                hoanThanh += 1
                            } else chuaHoanThanh += 1
                        }
                        if (hoanThanh == cvnList.size) {
                            phanTramHoanThanh.postValue("Hoàn thành: 100%")
                        } else if (chuaHoanThanh == cvnList.size) {
                            phanTramHoanThanh.postValue("Hoàn thành: 0%")
                        }
                        val phanTram = hoanThanh.toDouble() / cvnList.size
                        phanTramHoanThanh.postValue(
                            "Hoàn thành: " + DecimalFormat("#.##").format(
                                phanTram * 100
                            ) + "%"
                        )
                    }
                }
                if (cvnList != null) {
                    soViecCanLam.postValue("Bạn có " + (cvnList.size - hoanThanh) + " việc cần làm")
                }
            }
            if (response.code() == 404) {
                phanTramHoanThanh.postValue("")
                soViecCanLam.postValue("")
            }
        }
    }

    fun sapXepCvNgay(luaChon: Int) {
        viewModelScope.launch {
            val congViecNgayListTemp: MutableList<CongViecNgay> = ArrayList()
            congViecNgayListTemp.addAll(congViecNgayList)
            var congViecNgayListTempSorted: List<CongViecNgay> = ArrayList()
            if(luaChon==0){
                congViecNgayListTempSorted=congViecNgayListTemp
            }
            else if (luaChon == 1) {
                congViecNgayListTempSorted = congViecNgayListTemp.sortedBy { it.trangThai }
            } else if (luaChon == 2) {
                congViecNgayListTempSorted =
                    congViecNgayListTemp.sortedByDescending { it.trangThai }
            } else if (luaChon == 3) {
                congViecNgayListTempSorted = congViecNgayListTemp.sortedBy { it.congViec.tinhChat }
            } else if (luaChon == 4) {
                congViecNgayListTempSorted =
                    congViecNgayListTemp.sortedByDescending { it.congViec.tinhChat }

            }
            _danhSachCongViecNgay.emit(Resource.Success(congViecNgayListTempSorted))
        }
    }


    fun setThongTinCongViec(congViec: CongViec) {
        _thongTinCongViec.value = congViec
    }


}