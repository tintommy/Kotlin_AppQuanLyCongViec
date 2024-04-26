package com.example.kotlin_appquanlycongviec.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.GhiChuApiService
import com.example.kotlin_appquanlycongviec.api.apiService.LoginApiService
import com.example.kotlin_appquanlycongviec.api.apiService.NguoiDungApiService
import com.example.kotlin_appquanlycongviec.model.GhiChu
import com.example.kotlin_appquanlycongviec.request.Status
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GhiChuViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var userEmail: String
    private lateinit var ghiChuApiService: GhiChuApiService
    private val _ghiChuList: MutableStateFlow<Resource<MutableList<GhiChu>>> =
        MutableStateFlow(Resource.Unspecified())
    val ghiChuList = _ghiChuList.asStateFlow()

    private val _luuGhiChu: MutableStateFlow<Resource<GhiChu>> =
        MutableStateFlow(Resource.Unspecified())
    val luuGhiChu = _luuGhiChu.asStateFlow()
    private val _xoaGhiChu: MutableStateFlow<Resource<Status>> =
        MutableStateFlow(Resource.Unspecified())
    val xoaGhiChu = _xoaGhiChu.asStateFlow()


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.userId = sharedPref.getInt("userId", 0)
        this.userEmail = sharedPref.getString("userEmail", "").toString()
        var retrofit = ApiInstance.getClient(token)
        ghiChuApiService = retrofit.create(GhiChuApiService::class.java)


    }

    fun taiDsGhiChu() {

        viewModelScope.launch {
            _ghiChuList.emit(Resource.Loading())
            val response = ghiChuApiService.layDanhSachGhiChuCuaNguoiDung(userId)
            if (response.isSuccessful) {
                _ghiChuList.emit(Resource.Success(response.body()!!.toMutableList()))
            } else {
                if(response.code()==404)
                    _ghiChuList.emit(Resource.Error("404"))
                else
                _ghiChuList.emit(Resource.Error("Lỗi khi tải danh sách"))
            }
        }

    }

    fun luuGhiChu(gccn: GhiChu){
        viewModelScope.launch {
            _luuGhiChu.emit(Resource.Loading())
            val response = ghiChuApiService.luuGhiChu(userEmail,gccn)
            if(response.isSuccessful){

                _luuGhiChu.emit(Resource.Success(response.body()!!))}
            else
                _luuGhiChu.emit(Resource.Error("Xảy ra lỗi khi lưu ghi chú"))
        }
    }
    fun xoaGhiChu(maGhiChu: Int){
        viewModelScope.launch {
            _xoaGhiChu.emit(Resource.Loading())
            val response = ghiChuApiService.xoaGhiChu(maGhiChu)
            if(response.isSuccessful)
                _xoaGhiChu.emit(Resource.Success(response.body()!!))
            else
                _xoaGhiChu.emit(Resource.Error("Xảy ra lỗi khi xoá ghi chú"))
        }
    }


}