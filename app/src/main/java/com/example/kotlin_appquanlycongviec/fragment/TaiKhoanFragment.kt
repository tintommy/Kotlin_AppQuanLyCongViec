package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide

import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.activity.LogInSignUpActivity
import com.example.kotlin_appquanlycongviec.databinding.FragmentTaiKhoanBinding

import com.example.kotlin_appquanlycongviec.model.NguoiDung
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaiKhoanFragment : Fragment() {
    private lateinit var sharedPref:SharedPreferences
    private lateinit var binding: FragmentTaiKhoanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTaiKhoanBinding.inflate(layoutInflater)
        sharedPref= requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogOut.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Xác nhận đăng xuất ?")
            builder.setCancelable(false)
            builder.setPositiveButton("Đăng xuất") { dialog, which ->
                val editor = sharedPref.edit()
                editor.remove("token")
                editor.remove("userEmail")
                editor.remove("userId")
                editor.apply()

                Toast.makeText(requireContext(), "Đã đăng xuất ", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    requireContext(),
                    LogInSignUpActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            builder.setNegativeButton("Huỷ") { dialog, which -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()




        }



    }

}