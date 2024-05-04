package com.example.kotlin_appquanlycongviec.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.SuKienApiService
import com.example.kotlin_appquanlycongviec.di.AlarmReceiver

import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.request.Status
import com.example.kotlin_appquanlycongviec.util.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class SuKienViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {

    private var lastNotificationId = 0

    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var userEmail: String
    private lateinit var suKienApiService: SuKienApiService
    private val _todayEvent = MutableStateFlow<Resource<MutableList<SuKien>>>(Resource.Unspecified())
    val todayEvent = _todayEvent.asStateFlow()

    private val _nearlyEvent = MutableStateFlow<Resource<MutableList<SuKien>>>(Resource.Unspecified())
    val nearlyEvent = _nearlyEvent.asStateFlow()

    private val _allEvent = MutableStateFlow<Resource<MutableList<SuKien>>>(Resource.Unspecified())
    val allEvent = _allEvent.asStateFlow()

    private val _addEvent = MutableStateFlow<Resource<SuKien>>(Resource.Unspecified())

    val addEvent = _addEvent.asStateFlow()
    private val _updateEvent = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val updateEvent = _updateEvent.asStateFlow()
    private val _deleteEvent = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val deleteEvent = _deleteEvent.asStateFlow()
    private val _allEventFromDateToDate = MutableStateFlow<Resource<MutableList<SuKien>>>(Resource.Unspecified())
    val allEventFromDateToDate = _allEventFromDateToDate.asStateFlow()


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
                _todayEvent.emit(Resource.Success(sortedList.toMutableList()))
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
                _nearlyEvent.emit(Resource.Success(sortedList.toMutableList()))
            } else {
                if (response.code() == 404) {
                    _nearlyEvent.emit(Resource.Error("404"))
                }
            }
        }
    }

    //ham tao thong bao
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, suKien: SuKien) {

        if (suKien.nhacTruoc != -1) { // Kiểm tra nếu chọn không nhắc trước thì không cần thông báo

            Log.d("ScheduleNotification", "Scheduling notification for event with ID: ${suKien.maSK}")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val gson = Gson()
            val suKienJson = gson.toJson(suKien)

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("suKien", suKienJson)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                suKien.maSK,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )



            // Chuyển đổi ngày và giờ của sự kiện thành định dạng thời gian millis
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val ngayGioString = "${suKien.ngay} ${suKien.gio}"
            val ngayGioDate = dateFormat.parse(ngayGioString)
            val nhacTruoc = suKien.nhacTruoc
            val notificationTimeMillis = ngayGioDate.time - nhacTruoc * 60 * 60 * 1000

            // Đặt lịch thông báo
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        }
    }


//    ham them su kien
    fun addEvent(context: Context, suKien: SuKien) {
        viewModelScope.launch {
            _addEvent.emit(Resource.Loading())
            val response = suKienApiService.themSuKien(suKien, userId)
            if (response.isSuccessful) {
                _addEvent.emit(Resource.Success(response.body()!!))
                // Tạo thông báo mới với thông tin sự kiện đã cập nhật
                scheduleNotification(context, response.body()!!)
            } else {
                _addEvent.emit(Resource.Error("404"))
            }
        }
    }



    //ham cap nhat su kien
    fun updateEvent(context: Context, suKien: SuKien) {
        viewModelScope.launch {
            _updateEvent.emit(Resource.Loading())
            val response = suKienApiService.suaSuKien(suKien, userId)
            if (response.isSuccessful) {
                _updateEvent.emit(Resource.Success(response.body()!!))

                // Hủy thông báo cũ
//                cancelNotification(context, suKien.maSK)

                // Tạo thông báo mới với thông tin sự kiện đã cập nhật
                scheduleNotification(context, suKien)
            } else {
                _updateEvent.emit(Resource.Error("404"))
            }
        }
    }


    //ham xoa su kien
    fun deleteEvent(maSuKien: Int, context: Context) {
        viewModelScope.launch {
            _deleteEvent.emit(Resource.Loading())
            val response = suKienApiService.xoaSuKien(maSuKien)
            if (response.isSuccessful) {
                _deleteEvent.emit(Resource.Success(response.body()!!))

                // Hủy thông báo
                cancelNotification(context, maSuKien)
            } else {
                _deleteEvent.emit(Resource.Error("404"))
            }
        }
    }

    private fun cancelNotification(context: Context, maSuKien: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            maSuKien,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            Log.d("CancelNotification", "Cancel notification for event with ID: $maSuKien")
        }
        else
            Log.d("CancelNotification", "No notification found for event with ID: $maSuKien")
    }


    fun getAllEvent(){
        viewModelScope.launch{
            _allEvent.emit(Resource.Loading())
            val response= suKienApiService.layTatCaSuKien(userId)
            if (response.isSuccessful){
                val sortedList = response.body()!!.sortedWith(compareBy(SuKien::ngay, SuKien::gio))
                _allEvent.emit(Resource.Success(sortedList.toMutableList()))}
            else
                _allEvent.emit(Resource.Error("404"))
        }


    }

        fun getEventFromDateToDate(ngayBatDau: String, ngayKetThuc: String) {
        viewModelScope.launch {
            _allEventFromDateToDate.emit(Resource.Loading())
            val response = suKienApiService.laySuKienTuNgayDenNgay(userId, ngayBatDau, ngayKetThuc)
            if (response.isSuccessful) {
                val sortedList = response.body()!!.sortedWith(compareBy(SuKien::ngay, SuKien::gio))
                _allEventFromDateToDate.emit(Resource.Success(sortedList.toMutableList()))
            } else
                _allEventFromDateToDate.emit(Resource.Error("404"))
        }
    }

}