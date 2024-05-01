package com.example.kotlin_appquanlycongviec.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.CongviecItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.EventItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.SuKien

class SuKienAdapter :
    RecyclerView.Adapter<SuKienAdapter.SuKienViewHolder>() {




    interface OnItemClickListener {
        fun onItemClick(suKien: SuKien)
        fun onDeleteBtnClick(maSuKien: Int,position: Int)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }

    private val callback =
        object : DiffUtil.ItemCallback<SuKien>() {
            override fun areItemsTheSame(oldItem: SuKien, newItem: SuKien): Boolean {
                return oldItem.maSK == newItem.maSK
            }

            override fun areContentsTheSame(oldItem: SuKien, newItem: SuKien): Boolean {
                return oldItem == newItem
            }
        }

    val differ: AsyncListDiffer<SuKien> = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuKienViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: EventItemLayoutBinding =
            EventItemLayoutBinding.inflate(inflater, parent, false)
        return SuKienViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuKienViewHolder, position: Int) {
        val suKien: SuKien = differ.getCurrentList()[position]
        holder.bind(suKien,position)

    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }



    inner class SuKienViewHolder(private val binding: EventItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(suKien: SuKien,position: Int) {
            binding.apply {

                val parts = suKien.ngay.split("-")
                val nam = parts[0].toInt()
                val thang = parts[1].toInt() - 1
                val ngay = parts[2].toInt()
                tvDate.text = dinhDangNgay(ngay, thang, nam)
                tvHour.text = suKien.gio
                tvEvent.text = suKien.tenSuKien

                btnDelete.setOnClickListener {
                    itemClick?.onDeleteBtnClick(suKien.maSK,position)
                }
            }
            itemView.setOnClickListener {
                itemClick?.onItemClick(suKien)
            }

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