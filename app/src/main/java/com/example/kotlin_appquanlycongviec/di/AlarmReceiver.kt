package com.example.kotlin_appquanlycongviec.di
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.MainActivity
import com.example.kotlin_appquanlycongviec.fragment.SuKienFragment
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val tenSuKien = intent.getStringExtra("tenSuKien")
//        val ngay = intent.getStringExtra("ngay")
//        val gio = intent.getStringExtra("gio")
//
//        if (tenSuKien != null && ngay != null && gio != null) {
//            showNotification(context, tenSuKien, ngay, gio)
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun showNotification(
//        context: Context,
//        tenSuKien: String,
//        ngay: String,
//        gio: String
//    ) {
//        val notificationManager = NotificationManagerCompat.from(context)
//
//        // Tạo intent để mở MainActivity
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//
//        val notificationId = Calendar.getInstance().timeInMillis.toInt()
//        val ngayThang = dinhDangNgay(ngay)
//
//        val builder = NotificationCompat.Builder(context, "channel1")
//            .setSmallIcon(R.drawable.baseline_calendar_month_24)
//            .setContentTitle("Sự kiện sắp diễn ra: $tenSuKien")
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//
//        val ngayHienTai = Calendar.getInstance()
//        val ngaySuKien = Calendar.getInstance().apply {
//            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(ngay) ?: Date()
//        }
//
//        val contentText = when {
//            ngayHienTai.isSameDay(ngaySuKien) -> {
//                "Bạn có sự kiện vào lúc $gio hôm nay"
//            }
//            ngayHienTai.isNextDay(ngaySuKien) -> {
//                "Bạn có sự kiện vào lúc $gio ngày mai"
//            }
//            else -> {
//                "Bạn có sự kiện vào lúc $gio ngày $ngayThang"
//            }
//        }
//
//        builder.setContentText(contentText)
//
//        notificationManager.notify(notificationId, builder.build())
//    }
//
//    private fun Calendar.isSameDay(other: Calendar): Boolean {
//        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
//                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
//                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
//    }
//
//    private fun Calendar.isNextDay(other: Calendar): Boolean {
//        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
//                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
//                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH) -1
//    }
//
//    private fun dinhDangNgay(ngay: String): String {
//        val parts = ngay.split("-")
//        val nam = parts[0]
//        val thang = parts[1]
//        val ngay = parts[2]
//        return "$ngay-$thang-$nam"
//    }
//}


class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val suKienJson = intent.getStringExtra("suKien")
        val gson = Gson()
        val suKien = gson.fromJson(suKienJson, SuKien::class.java)

        suKien?.let {
            showNotification(context, it)
            Log.d("ShowNotification", "Show notification for event with ID-al: ${suKien.maSK}")
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        suKien: SuKien
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

//        // Tạo intent để mở MainActivity
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
//            PendingIntent.FLAG_IMMUTABLE)

        // tao intent tới SuKienFragment
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.suKienFragment)
            .createPendingIntent()

//            .setDestination(R.id.chiTietSuKienFragment)
//            .setArguments(Bundle().apply {
//                putInt("maSK", suKien.maSK)
//            })
//            .createPendingIntent()


        val notificationId = Calendar.getInstance().timeInMillis.toInt()
        val ngayThang = dinhDangNgay(suKien.ngay)

        val builder = NotificationCompat.Builder(context, "channel1")
            .setSmallIcon(R.drawable.baseline_calendar_month_24)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val ngaySuKien = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(suKien.ngay) ?: Date()
        }

        val gioSuKien = Calendar.getInstance().apply {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(suKien.gio) ?: Date()
        }

        val ngayHienTai = Calendar.getInstance()


        val contentText = when {
            ngaySuKien.isSameDay(ngayHienTai) -> {
                if (isSameTime(ngayHienTai, gioSuKien)) {
                    "Nhắc bạn, đã đến thời gian của sự kiện"
                } else {
                    "Bạn có sự kiện vào lúc ${suKien.gio} hôm nay"
                }
            }
            ngayHienTai.isNextDay(ngaySuKien) -> {
                "Bạn có sự kiện vào lúc ${suKien.gio} ngày mai"
            }
            else -> {
                "Bạn có sự kiện vào lúc ${suKien.gio} ngày $ngayThang"
            }
        }

        val title = when {
            ngaySuKien.isSameDay(ngayHienTai) && isSameTime(ngayHienTai, gioSuKien) -> {
                "Sự kiện đang diễn ra: ${suKien.tenSuKien}"
            }
            else -> {
                "Sự kiện sắp diễn ra: ${suKien.tenSuKien}"
            }
        }


        builder.setContentTitle(title)
        builder.setContentText(contentText)

        notificationManager.notify(suKien.maSK, builder.build())
    }

    private fun Calendar.isSameDay(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
    }

    private fun Calendar.isNextDay(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH) - 1
    }



    private fun isSameTime(other: Calendar, gioSuKien: Calendar): Boolean {
        val currentHour = other.get(Calendar.HOUR_OF_DAY)
        val currentMinute = other.get(Calendar.MINUTE)
        val eventHour = gioSuKien.get(Calendar.HOUR_OF_DAY)
        val eventMinute = gioSuKien.get(Calendar.MINUTE)
        return eventHour == currentHour && eventMinute == currentMinute
    }


    private fun dinhDangNgay(ngay: String): String {
        val parts = ngay.split("-")
        val nam = parts[0]
        val thang = parts[1]
        val ngay = parts[2]
        return "$ngay-$thang-$nam"
    }
}
