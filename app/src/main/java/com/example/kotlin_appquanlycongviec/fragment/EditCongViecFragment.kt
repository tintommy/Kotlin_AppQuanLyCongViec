package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentEditCongViecBinding
import com.example.kotlin_appquanlycongviec.request.CongViecRequest
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.EditCongViecViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditCongViecFragment : Fragment() {

    private lateinit var binding: FragmentEditCongViecBinding
    private val viewModel: EditCongViecViewModel by viewModels<EditCongViecViewModel>()
    private var dpNgayBatDau: DatePickerDialog? = null
    private var dpNgayKetThuc: DatePickerDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCongViecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        khaiBaoSpiner()
        khaiBaoDatePicker()


        binding.apply {
            binding.edtNgayBatDau.setText(
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            )
            btnLuu.setOnClickListener { luuCongViec() }
            edtNgayBatDau.setOnClickListener { dpNgayBatDau?.show() }
            edtNgayKetThuc.setOnClickListener { dpNgayKetThuc?.show() }
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.luuCongViec.collectLatest {
                when(it) {
                    is Resource.Success ->  findNavController().navigate(R.id.action_editCongViecFragment_to_congViecFragment)
                    is Resource.Error -> Toast.makeText(requireContext(), "Kiểm tra lại kết nối mạng, lưu thất bại", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    private fun khaiBaoSpiner() {
        val chuKy = arrayOf(
            "Một lần",
            "Hằng ngày",
            "Thứ 2 đến thứ 6",
            "Hằng tuần",
            "Hàng tháng",
            "Hằng năm"
        )
        val tinhChat = arrayOf("Bình thường", "Quan trọng", "Rất quan trọng")
        val adapterChuKy =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, chuKy)
        val adapterTinhChat =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, tinhChat)
        binding.spnChuKy.adapter = adapterChuKy
        binding.spnTinhChat.adapter = adapterTinhChat
    }

    private fun khaiBaoDatePicker() {
        val newCalendar = Calendar.getInstance()
//        dpNgayBatDau = DatePickerDialog(
//            requireActivity(),
//            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
//                val newDate = Calendar.getInstance()
//                newDate[year, monthOfYear] = dayOfMonth
//                // Cập nhật TextView với ngày được chọn
//                binding.edtNgayBatDau.setText(
//                    SimpleDateFormat(
//                        "dd-MM-yyyy",
//                        Locale.getDefault()
//                    ).format(newDate.time)
//                )
//            },
//            newCalendar[Calendar.YEAR],
//            newCalendar[Calendar.MONTH],
//            newCalendar[Calendar.DAY_OF_MONTH]
//        )
        dpNgayKetThuc = DatePickerDialog(
            requireActivity(),
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                // Cập nhật TextView với ngày được chọn
                binding.edtNgayKetThuc.setText(
                    SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                    ).format(newDate.time)
                )
            },
            newCalendar[Calendar.YEAR],
            newCalendar[Calendar.MONTH],
            newCalendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun luuCongViec() {
        if (!validation()) {
            return
        }
        var congViec = CongViecRequest()
        congViec.tieuDe = binding.edtTieuDe.text.toString()
        congViec.noiDung = binding.edtNoiDung.text.toString()
        congViec.ngayBatDau = convertToYYYYMMDD(binding.edtNgayBatDau.text.toString())
        Log.e("bug", congViec.ngayBatDau)
        congViec.tinhChat = binding.spnTinhChat.selectedItemId.toInt()
        congViec.chuKi = binding.spnChuKy.selectedItemId.toInt().toString()
        congViec.dungSauNgay = convertToYYYYMMDD(binding.edtNgayKetThuc.text.toString())

        viewModel.saveCongViec(congViec)

    }

    private fun validation(): Boolean {
        if (binding.edtTieuDe.text.isNullOrEmpty()) {
            buildDialog("Tiêu đề không được để trống")
            return false
        }
        if (binding.edtNoiDung.text.isNullOrEmpty()) {
            buildDialog("Nội dung không được để trống")
            return false
        }
        if (binding.edtNgayBatDau.text.isNullOrEmpty()) {
            buildDialog("Ngày bắt đầu không được để trống")
            return false
        }
        if (binding.spnChuKy.selectedItemId.toInt() != 0 && binding.edtNgayKetThuc.text.isNullOrEmpty()) {
            buildDialog("Ngày kết thúc không được để trống")
            return false
        }
        if (binding.spnChuKy.selectedItemId.toInt() != 0 && !binding.edtNgayKetThuc.text.isNullOrEmpty()){
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            try {
                val startDate = dateFormat.parse(binding.edtNgayBatDau.text.toString())
                val endDate = dateFormat.parse(binding.edtNgayKetThuc.text.toString())
                if (!startDate!!.before(endDate)) {
                    buildDialog("Ngày kết thúc phải lớn hơn này bắt đầu")
                    return false
                }
            } catch (e: Exception) {
                return false // Trả về false nếu có lỗi khi parse chuỗi ngày
            }
        }


        return true;


    }

    private fun buildDialog(message: String) {

        // Tạo và hiển thị AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thông báo")
        builder.setMessage(message)
        builder.setPositiveButton(
            "OK"
        ) { dialog, id -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
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