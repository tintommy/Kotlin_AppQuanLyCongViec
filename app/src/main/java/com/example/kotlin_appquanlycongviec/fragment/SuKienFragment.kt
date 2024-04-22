package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.SuKienAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentSuKienBinding
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.SuKienViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class SuKienFragment : Fragment() {

    private lateinit var binding: FragmentSuKienBinding
    private val suKienViewModel by viewModels<SuKienViewModel>()
    private lateinit var todayEventAdapter: SuKienAdapter
    private lateinit var nearlyEventAdapter: SuKienAdapter
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var nam2 = 0
    private var thang2 = 0
    private var ngay2 = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuKienBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNextDate()
        initAdapter()
        binding.tvDate.text = dinhDangNgay(ngay, thang, nam)
        Log.e("MyTag", dinhDangNgayAPI(ngay2, thang2, nam2))
        suKienViewModel.getTodayEvent(dinhDangNgayAPI(ngay, thang, nam))
        suKienViewModel.getNearlyEvent(dinhDangNgayAPI(ngay2, thang2, nam2))

        lifecycleScope.launch {
            suKienViewModel.todayEvent.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        todayEventAdapter.differ.submitList(it.data)
                        binding.tvNotEventToday.visibility = View.GONE
                        binding.rvEventToday.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {


                        if (it.message.equals("404")) {
                            binding.tvNotEventToday.visibility = View.VISIBLE
                            binding.rvEventToday.visibility = View.GONE
                        }
                    }

                    else -> {}


                }
            }
        }

        lifecycleScope.launch {
            suKienViewModel.nearlyEvent.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Log.e("MyTag", it.data!!.size.toString())
                        nearlyEventAdapter.differ.submitList(it.data)
                        binding.tvNotNearlyEvent.visibility = View.GONE
                        binding.rvEventNear.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {


                        if (it.message.equals("404")) {
                            binding.tvNotNearlyEvent.visibility = View.VISIBLE
                            binding.rvEventNear.visibility = View.GONE
                        }
                    }

                    else -> {}


                }
            }
        }

    }

    private fun initAdapter() {
        binding.apply {
            todayEventAdapter = SuKienAdapter()
            rvEventToday.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvEventToday.adapter = todayEventAdapter


            nearlyEventAdapter = SuKienAdapter()
            rvEventNear.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvEventNear.adapter = nearlyEventAdapter
        }
    }

    private fun getNextDate() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        ngay2 = calendar.get(Calendar.DAY_OF_MONTH)
        thang2 = calendar.get(Calendar.MONTH)
        nam2 = calendar.get(Calendar.YEAR)
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