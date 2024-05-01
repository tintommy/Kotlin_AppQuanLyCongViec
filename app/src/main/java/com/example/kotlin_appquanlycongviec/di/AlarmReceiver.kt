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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tenSuKien = intent.getStringExtra("tenSuKien") // Lấy tên sự kiện từ intent
//        val moTa = intent.getStringExtra("moTa") // Lấy mô tả sự kiện từ intent
        val ngay = intent.getStringExtra("ngay")
        val gio = intent.getStringExtra("gio")


        if (tenSuKien != null) {

            if (ngay != null) {
                if (gio != null) {
                    showNotification(context, tenSuKien, ngay, gio)
                }
            }
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

        // Tạo một Intent để mở MainActivity khi thông báo được bấm
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // Sử dụng thời gian hiện tại để tạo một ID duy nhất cho mỗi thông báo
        val notificationId = Calendar.getInstance().timeInMillis.toInt()

        // Đổi định dạng ngày từ "yyyy-mm-dd" sang "dd-mm-yyyy"
        val ngayThang = dinhDangNgay(ngay)

        val builder = NotificationCompat.Builder(context, "channel1")
            .setSmallIcon(R.drawable.baseline_calendar_month_24)
            .setContentTitle("Sự kiện sắp diễn ra: $tenSuKien")
            .setContentText("Bạn có 1 sự kiện vào lúc $gio ngày $ngayThang")
            .setContentIntent(pendingIntent) // Đặt PendingIntent cho thông báo
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Tự động đóng thông báo khi bấm vào

        // Hiển thị thông báo
        notificationManager.notify(notificationId, builder.build())
    }

    private fun dinhDangNgay(ngay: String): String {
        val parts = ngay.split("-")
        val nam = parts[0]
        val thang = parts[1]
        val ngay = parts[2]
        return "$ngay-$thang-$nam"
    }
}