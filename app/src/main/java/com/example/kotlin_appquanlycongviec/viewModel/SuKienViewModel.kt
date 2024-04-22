package com.example.kotlin_appquanlycongviec.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.LoginApiService
import com.example.kotlin_appquanlycongviec.api.apiService.NguoiDungApiService
import com.example.kotlin_appquanlycongviec.api.apiService.SuKienApiService
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.request.Token
import com.example.kotlin_appquanlycongviec.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuKienViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {

    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var userEmail: String
    private lateinit var suKienApiService: SuKienApiService
    private val _todayEvent = MutableStateFlow<Resource<List<SuKien>>>(Resource.Unspecified())
    val todayEvent = _todayEvent.asStateFlow()

    private val _nearlyEvent = MutableStateFlow<Resource<List<SuKien>>>(Resource.Unspecified())
    val nearlyEvent = _nearlyEvent.asStateFlow()

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.userId = sharedPref.getInt("userId", 0)
        this.userEmail = sharedPref.getString("userEmail", "").toString()
        var retrofit = ApiInstance.getClient(token)
        suKienApiService = retrofit.create(SuKienApiService::class.java)


    }

    fun getTodayEvent(ngay: String) {
        viewModelScope.launch {
            _todayEvent.emit(Resource.Loading())
            val response = suKienApiService.layDanhSachSuKienHomNay(userId, ngay)
            if (response.isSuccessful) {
                val sortedList = response.body()!!.sortedWith(compareBy(SuKien::ngay, SuKien::gio))
                _todayEvent.emit(Resource.Success(sortedList))
            } else {
                if (response.code() == 404) {
                    _todayEvent.emit(Resource.Error("404"))
                }
            }
        }
    }

    fun getNearlyEvent(ngay: String) {
        viewModelScope.launch {
            _nearlyEvent.emit(Resource.Loading())
            val response = suKienApiService.layDanhSachSuKienSapToi(userId, ngay)
            if (response.isSuccessful) {
                val sortedList = response.body()!!.sortedWith(compareBy(SuKien::ngay, SuKien::gio))
                _nearlyEvent.emit(Resource.Success(sortedList))
            } else {
                if (response.code() == 404) {
                    _nearlyEvent.emit(Resource.Error("404"))
                }
            }
        }
    }
}