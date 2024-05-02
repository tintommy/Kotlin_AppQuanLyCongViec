package com.example.kotlin_appquanlycongviec.viewModel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.content.SharedPreferences
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class SuKienViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {

    private var notificationRequestCode = 0 // Khai báo biến global
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

    private val _addEvent = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val addEvent = _addEvent.asStateFlow()
    private val _updateEvent = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val updateEvent = _updateEvent.asStateFlow()
    private val _deleteEvent = MutableStateFlow<Resource<Status>>(Resource.Unspecified())
    val deleteEvent = _deleteEvent.asStateFlow()
    private val _allEventFromDateToDate = MutableStateFlow<Resource<MutableList<SuKien>>>(Resource.Unspecified())
    val allEventFromDateToDate = _allEventFromDateToDate.asStateFlow()

    private val _newEventAdded = MutableLiveData<SuKien>()
    val newEventAdded: LiveData<SuKien> = _newEventAdded

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

//    fun addEvent(suKien: SuKien) {
//        viewModelScope.launch {
//            _addEvent.emit(Resource.Loading())
//            val response = suKienApiService.themSuKien(suKien, userId)
//            if (response.isSuccessful) {
//                _addEvent.emit(Resource.Success(response.body()!!))
//
//                // Lên lịch thông báo khi thêm sự kiện thành công
////                scheduleNotification(suKien)
//
//                _newEventAdded.postValue(suKien)
//            } else {
//                _addEvent.emit(Resource.Error("404"))
//            }
//        }
//    }

    fun addEvent(context: Context, suKien: SuKien) {
        viewModelScope.launch {
            _addEvent.emit(Resource.Loading())
            val response = suKienApiService.themSuKien(suKien, userId)
            if (response.isSuccessful) {
                _addEvent.emit(Resource.Success(response.body()!!))

                // Lên lịch thông báo sk
                scheduleNotification(context, suKien)

            } else {
                _addEvent.emit(Resource.Error("404"))
            }
        }
    }



    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(context: Context, suKien: SuKien) {
        if (suKien.nhacTruoc != -1) { // Kiểm tra nếu chọn không nhắc trước thì không cần thông báo
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Tăng giá trị của notificationRequestCode
            notificationRequestCode++

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("tenSuKien", suKien.tenSuKien)
                putExtra("moTa", suKien.moTa)
                putExtra("ngay", suKien.ngay)
                putExtra("gio", suKien.gio)
                putExtra("maSK", suKien.maSK)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationRequestCode, // Sử dụng notificationRequestCode thay vì maSK
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




//    fun updateEvent(suKien: SuKien){
//        viewModelScope.launch{
//            _updateEvent.emit(Resource.Loading())
//            val response= suKienApiService.suaSuKien(suKien,userId)
//            if (response.isSuccessful)
//                _updateEvent.emit(Resource.Success(response.body()!!))
//            else
//                _updateEvent.emit(Resource.Error("404"))
//        }
//    }


    fun updateEvent(context: Context, suKien: SuKien) {
        viewModelScope.launch {
            _updateEvent.emit(Resource.Loading())
            val response = suKienApiService.suaSuKien(suKien, userId)
            if (response.isSuccessful) {
                _updateEvent.emit(Resource.Success(response.body()!!))

                // Hủy thông báo cũ
                cancelNotification(context, suKien.maSK)

                // Tạo thông báo mới với thông tin sự kiện đã cập nhật
                scheduleNotification(context, suKien)
            } else {
                _updateEvent.emit(Resource.Error("404"))
            }
        }
    }


    fun deleteEvent(maSuKien: Int) {
        viewModelScope.launch {
            _deleteEvent.emit(Resource.Loading())
            val response = suKienApiService.xoaSuKien(maSuKien)
            if (response.isSuccessful)
                _deleteEvent.emit(Resource.Success(response.body()!!))
            else
                _deleteEvent.emit(Resource.Error("404"))
        }

    }





//    fun deleteEvent(context: Int, maSuKien: Int) {
//        viewModelScope.launch {
//            _deleteEvent.emit(Resource.Loading())
//            val response = suKienApiService.xoaSuKien(maSuKien)
//            if (response.isSuccessful) {
//                _deleteEvent.emit(Resource.Success(response.body()!!))
//
//                // Hủy thông báo tương ứng với sự kiện đã bị xoá
//                cancelNotification(context, maSuKien)
//            } else {
//                _deleteEvent.emit(Resource.Error("404"))
//            }
//        }
//    }

    private fun cancelNotification(context: Context, maSK: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, maSK, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
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
//    fun getEventFromDateToDate(ngayBatDau: String, ngayKetThuc: String) {
//        viewModelScope.launch {
//            _allEvent.emit(Resource.Loading())
//            val response = suKienApiService.laySuKienTuNgayDenNgay(userId, ngayBatDau, ngayKetThuc)
//            if (response.isSuccessful) {
//                val sortedList = response.body()!!.sortedWith(compareBy(SuKien::ngay, SuKien::gio))
//                _allEventFromDateToDate.emit(Resource.Success(sortedList.toMutableList()))
//            } else
//                _allEventFromDateToDate.emit(Resource.Error("404"))
//        }
//    }
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