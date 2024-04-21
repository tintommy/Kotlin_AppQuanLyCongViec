package com.example.kotlin_appquanlycongviec.fragment.login_signup

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentOtpBinding
import com.example.kotlin_appquanlycongviec.request.SignUpRequest
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding

    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var signUpRequest: SignUpRequest
    private lateinit var otp: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            signUpRequest = bundle.getSerializable("signupRequest") as SignUpRequest
        }
        init()
        nguoiDungViewModel.getOTP(signUpRequest.email)

        lifecycleScope.launch {
            nguoiDungViewModel.otp.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        otp = it.data!!.otp
                        Toast.makeText(requireContext(), "OTP đã được gửi", Toast.LENGTH_SHORT)
                            .show()
                        binding.btnConfirm.revertAnimation()
                        startCountdown()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Có lỗi khi gửi OTP", Toast.LENGTH_SHORT)
                            .show()
                        binding.btnConfirm.revertAnimation()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            nguoiDungViewModel.signup.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Đăng kí tài khoản thành công",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        view.findNavController().navigate(R.id.action_otpFragment_to_logInFragment)
                        binding.btnConfirm.revertAnimation()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.btnConfirm.revertAnimation()
                    }

                    else -> {}
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            binding.tvNotCorrect.visibility = View.GONE
            if (binding.etOTP.text.toString() == otp) {
                binding.tvNotCorrect.visibility = View.GONE
                nguoiDungViewModel.userSignup(signUpRequest)


            } else {
                binding.tvNotCorrect.visibility = View.VISIBLE
            }
        }

        binding.btnReOTP.setOnClickListener {
            nguoiDungViewModel.getOTP(signUpRequest.email)
        }
    }

    fun init() {
        binding.tvEmail.text = signUpRequest.email



        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    view?.findNavController()?.navigateUp()
                }
                return true
            }
        })
    }

    private fun startCountdown() {
        binding.btnReOTP.isEnabled = false

        var countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.btnReOTP.text = "Có thể gửi lại mã sau $seconds giây"
                println("Seconds remaining: $seconds")
            }

            override fun onFinish() {
                binding.btnReOTP.isEnabled = true
                binding.btnReOTP.text = "Gửi lại mã "
            }
        }

        countDownTimer.start()
    }
}