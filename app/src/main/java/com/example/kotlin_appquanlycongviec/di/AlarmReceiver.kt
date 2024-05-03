package com.example.kotlin_appquanlycongviec.di
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.MainActivity
import com.example.kotlin_appquanlycongviec.fragment.SuKienFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tenSuKien = intent.getStringExtra("tenSuKien")
        val ngay = intent.getStringExtra("ngay")
        val gio = intent.getStringExtra("gio")

        if (tenSuKien != null && ngay != null && gio != null) {
            showNotification(context, tenSuKien, ngay, gio)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        tenSuKien: String,
        ngay: String,
        gio: String
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        //tao intent de mo MainActivity va chuyen den fragment SuKienFragment
        val intent = Intent(context, MainActivity::class.java)
        intent.action = "OPEN_SUKIEN_FRAGMENT_FROM_NOTIFICATION"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = Calendar.getInstance().timeInMillis.toInt()
        val ngayThang = dinhDangNgay(ngay)

        val builder = NotificationCompat.Builder(context, "channel1")
            .setSmallIcon(R.drawable.baseline_calendar_month_24)
            .setContentTitle("Sự kiện sắp diễn ra: $tenSuKien")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val ngayHienTai = Calendar.getInstance()
        val ngaySuKien = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(ngay) ?: Date()
        }

        val contentText = when {
            ngayHienTai.isSameDay(ngaySuKien) -> {
                "Bạn có sự kiện vào lúc $gio hôm nay"
            }
            ngayHienTai.isNextDay(ngaySuKien) -> {
                "Bạn có sự kiện vào lúc $gio ngày mai"
            }
            else -> {
                "Bạn có sự kiện vào lúc $gio ngày $ngayThang"
            }
        }

        builder.setContentText(contentText)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun Calendar.isSameDay(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
    }

    private fun Calendar.isNextDay(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH) -1
    }

    private fun dinhDangNgay(ngay: String): String {
        val parts = ngay.split("-")
        val nam = parts[0]
        val thang = parts[1]
        val ngay = parts[2]
        return "$ngay-$thang-$nam"
    }
}