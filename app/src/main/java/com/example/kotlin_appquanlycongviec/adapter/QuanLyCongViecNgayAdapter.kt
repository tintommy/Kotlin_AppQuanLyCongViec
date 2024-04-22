package com.example.kotlin_appquanlycongviec.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.CongviecItemLayoutBinding

class QuanLyCongViecNgayAdapter :RecyclerView.Adapter<QuanLyCongViecNgayAdapter.QuanLyCongViecNgayViewHolder>() {
    private val differ: AsyncListDiffer<String> =  AsyncListDiffer(this, DiffCallback);
    inner class QuanLyCongViecNgayViewHolder(private val binding: CongviecItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind() {

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuanLyCongViecNgayViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: QuanLyCongViecNgayViewHolder, position: Int) {
        TODO("Not yet implemented")
    }




    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


}