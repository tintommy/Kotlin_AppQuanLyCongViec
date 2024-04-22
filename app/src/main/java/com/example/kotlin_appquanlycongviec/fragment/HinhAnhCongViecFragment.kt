package com.example.kotlin_appquanlycongviec.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.HinhAnhAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentHinhAnhCongViecBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.HinhAnh
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.util.hideBottomNavigation
import com.example.kotlin_appquanlycongviec.viewModel.HinhAnhViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HinhAnhCongViecFragment : Fragment() {

    private lateinit var binding: FragmentHinhAnhCongViecBinding
    private lateinit var hinhAnhAdapter: HinhAnhAdapter
    private val hinhAnhViewModel by viewModels<HinhAnhViewModel>()
    private var maCvNgay: Int = 0
    private lateinit var anhDuocChon: MutableList<Uri>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigation()

        binding = FragmentHinhAnhCongViecBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        binding.rvHinhAnh.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnQuayLai.setOnClickListener {
            it.findNavController().navigateUp()
        }

        taiDanhSachHinhAnh()
        chonAnh()
    }

    private fun taiDanhSachHinhAnh() {
        lifecycleScope.launch {
            val bundle = arguments
            maCvNgay = bundle?.getInt("maCvNgay")!!
            hinhAnhViewModel.taiDanhSachHinhAnh(maCvNgay)
            hinhAnhViewModel.hinhAnhList.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvTrong.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvTrong.visibility = View.GONE
                        khaiBaoAdapter(it.data!!)
                        binding.rvHinhAnh.adapter = hinhAnhAdapter
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        if (it.message == "error") {
                            Toast.makeText(
                                requireContext(),
                                "Kiểm Tra Kết Nối Mạng",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        else if (it.message == "404") {
                            binding.progressBar.visibility = View.GONE
                            binding.tvTrong.visibility = View.VISIBLE
                        }
                        else
                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun khaiBaoAdapter(hinhAnhs: MutableList<HinhAnh>) {
        hinhAnhAdapter = HinhAnhAdapter(hinhAnhs)
        hinhAnhAdapter.setOnItemClickListener(object : HinhAnhAdapter.OnItemClickListener {
            override fun onDeleteButtonClick(hinhAnh: HinhAnh, position: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Xoá ảnh này ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton("Xoá") { dialog, which ->
                    hinhAnhViewModel.xoaAnh(hinhAnh.maHinh, hinhAnh.link)
                    hinhAnhAdapter.xoaHinhAnhTrongDanhSach(position)
                    if (hinhAnhs.isEmpty()) binding.tvTrong.visibility = View.VISIBLE
                }
                builder.setNegativeButton("Huỷ") { dialog, which -> dialog.cancel() }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        })
    }

    private fun chonAnh() {
        val selectImageActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    anhDuocChon = ArrayList()
                    val intent = result.data

                    // Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = intent.clipData!!.getItemAt(i).uri
                            if (imageUri != null) {
                                anhDuocChon.add(imageUri)
                            }
                        }
                    } else if (intent?.data != null) {
                        val imageUri = intent.data
                        if (imageUri != null) {
                            anhDuocChon.add(imageUri)
                        }
                    }
                    hinhAnhViewModel.luuDanhSachAnh(anhDuocChon, requireActivity(), maCvNgay)
                }
            }

        binding.btnThemAnh.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }
    }

    private fun onBackPressed() {
        requireView().setFocusableInTouchMode(true)
        requireView().requestFocus()
        requireView().setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    findNavController().navigateUp()
                }
                return true
            }
        })
    }
}

