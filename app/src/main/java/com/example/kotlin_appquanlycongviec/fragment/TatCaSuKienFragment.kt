package com.example.kotlin_appquanlycongviec.fragment

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_appquanlycongviec.R
import com.example.kotlin_appquanlycongviec.adapter.SuKienAdapter
import com.example.kotlin_appquanlycongviec.databinding.FragmentTatCaSuKienBinding
import com.example.kotlin_appquanlycongviec.model.SuKien
import com.example.kotlin_appquanlycongviec.util.Resource
import com.example.kotlin_appquanlycongviec.viewModel.SuKienViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TatCaSuKienFragment : Fragment() {

    private lateinit var binding: FragmentTatCaSuKienBinding
    private val suKienViewModel by viewModels<SuKienViewModel>()
    private var eventList: MutableList<SuKien> = mutableListOf()
    private lateinit var suKienAdapter: SuKienAdapter
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var filterEventList: MutableList<SuKien> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTatCaSuKienBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initSpinner()
        initMonthSpinner()
        setBtnEvent()
        suKienViewModel.getAllEvent()
        lifecycleScope.launch {
            suKienViewModel.allEvent.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        eventList.clear()
                        filterEventList.clear()
                        suKienAdapter.differ.submitList(it.data)
                        eventList.addAll(it.data!!)
                        filterEventList.addAll(it.data)
                        binding.tvNotEvent.visibility = View.GONE
                        binding.rvEvent.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {


                        if (it.message.equals("404")) {
                            binding.tvNotEvent.visibility = View.VISIBLE
                            binding.rvEvent.visibility = View.GONE
                        }
                    }

                    else -> {}


                }
            }
        }
    }

    private fun setBtnEvent() {
        binding.btnBack.setOnClickListener { it.findNavController().navigateUp() }

    }


    private fun initAdapter() {
        binding.apply {
            suKienAdapter = SuKienAdapter()
            rvEvent.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvEvent.adapter = suKienAdapter
            suKienAdapter.setOnItemClickListener(object : SuKienAdapter.OnItemClickListener {
                override fun onItemClick(suKien: SuKien) {
                    val b = Bundle()
                    b.putSerializable("suKien", suKien)
                    findNavController().navigate(
                        R.id.action_tatCaSuKienFragment_to_chiTietSuKienFragment,
                        b
                    )
                }

                override fun onDeleteBtnClick(maSuKien: Int, position: Int) {

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Xác nhận xoá ?")
                    builder.setCancelable(false)
                    builder.setPositiveButton("Xoá") { dialog, which ->
                        suKienViewModel.deleteEvent(maSuKien, requireContext())
                        eventList.removeAt(position)
                        suKienAdapter.differ.submitList(eventList)
                        suKienAdapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Đã xoá sự kiện", Toast.LENGTH_SHORT)
                            .show()
                    }
                    builder.setNegativeButton("Huỷ") { dialog, which -> dialog.cancel() }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
            })

        }
    }

    private fun initSpinner() {
        val luaChon = arrayOf(
            "Mới nhất",
            "Cũ nhất"
        )
        val adapter = ArrayAdapter(requireActivity(), R.layout.remind_spinner_item, luaChon)
        binding.spOrder.setAdapter(adapter)


        binding.spOrder.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {

                    if(position==0){
                        val sortedList = filterEventList.sortedWith(compareBy(SuKien::ngay, SuKien::gio)).reversed().toMutableList()
                        suKienAdapter.differ.submitList(sortedList)
                        suKienAdapter.notifyDataSetChanged()
                    }
                    else{
                        val sortedList = filterEventList.sortedWith(compareBy(SuKien::ngay, SuKien::gio)).toMutableList()
                        suKienAdapter.differ.submitList(sortedList)
                        suKienAdapter.notifyDataSetChanged()
                    }



                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }

    private fun initMonthSpinner() {
        val luaChon = arrayOf(
            "Tất cả",
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


        binding.spMonth.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                l: Long
            ) {
                if (view != null) {
                    when (position) {
                        0 -> {
                            suKienAdapter.differ.submitList(mutableListOf())
                            suKienAdapter.notifyDataSetChanged()

                            suKienAdapter.differ.submitList(eventList)
                            suKienAdapter.notifyDataSetChanged()
                            filterEventList.clear()
                            filterEventList.addAll(eventList)
                        }

                        else -> {
                            filterMonthEvent(position)

                        }
                    }

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }


    private fun filterMonthEvent(month: Int) {
        filterEventList.clear()
        var monthTemp = 0
        for (i in 0 until eventList.size) {
            monthTemp = eventList.get(i).ngay.split("-")[1].toInt()

            if (monthTemp == month) {
                filterEventList.add(eventList.get(i))

            }
        }
        suKienAdapter.differ.submitList(filterEventList)
        suKienAdapter.notifyDataSetChanged()
        if (filterEventList.size == 0) {
            binding.tvNotEvent.visibility = View.VISIBLE
        } else {
            binding.tvNotEvent.visibility = View.GONE
        }
    }
}