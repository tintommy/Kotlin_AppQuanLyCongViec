package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.QuanLyCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentQuanLyCongViecNgayBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.viewModel.QuanLyNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class QuanLyCongViecNgayFragment : Fragment() {

    private lateinit var binding: FragmentQuanLyCongViecNgayBinding
    private val sharedViewModel: QuanLyNgayViewModel by activityViewModels()
    private lateinit var adapter: QuanLyCongViecNgayAdapter

    private var dpNgay: DatePickerDialog? = null


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
        khaiBaoDatePicker()
        khaiBaoAdapter()
        taiDanhSachCongViec()
        binding.edtNgay.setText(convertToDDMMYYYY(sharedViewModel.ngay.value.toString()))
        binding.edtNgay.setOnClickListener{ dpNgay!!.show()}
        binding.btnThemCongViec.setOnClickListener {
            findNavController().navigate(R.id.action_quanLyCongViecNgayFragment_to_themCongViecFragment) }

        sharedViewModel.ngay.observe(getViewLifecycleOwner()
        ) { sharedViewModel.taiDanhSachCongViec() }
    }

    private fun taiDanhSachCongViec() {
        sharedViewModel.taiDanhSachCongViec()
        lifecycleScope.launch {
            sharedViewModel.danhSachCongViecNgay.collectLatest {
                adapter.differ.submitList(it.data)
            }
        }
    }

    private fun khaiBaoAdapter() {
        adapter = QuanLyCongViecNgayAdapter()
        adapter.setOnItemClickListener(object : QuanLyCongViecNgayAdapter.OnItemClickListener {
            override fun onItemClick(congViecNgay: CongViecNgay?) {

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
        binding.rvCongViec.setAdapter(adapter)
        binding.rvCongViec.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )
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

    private fun khaiBaoDatePicker() {
        val newCalendar = Calendar.getInstance()
        dpNgay = DatePickerDialog(
            requireActivity(),
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                // Cập nhật TextView với ngày được chọn
                binding.edtNgay.setText(
                    SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                    ).format(newDate.time)
                )
                sharedViewModel.setNgay(SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(newDate.time))

            },
            newCalendar[Calendar.YEAR],
            newCalendar[Calendar.MONTH],
            newCalendar[Calendar.DAY_OF_MONTH]
        )

    }

    private fun convertToDDMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
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