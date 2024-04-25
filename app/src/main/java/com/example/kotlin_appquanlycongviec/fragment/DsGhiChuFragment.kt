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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.GhiChuAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentDsGhiChuBinding
import com.example.kotlin_appquanlycongviec.model.GhiChu
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.GhiChuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DsGhiChuFragment : Fragment() {

    private lateinit var binding: FragmentDsGhiChuBinding
    private lateinit var ghiChuAdapter: GhiChuAdapter
    private val ghiChuViewModel by viewModels<GhiChuViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDsGhiChuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        ghiChuViewModel.taiDsGhiChu()
        lifecycleScope.launch {
            ghiChuViewModel.ghiChuList.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        ghiChuAdapter.differ.submitList(it.data)
                        binding.tvTrong.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        if (!it.message.equals("404")) {
                            Toast.makeText(
                                requireContext(),
                                "Lỗi khi tải danh sách",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.tvTrong.visibility = View.VISIBLE
                    }

                    else -> {}
                }
            }
        }

        binding.btnThemGhiChu.setOnClickListener {
            val b = Bundle()
            b.putString("loai","tao")
            findNavController().navigate(R.id.action_dsGhiChuFragment_to_taoVaXemGhiChuFragment,b)
        }
    }

    private fun initAdapter() {
        binding.apply {
            ghiChuAdapter = GhiChuAdapter()
            rvList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvList.adapter = ghiChuAdapter

            ghiChuAdapter.setOnItemClickListener(object : GhiChuAdapter.OnItemClickListener{
                override fun onItemClick(ghiChu: GhiChu) {
                   val b = Bundle()
                    b.putSerializable("ghiChu",ghiChu)
                    b.putString("loai","xem")
                    findNavController().navigate(R.id.action_dsGhiChuFragment_to_taoVaXemGhiChuFragment,b)
                }

                override fun onDeleteBtnClick(maGhiChu: Int, position: Int) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}