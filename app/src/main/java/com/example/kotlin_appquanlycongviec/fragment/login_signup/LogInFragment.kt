package com.example.kotlin_appquanlycongviec.fragment.login_signup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.MainActivity
import com.example.kotlin_appquanlycongviec.databinding.FragmentLogInBinding
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var binding : FragmentLogInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonEvent()

        lifecycleScope.launch {

                nguoiDungViewModel.login.collectLatest {
                    when (it) {

                        is Resource.Loading -> {
                            binding.btnLogin.startAnimation()
                           // binding.tvThongBao.text = ""
                        }

                        is Resource.Success -> {

                            binding.btnLogin.revertAnimation()
                            val intent = Intent(
                                requireContext(),
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            Toast.makeText(requireContext(), "Đăng nhập vào "+ binding.edtEmail.text.toString(), Toast.LENGTH_SHORT).show()
                        }

                        is Resource.Error -> {
                            binding.btnLogin.revertAnimation()
                            Toast.makeText(requireContext(), "Sai email hoặc mật khẩu", Toast.LENGTH_LONG).show()
                        }

                        else -> {}
                    }


            }
        }



    }

    private fun setButtonEvent(){
        binding.btnLogin.setOnClickListener {
            if(binding.edtEmail.text.toString().equals("")||binding.edtPassword.text.toString().equals(""))
            {
                Toast.makeText(requireContext(), "Hãy nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
            else
            nguoiDungViewModel.userLogin(binding.edtEmail.text.toString(),binding.edtPassword.text.toString())
        }

        binding.registerNow.setOnClickListener {
            it.findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }
}