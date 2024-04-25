package com.example.kotlin_appquanlycongviec.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.appHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomMenu.setupWithNavController(navController)


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
//
//    }
//    fun replaceFragment(fragment: Fragment) {
//
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.appHostFragment, fragment)
//        transaction.commit()
//
    }
}