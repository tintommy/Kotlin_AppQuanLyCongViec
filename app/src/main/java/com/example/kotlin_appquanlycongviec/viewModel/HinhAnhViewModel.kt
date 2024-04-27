package com.example.kotlin_appquanlycongviec.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_appquanlycongviec.api.ApiInstance
import com.example.kotlin_appquanlycongviec.api.apiService.CongViecNgayApiService
import com.example.kotlin_appquanlycongviec.api.apiService.HinhAnhApiService
import com.example.kotlin_appquanlycongviec.model.CongViecNgay
import com.example.kotlin_appquanlycongviec.model.HinhAnh
import com.example.kotlin_appquanlycongviec.util.Resource
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HinhAnhViewModel @Inject constructor(private val sharedPref: SharedPreferences)  : ViewModel() {
    private lateinit var token: String
    private var userId: Int = 0
    private lateinit var userEmail: String
    private var _hinhAnhList: MutableStateFlow<Resource<MutableList<HinhAnh>>> =
        MutableStateFlow(Resource.Unspecified())
    val hinhAnhList = _hinhAnhList.asStateFlow()

    private lateinit var hinhAnhApiService: HinhAnhApiService


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.userId = sharedPref.getInt("userId", 0)
        this.userEmail = sharedPref.getString("userEmail", "").toString()
        var retrofit = ApiInstance.getClient(token)
        hinhAnhApiService = retrofit.create(HinhAnhApiService::class.java)


    }


    fun taiDanhSachHinhAnh(maCvNgay: Int) {
        viewModelScope.launch {
            _hinhAnhList.emit(Resource.Loading())

            val response = hinhAnhApiService.layDanhSachHinhAnh(maCvNgay)
            if (response.isSuccessful) {

                _hinhAnhList.emit(Resource.Success(response.body()!!))
            } else {
                if (response.code() == 404) {
                    _hinhAnhList.emit(Resource.Error("404"))
                }
            }
        }

    }


    fun luuDanhSachAnh(linkHinhAnh: List<Uri?>, context: Context, maCvNgay: Int) {
        viewModelScope.launch {
            _hinhAnhList.emit(Resource.Loading())
            val anhURL: MutableList<String> = ArrayList()
            val soAnh = linkHinhAnh.size
            val storageRef = FirebaseStorage.getInstance().getReference()
            for (imageUri in linkHinhAnh) {
                val imageName =
                    "image_" + UUID.randomUUID().toString() // Tên duy nhất cho mỗi ảnh
                val imageRef = storageRef.child("images/$imageName")
                val stream = context.contentResolver.openInputStream(imageUri!!)
                val uploadTask = imageRef.putStream(stream!!)
                uploadTask.addOnFailureListener { }.addOnSuccessListener {
                    imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        anhURL.add(imageUrl)
                        Log.e("SIZE", anhURL.size.toString())
                        if (anhURL.size == soAnh) {

                            viewModelScope.launch {
                                val response =
                                    hinhAnhApiService.luuDanhSachAnh(maCvNgay, anhURL)
                                if (response.isSuccessful) {
                                    taiDanhSachHinhAnh(maCvNgay)
                                }
                                else{
                                    _hinhAnhList.emit(Resource.Error("Lỗi khi up ảnh"))
                                }
                            }

                        }

                    }
                }
            }

            }
        }

        fun xoaAnh(maHinhAnh: Int, linkHinhAnh: String?) {
            viewModelScope.launch {
                val response = hinhAnhApiService.xoaHinhAnh(maHinhAnh)
                if (response.isSuccessful) {
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                        linkHinhAnh!!
                    )
                    imageRef.delete()
                }
            }


        }

}