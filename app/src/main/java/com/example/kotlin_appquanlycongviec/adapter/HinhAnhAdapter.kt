package com.example.kotlin_appquanlycongviec.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin_appquanlycongviec.databinding.HinhanhItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.HinhAnh

class HinhAnhAdapter(private val danhSachAnh: MutableList<HinhAnh>) :
    RecyclerView.Adapter<HinhAnhAdapter.HinhAnhViewHolder>() {
    interface OnItemClickListener {
        fun onDeleteButtonClick(hinhAnh: HinhAnh, position: Int)
    }

    private var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HinhAnhViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HinhanhItemLayoutBinding.inflate(inflater, parent, false)
        return HinhAnhViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HinhAnhViewHolder, position: Int) {
        val hinhAnh = danhSachAnh[position]
        holder.bind(hinhAnh)
        holder.binding.btnXoa.setOnClickListener {
            itemClick?.onDeleteButtonClick(hinhAnh, position)
        }
    }

    override fun getItemCount(): Int {
        return danhSachAnh.size
    }

    fun xoaHinhAnhTrongDanhSach(position: Int) {
        danhSachAnh.removeAt(position)
        notifyDataSetChanged()
    }

    inner class HinhAnhViewHolder(val binding: HinhanhItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hinhAnh: HinhAnh) {
            Glide.with(binding.root).load(hinhAnh.link).into(binding.ivHinhAnh)
        }
    }
}