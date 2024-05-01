package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.QuanLyCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.adapter.XemCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentEditCongViecBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentXemDanhSachCongViecNgayBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.viewModel.QuanLyNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class XemDanhSachCongViecNgayFragment : Fragment() {
    private var binding: FragmentXemDanhSachCongViecNgayBinding? = null
    private val sharedViewModel: QuanLyNgayViewModel by activityViewModels<QuanLyNgayViewModel>()
    private lateinit var adapter: XemCongViecNgayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentXemDanhSachCongViecNgayBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        khaiBaoAdapter()
        taiDanhSachCongViec()
        binding?.apply {
            tvNgay.text = convertToDDMMYYYY(sharedViewModel.ngay.value.toString())
            btnThemCongViec.setOnClickListener {
                findNavController().navigate(R.id.action_xemDanhSachCongViecNgayFragment_to_themCongViecFragment)
            }
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }




    private fun khaiBaoAdapter() {
        adapter = XemCongViecNgayAdapter()
        adapter.setOnItemClickListener(object : XemCongViecNgayAdapter.OnItemClickListener {
            override fun onImageButtonClick(congViecNgay: CongViecNgay?) {
                val bundle = Bundle()
                bundle.putInt("maCvNgay", congViecNgay!!.maCvNgay)
                view?.findNavController()?.navigate(
                    R.id.action_xemDanhSachCongViecNgayFragment_to_hinhAnhCongViecFragment,
                    bundle
                )
            }

            override fun onDeleteButtonClick(congViecNgay: CongViecNgay?) {
                xoaCongViecTrongNgay(congViecNgay)
            }

            override fun onStopTrackingTouch(congViecNgay: CongViecNgay?, percent: Int) {
                congViecNgay!!.phanTramHoanThanh = percent
                if (percent == 100)
                    congViecNgay.trangThai = true
                else congViecNgay.trangThai = false

                sharedViewModel.luuCongViecNgay(congViecNgay)
            }


        })
        binding?.apply {
            rvCongViec.adapter = adapter
            rvCongViec.setLayoutManager(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
        }

//        val chuKy = arrayOf(
//            "Một lần",
//            "Hằng ngày",
//            "Thứ 2 đến thứ 6",
//            "Hằng tuần",
//            "Hàng tháng",
//            "Hằng năm"
//        )
//        val tinhChat = arrayOf("Bình thường", "Quan trọng", "Rất quan trọng")
//        val adapterChuKy =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, chuKy)
//        val adapterTinhChat =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, tinhChat)
//        binding?.apply {
//            spnTinhChat.adapter = adapterTinhChat
//            spnTrangThai.adapter = adapterChuKy
//        }

    }

    private fun xoaCongViecTrongNgay(congViecNgay: CongViecNgay?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc muốn xóa công việc này không?")
            .setPositiveButton( "Có"
            ) { dialog, which ->
                if (congViecNgay != null) {
                    sharedViewModel.xoaCongViecNgayTrongNgay(congViecNgay.maCvNgay)
                }
            }
            .setNegativeButton("Không", null)
            .setIcon(R.drawable.baseline_announcement_24)
            .show()
    }

    private fun taiDanhSachCongViec() {
        sharedViewModel.taiDanhSachCongViec()
        lifecycleScope.launch {
            sharedViewModel.danhSachCongViecNgay.collectLatest {
                adapter.differ.submitList(it.data)
            }
        }
    }




    private fun convertToDDMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        var date: Date? = null
        date = try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        return outputFormat.format(date)
    }

    // Hàm chuyển đổi từ "dd-MM-yyyy" sang "yyyy-MM-dd"
    private fun convertToYYYYMMDD(date: String): String {
        val originalFormat = SimpleDateFormat("dd-MM-yyyy")
        val targetFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateObj: Date
        var formattedDate = ""
        try {
            dateObj = originalFormat.parse(date)
            formattedDate = targetFormat.format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }

}