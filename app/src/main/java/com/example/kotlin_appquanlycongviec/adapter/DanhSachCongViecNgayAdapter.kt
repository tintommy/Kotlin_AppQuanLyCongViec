package com.example.kotlin_appquanlycongviec.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.CongviecItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay


class DanhSachCongViecNgayAdapter :
    RecyclerView.Adapter<DanhSachCongViecNgayAdapter.DanhSachCongViecNgayViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(congViecNgay: CongViecNgay?)
        fun onCheckBoxClick(congViecNgay: CongViecNgay, hoanThanh: Boolean)
        fun onDeleteBtnClick(maCongViecNgay: Int, position: Int)
        fun onImageButtonClick(congViecNgay: CongViecNgay?)
        fun onStopTrackingTouch(congViecNgay: CongViecNgay?, percent: Int)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }

    private val callback =
        object : DiffUtil.ItemCallback<CongViecNgay>() {
            override fun areItemsTheSame(oldItem: CongViecNgay, newItem: CongViecNgay): Boolean {
                return oldItem.maCvNgay == newItem.maCvNgay
            }

            override fun areContentsTheSame(oldItem: CongViecNgay, newItem: CongViecNgay): Boolean {
                return oldItem == newItem
            }
        }

    val differ: AsyncListDiffer<CongViecNgay> = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DanhSachCongViecNgayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CongviecItemLayoutBinding =
            CongviecItemLayoutBinding.inflate(inflater, parent, false)
        return DanhSachCongViecNgayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DanhSachCongViecNgayViewHolder, position: Int) {
        val congViecNgay: CongViecNgay = differ.getCurrentList()[position]
        holder.bind(congViecNgay, position)

    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }


    inner class DanhSachCongViecNgayViewHolder(private val binding: CongviecItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(congViecNgay: CongViecNgay, position: Int) {
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
            binding.cbCongViec.setOnClickListener(View.OnClickListener {
                if (binding.cbCongViec.isChecked) {
                    binding.sbPercent.progress = 100
                    itemClick!!.onCheckBoxClick(
                        congViecNgay,true
                    )
                } else {
                    binding.sbPercent.progress = 0
                    itemClick!!.onCheckBoxClick(
                        congViecNgay,false
                    )
                }
            })
            binding.btnAnhCongViec.setOnClickListener(View.OnClickListener {
                itemClick!!.onImageButtonClick(
                    congViecNgay
                )
            })
            itemView.setOnClickListener(View.OnClickListener {
                itemClick!!.onItemClick(
                    congViecNgay
                )
            })
            binding.btnDelete.setOnClickListener {
                itemClick!!.onDeleteBtnClick(
                    congViecNgay.maCvNgay, position
                )
            }
            binding.sbPercent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvPercent.text = p1.toString() + "%"

                    if (p1 == 100) {
                        binding.cbCongViec.setChecked(true)
                    } else
                        binding.cbCongViec.setChecked(false)
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
}
