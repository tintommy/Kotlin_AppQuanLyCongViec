package com.example.kotlin_appquanlycongviec.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.CongviecItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.CongviecchualamItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay


class DanhSachCongViecChuaLamAdapter :
    RecyclerView.Adapter<DanhSachCongViecChuaLamAdapter.DanhSachCongViecChuaLamViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(congViecNgay: CongViecNgay?)
        fun onCheckBoxClick(maCongViecNgay: Int)
        fun onImageButtonClick(congViecNgay: CongViecNgay?)
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
                return oldItem==newItem
            }
        }

    val differ: AsyncListDiffer<CongViecNgay> = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DanhSachCongViecChuaLamViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CongviecchualamItemLayoutBinding =
            CongviecchualamItemLayoutBinding.inflate(inflater, parent, false)
        return DanhSachCongViecChuaLamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DanhSachCongViecChuaLamViewHolder, position: Int) {
        val congViecNgay: CongViecNgay = differ.getCurrentList()[position]
        holder.bind(congViecNgay)

    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }


    inner class DanhSachCongViecChuaLamViewHolder(private val binding: CongviecchualamItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(congViecNgay: CongViecNgay) {
            val parts = congViecNgay.ngayLam.split("-")
            val nam = parts[0].toInt()
            val thang = parts[1].toInt() - 1
            val ngay = parts[2].toInt()
            binding.tvNgay.text = dinhDangNgay(ngay, thang, nam)
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

            binding.cbCongViec.setOnClickListener(View.OnClickListener {
                itemClick!!.onCheckBoxClick(
                    congViecNgay.maCvNgay
                )
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
        }
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
}
