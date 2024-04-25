package com.example.kotlin_appquanlycongviec.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentChiTietSuKienBinding
import com.example.kotlin_appquanlycongviec.databinding.FragmentThemSuKienBinding
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.SuKienViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ChiTietSuKienFragment : Fragment() {

private lateinit var binding: FragmentChiTietSuKienBinding

    private val suKienViewModel by viewModels<SuKienViewModel>()

    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var gio = 0
    private var phut = 0
    private var ngayApi = ""
    private var gioApi = ""
    private var nhacTruoc = 0

    private lateinit var suKien: SuKien
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChiTietSuKienBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        setBtnEvent()
        onBackPressed()

        val bundle= arguments
        if (bundle != null) {
            suKien= bundle.getSerializable("suKien") as SuKien
        }

        binding.apply {
            etEventName.setText(suKien.tenSuKien)
            etTime.setText(suKien.gio)
            val parts = suKien.ngay.split("-")
            val nam = parts[0].toInt()
            val thang = parts[1].toInt()-1
            val ngay = parts[2].toInt()
            etDate.setText(dinhDangNgay(ngay,thang,nam))
            etDescrip.setText(suKien.moTa)
            spRemind.setSelection(suKien.nhacTruoc-1)

            gioApi=suKien.gio
            ngayApi=dinhDangNgayAPI(ngay,thang,nam)
            nhacTruoc= suKien.nhacTruoc
        }


        lifecycleScope.launch {
            suKienViewModel.updateEvent.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Đã sửa sự kiện thành công",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()

                    }

                    else -> {}
                }
            }
        }
    }

    private fun setBtnEvent() {
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.etDate.setOnClickListener {
            openLichDialog()
        }
        binding.etTime.setOnClickListener {
            openTimePickerDialog()
        }

        binding.btnSave.setOnClickListener {

            var eventUpdate = SuKien(
                gioApi,
                suKien.maSK,
                binding.etDescrip.text.toString().trim(),
                ngayApi,
                suKien.ngayNhac,
                nhacTruoc,
                binding.etEventName.text.toString().trim()
            )
            suKienViewModel.updateEvent(
                eventUpdate
            )
        }
    }

    private fun onBackPressed() {
        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (v != null) {
//                        (activity as MainActivity).replaceFragment(SuKienFragment())
                        findNavController().navigateUp()

                    }
                }
                return true
            }
        })

    }

    private fun initSpinner() {
        val luaChon = arrayOf(
            "1 ngày",
            "2 ngày",
            "3 ngày",
            "4 ngày",
            "5 ngày"
        )
        val adapter = ArrayAdapter(requireActivity(), R.layout.remind_spinner_item, luaChon)
        binding.spRemind.setAdapter(adapter)


        binding.spRemind.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {

                    nhacTruoc = position + 1
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }

    private fun openLichDialog() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.etDate.setText(dinhDangNgay(day, month, year))
                ngay = day
                thang = month
                nam = year
                ngayApi = dinhDangNgayAPI(ngay, thang, nam)
            }, nam, thang, ngay
        )
        dialog.show()
    }

    private fun openTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                gio = selectedHour
                phut = selectedMinute
                binding.etTime.setText(time)
                gioApi = time
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun dinhDangNgay(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        temp += "/"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "/"
        temp += nam
        return temp
    }


    private fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
        var temp = ""
        temp += nam
        temp += "-"
        temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
        temp += "-"
        temp += if (ngay < 10) "0$ngay" else ngay.toString()
        return temp
    }

}