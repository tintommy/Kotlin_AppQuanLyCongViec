package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.DanhSachCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.adapter.DanhSachNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentCongViecBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentQuanLyNgayBinding
import com.example.kotlin_appquanlycongviec.viewModel.EditCongViecViewModel
import com.example.kotlin_appquanlycongviec.viewModel.QuanLyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuanLyNgayFragment : Fragment() {
    private lateinit var binding: FragmentQuanLyNgayBinding
    private val viewModel: QuanLyViewModel by viewModels<QuanLyViewModel>()
    private lateinit var danhSachNgayAdapter: DanhSachNgayAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLyNgayBinding.inflate(inflater,container,false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        khaiBaoAdapter()
        taiDanhSachNgay()

    }

    private fun khaiBaoAdapter() {
        danhSachNgayAdapter = DanhSachNgayAdapter()
        binding.rvNgay.setAdapter(danhSachNgayAdapter)
        binding.rvNgay.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )
    }

    fun taiDanhSachNgay() {
        viewModel.taiDanhNgay()
        lifecycleScope.launch {
            viewModel.danhSachNgay.collectLatest {
                danhSachNgayAdapter.differ.submitList(it.data)
            }
        }

    }

}