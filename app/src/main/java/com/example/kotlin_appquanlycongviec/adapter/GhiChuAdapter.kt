package com.example.kotlin_appquanlycongviec.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.EventItemLayoutBinding
import com.example.kotlin_appquanlycongviec.databinding.NoteItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.GhiChu
import com.example.kotlin_appquanlycongviec.model.SuKien

class GhiChuAdapter: RecyclerView.Adapter<GhiChuAdapter.GhiChuViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(ghiChu: GhiChu)
        fun onDeleteBtnClick(maGhiChu: Int,position: Int)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }

    private val callback =
        object : DiffUtil.ItemCallback<GhiChu>() {
            override fun areItemsTheSame(oldItem: GhiChu, newItem: GhiChu): Boolean {
                return oldItem.maGCCN == newItem.maGCCN
            }

            override fun areContentsTheSame(oldItem: GhiChu, newItem: GhiChu): Boolean {
                return oldItem == newItem
            }
        }

    val differ: AsyncListDiffer<GhiChu> = AsyncListDiffer(this, callback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GhiChuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: NoteItemLayoutBinding =
            NoteItemLayoutBinding.inflate(inflater, parent, false)
        return GhiChuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GhiChuViewHolder, position: Int) {
        val ghiChu: GhiChu = differ.getCurrentList()[position]
        holder.bind(ghiChu,position)

    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }



    inner class GhiChuViewHolder(private val binding: NoteItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(ghiChu: GhiChu, position: Int) {
            binding.apply {

                val parts = ghiChu.ngayChinhSua.split("-")
                val nam = parts[0].toInt()
                val thang = parts[1].toInt() - 1
                val ngay = parts[2].toInt()
                tvThoiGian.text = "Sửa đổi lần cuối: "+dinhDangNgay(ngay, thang, nam)
                tvTieuDe.text = ghiChu.tieuDe
                tvNoiDung.text = ghiChu.noiDung


            }
            itemView.setOnClickListener {
                itemClick?.onItemClick(ghiChu)
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