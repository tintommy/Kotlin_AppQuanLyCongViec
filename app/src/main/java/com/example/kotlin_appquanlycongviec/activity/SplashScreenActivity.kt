package com.example.kotlin_appquanlycongviec.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var sharedPref: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPref = application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

        handler.postDelayed({
            nguoiDungViewModel.getUser()
            lifecycleScope.launchWhenStarted {
                nguoiDungViewModel.user.collectLatest {
                    when (it) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Toast.makeText(
                                this@SplashScreenActivity,
                                "Đã đăng nhập vào " + it.data!!.email,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@SplashScreenActivity,
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }

                        is Resource.Error -> {
                            val editor = sharedPref.edit()
                            editor.remove("token")
                            editor.remove("username")
                            editor.apply()
                            val intent = Intent(
                                this@SplashScreenActivity,
                                LogInSignUpActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }

                        else -> {}
                    }
                }

            }
            finish()
        }, 1500)
    }

}