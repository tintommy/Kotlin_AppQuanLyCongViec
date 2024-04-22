package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentHinhAnhCongViecBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentQuanLyCongViecNgayBinding


class QuanLyCongViecNgayFragment : Fragment() {

    private lateinit var binding: FragmentQuanLyCongViecNgayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLyCongViecNgayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}