package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentChiTietCongViecBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentEditCongViecBinding
import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.viewModel.CongViecNgayViewModel
import com.example.kotlin_appquanlycongviec.viewModel.EditCongViecViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChiTietCongViecFragment : Fragment() {
    private var binding: FragmentChiTietCongViecBinding? = null
    private val sharedViewModel: CongViecNgayViewModel by activityViewModels<CongViecNgayViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChiTietCongViecBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            edtTieuDe.setText(sharedViewModel.thongTinCongViec.value?.tieuDe.toString())
            edtNoiDung.setText(sharedViewModel.thongTinCongViec.value?.noiDung.toString())
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            edtNgayBatDau.setText(
                convertToDDMMYYYY(sharedViewModel.thongTinCongViec.value?.ngayBatDau.toString())
            )
            if (sharedViewModel.thongTinCongViec.value?.dungSauNgay != "1900-01-01") {
                edtNgayKetThuc.setText(
                    convertToDDMMYYYY(sharedViewModel.thongTinCongViec.value?.dungSauNgay.toString())
                )
            }
            when(sharedViewModel.thongTinCongViec.value?.chuKi){
                "0"-> tvChuKy.text = "Một lần"
                "1"-> tvChuKy.text = "Hằng ngày"
                "2"-> tvChuKy.text = "Thứ 2 đến thứ 6"
                "3"-> tvChuKy.text = "Hằng tuần"
                "4"-> tvChuKy.text = "Hằng tháng"
                "5"-> tvChuKy.text = "Hằng năm"
            }
            when(sharedViewModel.thongTinCongViec.value?.tinhChat){
                0-> tvTinhChat.text = "Bình thường"
                1-> tvTinhChat.text = "Quan trọng"
                2-> tvTinhChat.text = "Rất quan trọng"

            }




        }
    }







    // Hàm chuyển đổi từ "yyyy-MM-dd" sang "dd-MM-yyyy"
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