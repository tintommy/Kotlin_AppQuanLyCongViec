package com.example.kotlin_appquanlycongviec.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.CongviecItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.CongviecngayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.NgayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay

class QuanLyCongViecNgayAdapter :RecyclerView.Adapter<QuanLyCongViecNgayAdapter.QuanLyCongViecNgayViewHolder>() {
    val differ: AsyncListDiffer<CongViecNgay> =  AsyncListDiffer(this, DiffCallback)


    interface OnItemClickListener {
        fun onItemClick(congViecNgay: CongViecNgay?)
        fun onDeleteButtonClick(congViecNgay: CongViecNgay?)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }
    inner class QuanLyCongViecNgayViewHolder(private val binding: CongviecngayItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(congViecNgay: CongViecNgay) {
            binding.tvTieuDe.text = congViecNgay.congViec.tieuDe
            when (congViecNgay.congViec.tinhChat) {
                0 -> {
                    binding.tvTinhChat.setText("Bình Thường")
                    binding.tvTinhChat.setTextColor(Color.parseColor("#80BCBD"))
                }

                1 -> {
                    binding.tvTinhChat.setText("Quan trọng")
                    binding.tvTinhChat.setTextColor(Color.parseColor("#2400FF"))
                }

                2 -> {
                    binding.tvTinhChat.setText("Rất quan trọng")
                    binding.tvTinhChat.setTextColor(Color.RED)
                }
            }

            binding.btnXoaCongViec.setOnClickListener { itemClick?.onDeleteButtonClick(congViecNgay) }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuanLyCongViecNgayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CongviecngayItemLayoutBinding =
            CongviecngayItemLayoutBinding.inflate(inflater, parent, false)
        return QuanLyCongViecNgayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: QuanLyCongViecNgayViewHolder, position: Int) {
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


}