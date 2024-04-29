package com.example.kotlin_appquanlycongviec.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.NgayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.XemcongviecngayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.XemtheongayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class XemCongViecNgayAdapter : RecyclerView.Adapter<XemCongViecNgayAdapter.XemCongViecNgayViewHolder>() {
    val differ: AsyncListDiffer<CongViecNgay> =  AsyncListDiffer(this, DiffCallback)



    interface OnItemClickListener {
        fun onImageButtonClick(congViecNgay: CongViecNgay?)
        fun onDeleteButtonClick(congViecNgay: CongViecNgay?)
        fun onStopTrackingTouch(congViecNgay: CongViecNgay?, percent: Int)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }

    inner class XemCongViecNgayViewHolder(private val binding: XemtheongayItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(congViecNgay: CongViecNgay) {
            binding.cbCongViec.setChecked(congViecNgay.trangThai)
            binding.tvTieuDeCongViec.setText(congViecNgay.congViec.tieuDe)
            when (congViecNgay.congViec.tinhChat) {
                0 -> {
                    binding.tvTinhChatCongViec.setText("Bình Thường")
                    binding.tvTinhChatCongViec.setTextColor(Color.parseColor("#80BCBD"))
                }

                1 -> {
                    binding.tvTinhChatCongViec.setText("Quan trọng")
                    binding.tvTinhChatCongViec.setTextColor(Color.parseColor("#2400FF"))
                }

                2 -> {
                    binding.tvTinhChatCongViec.setText("Rất quan trọng")
                    binding.tvTinhChatCongViec.setTextColor(Color.RED)
                }
            }
            binding.tvPercent.text = congViecNgay.phanTramHoanThanh.toString() + "%"
            binding.sbPercent.progress = congViecNgay.phanTramHoanThanh


            binding.tvNoiDung.text = congViecNgay.congViec.noiDung

            binding.btnAnhCongViec.setOnClickListener(View.OnClickListener {
                itemClick!!.onImageButtonClick(
                    congViecNgay
                )
            })
            binding.btnXoaCongViec.setOnClickListener {
                itemClick!!.onDeleteButtonClick(
                    congViecNgay
                )
            }

            binding.sbPercent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvPercent.text = p1.toString() + "%"

//                    if (p1 == 100) {
//                        binding.cbCongViec.setChecked(true)
//                    } else
//                        binding.cbCongViec.setChecked(false)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (p0 != null) {
                        itemClick?.onStopTrackingTouch(congViecNgay, p0.progress)
                    }
                }
            })



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XemCongViecNgayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: XemtheongayItemLayoutBinding =
            XemtheongayItemLayoutBinding.inflate(inflater, parent, false)
        return XemCongViecNgayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: XemCongViecNgayViewHolder, position: Int) {
        val congViecNgay: CongViecNgay = differ.currentList[position]
        holder.bind(congViecNgay)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CongViecNgay>() {
        override fun areItemsTheSame(oldItem: CongViecNgay, newItem: CongViecNgay): Boolean {
            return oldItem.maCvNgay == newItem.maCvNgay
        }

        override fun areContentsTheSame(oldItem: CongViecNgay, newItem: CongViecNgay): Boolean {
            return oldItem == newItem
        }
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
}