package com.example.kotlin_appquanlycongviec.fragment.login_signup

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentSignUpBinding
import com.example.kotlin_appquanlycongviec.request.SignUpRequest
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0

    private var gender: Boolean = true


    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonEvent()

        lifecycleScope.launch {
            nguoiDungViewModel.emailExist.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        binding.edtEmail.error = "Email đã được dùng"

                    }

                    is Resource.Error -> {
                        binding.edtEmail.error = null
                        val signupRequest = getSignUpRequest()
                        val b = Bundle()
                        b.putSerializable("signupRequest", signupRequest)
                        view?.findNavController()
                            ?.navigate(R.id.action_signUpFragment_to_otpFragment, b)
                    }

                    else -> {}

                }
            }
        }
    }

    private fun setButtonEvent() {
        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }
        binding.edtBirthday.setOnClickListener {
            openLichDialog()
        }
        binding.btnRegister.setOnClickListener {
            if (checkInput()) {
                nguoiDungViewModel.checkUserEmail(binding.edtEmail.text.toString())




            }

        }
        binding.radioGroupGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonMale -> {
                    gender = true
                }

                R.id.radioButtonFemale -> {
                    gender = false
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }

    }

    private fun getSignUpRequest(): SignUpRequest {
        return SignUpRequest(
            binding.edtEmail.text.toString(),
            binding.edtLastname.text.toString(),
            binding.edtFirstname.text.toString(),
            binding.edtPassword.text.toString(),
            dinhDangNgayAPI(ngay, thang, nam),
            gender
        )
    }

    private fun openLichDialog() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.edtBirthday.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang = month
                nam = year
            }, nam, thang, ngay
        )
        dialog.show()
    }

    private fun dinhDangNgay(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        temp += "/"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "/"
        temp += nam
        return temp
    }


    private fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += nam
        temp += "-"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "-"
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        return temp
    }

    private fun checkInput(): Boolean {
        var ho = binding.edtFirstname.text.toString()
        var ten = binding.edtLastname.text.toString()
        var email = binding.edtEmail.text.toString()
        var ngaySinh = binding.edtBirthday.text.toString()
        var matKhau = binding.edtPassword.text.toString()
        var matKhau2 = binding.edtReEnterPassword.text.toString()
        var check = 0
        binding.edtEmail.setError(null)
        binding.tvRepass.setError(null)
        if (ho.equals("") || ten.equals("") || email.equals("") || ngaySinh.equals("") || matKhau.equals(
                ""
            ) || matKhau2.equals("")
        ) {
            Toast.makeText(requireContext(), "Hãy nhập đủ các trường", Toast.LENGTH_SHORT).show()
            check = 1
        }

        if (!isEmailValid(email)) {
            binding.edtEmail.error = "Email sai định dạng "
            check = 1
        }



        if (!matKhau.equals(matKhau2)) {
            binding.tvRepass.error = "Xác nhận mật khẩu không đúng"
            check = 1
        }

        if (check == 1)
            return false

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

}