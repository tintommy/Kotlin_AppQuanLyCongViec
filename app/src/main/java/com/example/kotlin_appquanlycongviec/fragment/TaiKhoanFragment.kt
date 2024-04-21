package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide

import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentTaiKhoanBinding
import com.example.kotlin_appquanlycongviec.model.NguoiDung
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TaiKhoanFragment : Fragment() {
    private lateinit var binding: FragmentTaiKhoanBinding
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private lateinit var nguoiDung: NguoiDung
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTaiKhoanBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.fragment_tai_khoan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nguoiDungViewModel.getUser()
        lifecycleScope.launch {
            nguoiDungViewModel.user.collectLatest {
                when(it)
                {
                    is Resource.Success ->{
                        nguoiDung= it.data!!
                    }
                    else ->{}
                }
            }
        }




    }

}