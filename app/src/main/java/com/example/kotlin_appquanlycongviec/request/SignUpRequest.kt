package com.example.kotlin_appquanlycongviec.request

import android.provider.ContactsContract.CommonDataKinds.Email
import java.io.Serializable

data class SignUpRequest(
    val email: String,
    val ho: String,
    val ten: String,
    val password: String,
    val ngaySinh: String,
    val gioiTinh: Boolean
): Serializable
