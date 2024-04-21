package com.example.kotlin_appquanlycongviec.request


import com.google.gson.annotations.SerializedName

data class OTP(
    @SerializedName("otp")
    val otp: String
)