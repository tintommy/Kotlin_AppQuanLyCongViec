package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentTaoMaPinGhiChuBinding
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.NguoiDungViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaoMaPinGhiChuFragment : Fragment() {

    private lateinit var binding: FragmentTaoMaPinGhiChuBinding
    private val nguoiDungViewModel by viewModels<NguoiDungViewModel>()
    private var pin = ""
    private var rePin = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaoMaPinGhiChuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            if (checkPin()) {
                nguoiDungViewModel.savePin(pin)
            } else {
                binding.tvNotCorrect.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            nguoiDungViewModel.pin.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_taoMaPinGhiChuFragment_to_ghiChuFragment)
                        Toast.makeText(requireContext(), "Tạo PIN thành công", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {

                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }

    private fun checkPin(): Boolean {
        binding.apply {
            pin =
                binding.etPin1.text.toString() + binding.etPin2.text.toString() + binding.etPin3.text.toString() + binding.etPin4.text.toString() + binding.etPin5.text.toString() + binding.etPin6.text.toString()

            rePin =
                binding.etRePin1.text.toString() + binding.etRePin2.text.toString() + binding.etRePin3.text.toString() + binding.etRePin4.text.toString() + binding.etRePin5.text.toString() + binding.etRePin6.text.toString()
        }
        if (!pin.equals(rePin))
            return false
        else
            return true
    }

}