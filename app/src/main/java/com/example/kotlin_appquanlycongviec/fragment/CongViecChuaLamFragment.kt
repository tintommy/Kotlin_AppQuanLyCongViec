package com.example.kotlin_appquanlycongviec.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.DanhSachCongViecChuaLamAdapter
import com.example.kotlin_appquanlycongviec.adapter.DanhSachCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentCongViecChuaLamBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.util.showBottomNavigation
import com.example.kotlin_appquanlycongviec.viewModel.CongViecNgayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CongViecChuaLamFragment : Fragment() {

    private lateinit var binding: FragmentCongViecChuaLamBinding
    private val congViecNgayViewModel by viewModels<CongViecNgayViewModel>()
    private lateinit var congViecChuaLamAdapter: DanhSachCongViecChuaLamAdapter
    private var thang = 0
    private var nam = 0
    private var cvList: MutableList<CongViecNgay> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCongViecChuaLamBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()
        initAdapter()
        val bundle = arguments
        if (bundle != null) {
            thang = bundle.getInt("thang", 0)
            nam = bundle.getInt("nam", 0)

        }

        congViecNgayViewModel.taiDanhSachCongViecNgayTheoThangNam(thang, nam)
        lifecycleScope.launch {
            lifecycleScope.launch {
                congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                    when (it) {
                        is Resource.Loading -> {

                        }

                        is Resource.Success -> {
                            congViecChuaLamAdapter.differ.submitList(filterTask(it.data!!))
                        }

                        is Resource.Error -> {


                        }

                        else -> {}


                    }

                }
            }

        }
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    private fun filterTask(list: List<CongViecNgay>): MutableList<CongViecNgay>? {
        cvList.clear()
        for (i in 0 until list.size) {
            if (list.get(i).trangThai == false)
                cvList.add(list.get(i))
        }
        return cvList.sortedWith(compareBy(CongViecNgay::ngayLam)).toMutableList()
    }

    private fun initAdapter() {
        binding.apply {
            congViecChuaLamAdapter = DanhSachCongViecChuaLamAdapter()
            rvList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvList.adapter = congViecChuaLamAdapter

            congViecChuaLamAdapter.setOnItemClickListener(object :
                DanhSachCongViecChuaLamAdapter.OnItemClickListener {
                override fun onItemClick(congViecNgay: CongViecNgay?) {}
                override fun onCheckBoxClick(maCongViecNgay: Int) {
                    congViecNgayViewModel.capNhatTrangThaiCongViecNgay(
                        maCongViecNgay,
                        dinhDangNgayAPI(1, thang, nam)
                    )

                }

                override fun onImageButtonClick(congViecNgay: CongViecNgay?) {
                    val bundle = Bundle()
                    bundle.putInt("maCvNgay", congViecNgay!!.maCvNgay)
                    view?.findNavController()?.navigate(
                        R.id.action_congViecChuaLamFragment_to_hinhAnhCongViecFragment,
                        bundle
                    )
                }
            })
        }
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