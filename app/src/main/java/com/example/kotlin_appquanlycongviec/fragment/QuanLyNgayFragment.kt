package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.DanhSachNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentQuanLyNgayBinding
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import com.example.kotlin_appquanlycongviec.viewModel.QuanLyNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class QuanLyNgayFragment : Fragment() {
    private lateinit var binding: FragmentQuanLyNgayBinding
    private val sharedViewModel: QuanLyNgayViewModel by activityViewModels<QuanLyNgayViewModel>()
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
        binding.btnTao.setOnClickListener {
            sharedViewModel.setNgay(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Calendar.getInstance().time))
            findNavController().navigate(R.id.action_quanLyNgayFragment_to_quanLyCongViecNgayFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun khaiBaoAdapter() {
        danhSachNgayAdapter = DanhSachNgayAdapter()
        danhSachNgayAdapter.setOnItemClickListener(object :
            DanhSachNgayAdapter.OnItemClickListener {
            override fun onItemClick(ngayDaTaoResponse: NgayDaTaoResponse) {

            }

            override fun onDeleteButtonClick(ngayDaTaoResponse: NgayDaTaoResponse) {
                xoaCongViecTrongNgay(ngayDaTaoResponse)
            }

            override fun onDetailClick(ngayDaTaoResponse: NgayDaTaoResponse) {
                sharedViewModel.setNgay(ngayDaTaoResponse.ngay)
                findNavController().navigate(R.id.action_quanLyNgayFragment_to_xemDanhSachCongViecNgayFragment)
            }

        })
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
        sharedViewModel.taiDanhNgay()
        lifecycleScope.launch {
            sharedViewModel.danhSachNgay.collectLatest {
                danhSachNgayAdapter.differ.submitList(it.data)
            }
        }
    }

    fun xoaCongViecTrongNgay(ngay:NgayDaTaoResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc muốn xóa tất cả công việc trong ngày ${ngay.ngay} không?")
            .setPositiveButton( "Có"
            ) { dialog, which ->
                sharedViewModel.xoaCongViecNgayTrongNgay(ngay.ngay)
                lifecycleScope.launch {
                    sharedViewModel.soViecDaXoa.collectLatest {
                        if(it.data!! > 0 && it.message !="404" ) {
                            Toast.makeText(context,"Xóa thành công ${it.data} việc trong ngày ${ngay.ngay}",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Không", null)
            .setIcon(R.drawable.baseline_announcement_24)
            .show()

    }

}