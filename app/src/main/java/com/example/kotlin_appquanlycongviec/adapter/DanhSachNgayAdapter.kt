package com.example.kotlin_appquanlycongviec.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_appquanlycongviec.databinding.NgayItemLayoutBinding
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.response.NgayDaTaoResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class DanhSachNgayAdapter : RecyclerView.Adapter<DanhSachNgayAdapter.DanhSachNgayViewHolder>() {

    val differ: AsyncListDiffer<NgayDaTaoResponse> =  AsyncListDiffer(this, DiffCallback)

    interface OnItemClickListener {
        fun onItemClick(ngayDaTaoResponse:NgayDaTaoResponse)
        fun onDeleteButtonClick(ngayDaTaoResponse:NgayDaTaoResponse)
        fun onDetailClick(ngayDaTaoResponse:NgayDaTaoResponse)
    }


    var itemClick: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClick = listener
    }

    inner class DanhSachNgayViewHolder(private val binding: NgayItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(ngayDaTao: NgayDaTaoResponse) {
            binding.tvNgay.text = convertToDDMMYYYY(ngayDaTao.ngay)
            binding.tvTienDo.text = ngayDaTao.phanTram.toInt().toString() +"%"
            binding.btnXoaCongViec.setOnClickListener { itemClick?.onDeleteButtonClick(ngayDaTao) }
            binding.btnDetail.setOnClickListener {itemClick?.onDetailClick(ngayDaTao)  }
            binding.cvItemNgay.setOnClickListener {itemClick?.onItemClick(ngayDaTao)   }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DanhSachNgayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: NgayItemLayoutBinding =
            NgayItemLayoutBinding.inflate(inflater, parent, false)
        return DanhSachNgayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DanhSachNgayViewHolder, position: Int) {
        val ngayDaTao: NgayDaTaoResponse = differ.currentList[position]
        holder.bind(ngayDaTao)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NgayDaTaoResponse>() {
        override fun areItemsTheSame(oldItem: NgayDaTaoResponse, newItem: NgayDaTaoResponse): Boolean {
            return oldItem.ngay == newItem.ngay
        }

        override fun areContentsTheSame(oldItem: NgayDaTaoResponse, newItem: NgayDaTaoResponse): Boolean {
            return oldItem == newItem
        }
    }

    // Hàm chuyển đổi từ "yyyy-MM-dd" sang "dd-MM-yyyy"
    private fun convertToDDMMYYYY(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        var date: Date? = null
        date = try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        return outputFormat.format(date)
    }
}