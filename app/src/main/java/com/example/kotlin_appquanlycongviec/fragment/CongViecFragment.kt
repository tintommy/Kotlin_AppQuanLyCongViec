package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.DanhSachCongViecNgayAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentCongViecBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.util.showBottomNavigation
import com.example.kotlin_appquanlycongviec.viewModel.CongViecNgayViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class CongViecFragment : Fragment() {

    private val congViecNgayViewModel by activityViewModels<CongViecNgayViewModel>()
    private lateinit var danhSachCongViecNgayAdapter: DanhSachCongViecNgayAdapter

    private lateinit var binding: FragmentCongViecBinding

    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0

    private var ngay = calendar[Calendar.DAY_OF_MONTH]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCongViecBinding.inflate(layoutInflater)
        showBottomNavigation()
        return binding.getRoot()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        khaiBaoSpinner()
        khaiBaoAdapter()

        taiDanhSachCongViecNgay(dinhDangNgayAPI(ngay, thang, nam))
        binding.tvNgay.setText(dinhDangNgay(ngay, thang, nam))
        binding.btnLich.setOnClickListener(View.OnClickListener { openLichDialog() })
        binding.btnThemCongViec.setOnClickListener {
            it.findNavController().navigate(R.id.action_congViecFragment_to_editCongViecFragment)
        }
        binding.btnQuanLi.setOnClickListener {
            it.findNavController().navigate(R.id.action_congViecFragment_to_quanLyNgayFragment)
        }

        congViecNgayViewModel.soViecCanLam.observe(
            getViewLifecycleOwner(),
            object : Observer<String?> {
                override fun onChanged(value: String?) {
                    binding.tvSoViecCanLam.setText(value)
                }
            })
        congViecNgayViewModel.phanTramHoanThanh.observe(
            getViewLifecycleOwner(),
            object : Observer<String?> {
                override fun onChanged(value: String?) {
                    binding.tvPhanTram.setText(value)
                }
            })


    }

    private fun khaiBaoSpinner() {
        val luaChon = arrayOf(
            "Mặc định",
            "Trạng thái tăng",
            "Trạng thái giảm",
            "Tính chất tăng",
            "Tính chất giảm"
        )
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, luaChon)
        binding.spSapXep.setAdapter(adapter)


        binding.spSapXep.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {

                    if (position == 0) {
                        congViecNgayViewModel.sapXepCvNgay(0)
                    } else if (position == 1) {
                        congViecNgayViewModel.sapXepCvNgay(1)
                    } else if (position == 2) {
                        congViecNgayViewModel.sapXepCvNgay(2)
                    } else if (position == 3) {
                        congViecNgayViewModel.sapXepCvNgay(3)
                    } else if (position == 4) {
                        congViecNgayViewModel.sapXepCvNgay(4)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                congViecNgayViewModel.sapXepCvNgay(0)
            }
        })
    }

    private fun taiDanhSachCongViecNgay(ngay: String) {
        congViecNgayViewModel.taiDanhSachCongViecNgay(ngay)
        lifecycleScope.launch {

            congViecNgayViewModel.danhSachCongViecNgay.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.setVisibility(View.VISIBLE)
                        binding.tvTrong.setVisibility(View.GONE)
                        binding.spSapXep.setVisibility(View.GONE)
                    }

                    is Resource.Success -> {
                        binding.progressBar.setVisibility(View.GONE)
                        binding.tvTrong.setVisibility(View.GONE)
                        binding.spSapXep.setVisibility(View.VISIBLE)

                        danhSachCongViecNgayAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {

                        binding.progressBar.setVisibility(View.GONE)
                        binding.spSapXep.setVisibility(View.GONE)
                        if (it.message.equals("error")) {
                            Toast.makeText(
                                requireContext(),
                                "Kiểm Tra Kết Nối Mạng",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        if (it.message.equals("404")) {
                            danhSachCongViecNgayAdapter.differ.submitList(ArrayList())
                            binding.progressBar.setVisibility(View.GONE)
                            binding.tvTrong.setVisibility(View.VISIBLE)
                        }
                    }

                    else -> {}


                }

            }
        }
    }

    private fun khaiBaoAdapter() {
        danhSachCongViecNgayAdapter = DanhSachCongViecNgayAdapter()
        danhSachCongViecNgayAdapter.setOnItemClickListener(object :
            DanhSachCongViecNgayAdapter.OnItemClickListener {
            override fun onItemClick(congViecNgay: CongViecNgay?) {
                if (congViecNgay != null) {
                    congViecNgayViewModel.setThongTinCongViec(congViecNgay.congViec)
                }
                findNavController().navigate(R.id.action_congViecFragment_to_chiTietCongViecFragment)

            }

            override fun onCheckBoxClick(congViecNgay: CongViecNgay, hoanThanh: Boolean) {
                if (hoanThanh == true) {
                    congViecNgay.phanTramHoanThanh = 100
                    congViecNgay.trangThai = true
                    congViecNgayViewModel.capNhatCongViecNgay(congViecNgay,dinhDangNgayAPI(ngay, thang, nam))
                } else {
                    congViecNgay.phanTramHoanThanh = 0
                    congViecNgay.trangThai = false
                    congViecNgayViewModel.capNhatCongViecNgay(congViecNgay,dinhDangNgayAPI(ngay, thang, nam))
                }
//                congViecNgayViewModel.capNhatTrangThaiCongViecNgay(
//                    congViecNgay.maCvNgay,
//                    dinhDangNgayAPI(ngay, thang, nam)
//                )
            }

            override fun onDeleteBtnClick(maCongViecNgay: Int, position: Int) {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Bạn chắc chắn xoá ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton(
                    "Xoá"
                ) { dialog, which ->

                    congViecNgayViewModel.xoaCongViecNgay(
                        maCongViecNgay,
                        dinhDangNgayAPI(ngay, thang, nam)

                    )

                    var list = danhSachCongViecNgayAdapter.differ.currentList.toMutableList()
                    list.removeAt(position)
                    danhSachCongViecNgayAdapter.differ.submitList(list)
                    danhSachCongViecNgayAdapter.notifyDataSetChanged()
                    Snackbar.make(view!!, "Đã xoá công việc", Snackbar.LENGTH_LONG).show()
                }
                builder.setNegativeButton(
                    "Huỷ"
                ) { dialog, which ->
                    danhSachCongViecNgayAdapter.notifyDataSetChanged()
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }

            override fun onImageButtonClick(congViecNgay: CongViecNgay?) {
                val bundle = Bundle()
                bundle.putInt("maCvNgay", congViecNgay!!.maCvNgay)
                view?.findNavController()?.navigate(
                    R.id.action_congViecFragment_to_hinhAnhCongViecFragment,
                    bundle
                )
            }

            override fun onStopTrackingTouch(congViecNgay: CongViecNgay?, percent: Int) {
                congViecNgay!!.phanTramHoanThanh = percent
                if (percent == 100)
                    congViecNgay.trangThai = true
                else congViecNgay.trangThai = false

                congViecNgayViewModel.capNhatCongViecNgay(congViecNgay!!,dinhDangNgayAPI(ngay, thang, nam))
            }
        })


        binding.rvCongViec.setAdapter(danhSachCongViecNgayAdapter)
        binding.rvCongViec.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )
    }

    private fun openLichDialog() {
        val dialog = DatePickerDialog(
            requireContext(),
            { datePicker, year, month, day ->
                binding.tvNgay.setText(dinhDangNgay(day, month, year))
                taiDanhSachCongViecNgay(dinhDangNgayAPI(day, month, year))
                ngay = day
                thang = month
                nam = year
            }, nam, thang, ngay
        )
        dialog.show()
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