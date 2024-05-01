package com.example.kotlin_appquanlycongviec.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentThongKeBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.CongViecNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import ir.mahozad.android.PieChart
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ThongKeFragment : Fragment() {
    private lateinit var binding: FragmentThongKeBinding
    private val congViecNgayViewModel by viewModels<CongViecNgayViewModel>()

    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var doneList: MutableList<CongViecNgay> = mutableListOf()
    private var doList: MutableList<CongViecNgay> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongKeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMonthSpinner()
        setBtnEvent()
        congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang + 1, nam)
        lifecycleScope.launch {
            congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        initPieChart(it.data!!)
                        binding.apply {
                            layoutPieChart.visibility= View.VISIBLE
                            pieChart.visibility= View.VISIBLE
                            tvTrong.visibility= View.GONE
                        }
                    }

                    is Resource.Error -> {

                        binding.apply {
                            layoutPieChart.visibility= View.GONE
                            pieChart.visibility= View.GONE
                            tvTrong.visibility= View.VISIBLE
                        }
                    }

                    else -> {}


                }

            }
        }

    }

    private fun setBtnEvent() {
        binding.apply {
            btnCaCulator.setOnClickListener {
                congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(
                    thang + 1,
                    etYear.text.toString().toInt()
                )
//                Toast.makeText(requireContext(), "Thang hien là"+thang, Toast.LENGTH_SHORT).show()

            }

            btnList.setOnClickListener {
                val b= Bundle()
                b.putInt("thang",thang+1)
                b.putInt("nam",nam)
                it.findNavController().navigate(R.id.action_thongKeFragment_to_congViecChuaLamFragment,b)
            }

            btnCreatePDF.setOnClickListener {
//                Toast.makeText(requireContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                it.findNavController().navigate(R.id.action_thongKeFragment_to_thongKeCongViecPdfFragment)
            }
        }
    }

    private fun initMonthSpinner() {
        val luaChon = arrayOf(
            "Tháng 1",
            "Tháng 2",
            "Tháng 3",
            "Tháng 4",
            "Tháng 5",
            "Tháng 6",
            "Tháng 7",
            "Tháng 8",
            "Tháng 9",
            "Tháng 10",
            "Tháng 11",
            "Tháng 12"
        )
        val adapter = ArrayAdapter(requireActivity(), R.layout.remind_spinner_item, luaChon)
        binding.spMonth.setAdapter(adapter)
        binding.spMonth.setSelection(thang)

        binding.spMonth.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {
                    thang = position 

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }

    private fun initPieChart(cvList: List<CongViecNgay>) {
        doList.clear()
        doneList.clear()
        for (i in 0 until cvList.size) {
            if (cvList.get(i).trangThai == true)
                doneList.add(cvList.get(i))
            else
                doList.add(cvList.get(i))
        }
        binding.pieChart.slices = listOf(
            PieChart.Slice(doneList.size / cvList.size.toFloat(), Color.parseColor("#8BC34A")),
            PieChart.Slice(doList.size / cvList.size.toFloat(), Color.parseColor("#000000")),
        )

        binding.apply {
            tvTotal.setText("Tổng số việc trong tháng : ${cvList.size}")
            tvDone.setText("Số việc đã làm : ${doneList.size}")
            tvNotDone.setText("Số việc chưa làm : ${doList.size}")
        }
    }
}