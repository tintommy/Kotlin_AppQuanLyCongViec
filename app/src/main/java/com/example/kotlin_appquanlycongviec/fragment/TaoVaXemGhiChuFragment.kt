package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide.init
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.databinding.FragmentTaoVaXemGhiChuBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.GhiChu
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.GhiChuViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TaoVaXemGhiChuFragment : Fragment() {
    private lateinit var binding: FragmentTaoVaXemGhiChuBinding
    private val ghiChuViewModel by viewModels<GhiChuViewModel>()

    private var loai = ""
    private var maGccn = 0
    private lateinit var ghiChu: GhiChu
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0

    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaoVaXemGhiChuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ghiChu= GhiChu(0,"abc","","")
        val bundle = arguments
        loai = bundle?.getString("loai", "").toString()
        if (loai.equals("xem")) {
            if (bundle != null) {
                ghiChu = bundle.getSerializable("ghiChu") as GhiChu
                maGccn = ghiChu.maGCCN
            }
        }
        init()

        lifecycleScope.launch {
            ghiChuViewModel.luuGhiChu.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Đã lưu ghi chú", Toast.LENGTH_SHORT)
                            .show()
                        loai = "xem"
                        ghiChu = it.data!!

                        init()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            ghiChuViewModel.xoaGhiChu.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Đã xoá ghi chú", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun init() {

        if (loai.equals("tao")) {
            binding.btnDelete.visibility = View.GONE

            binding.btnSave.setOnClickListener {
                val ghiChu = GhiChu(
                    0,
                    dinhDangNgayAPI(ngay, thang, nam),
                    binding.etNoiDung.text.toString(),
                    binding.etTieuDe.text.toString()
                )
                ghiChuViewModel.luuGhiChu(ghiChu)
            }

        } else {
            binding.btnDelete.visibility = View.VISIBLE
            binding.etTieuDe.setText(ghiChu.tieuDe)
            binding.etNoiDung.setText(ghiChu.noiDung)

            binding.btnSave.setOnClickListener {
                val ghiChuTemp = GhiChu(
                    ghiChu.maGCCN,
                    dinhDangNgayAPI(ngay, thang, nam),
                    binding.etNoiDung.text.toString(),
                    binding.etTieuDe.text.toString()
                )
                ghiChuViewModel.luuGhiChu(ghiChuTemp)
            }

            binding.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Bạn chắc chắn xoá ghi chú này ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton(
                    "Xoá"
                ) { dialog, which ->
                    ghiChuViewModel.xoaGhiChu(ghiChu.maGCCN)
                }
                builder.setNegativeButton(
                    "Huỷ"
                ) { dialog, which ->

                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }


        }
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
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