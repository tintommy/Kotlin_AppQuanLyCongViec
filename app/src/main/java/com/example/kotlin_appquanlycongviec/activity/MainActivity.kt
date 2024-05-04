package com.example.kotlin_appquanlycongviec.activity

import android.app.NotificationChannel
import android.app.NotificationManager

import android.os.Build
import android.os.Bundle

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.ActivityMainBinding

import com.example.kotlin_appquanlycongviec.fragment.CongViecFragment
import com.example.kotlin_appquanlycongviec.fragment.GhiChuFragment
import com.example.kotlin_appquanlycongviec.fragment.SuKienFragment
import com.example.kotlin_appquanlycongviec.fragment.TaiKhoanFragment
import com.example.kotlin_appquanlycongviec.fragment.ThongKeFragment
import dagger.hilt.android.AndroidEntryPoint

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import com.example.kotlin_appquanlycongviec.fragment.ChiTietSuKienFragment
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.appHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomMenu.setupWithNavController(navController)

        createNotificationChannel()


//
//        binding.bottomMenu.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.congViecFragment -> {
//                  replaceFragment(CongViecFragment())
//                    true
//                }
//                R.id.ghiChuFragment -> {
//                   replaceFragment(GhiChuFragment())
//                    true
//                }
//                R.id.suKienFragment -> {
//                  replaceFragment(SuKienFragment())
//                    true
//                }
//                R.id.thongKeFragment -> {
//                    replaceFragment(ThongKeFragment())
//                    true
//                }
//                R.id.taiKhoanFragment -> {
//                    replaceFragment(TaiKhoanFragment())
//                    true
//                }
//                else -> false
//            }
//
//
//        }
    }



//    fun replaceFragment(fragment: Fragment) {
//
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.appHostFragment, fragment)
//        transaction.commit()
//
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("channel1", name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}