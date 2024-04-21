package com.example.kotlin_appquanlycongviec.api.apiService

import com.example.kotlin_appquanlycongviec.request.OTP
import com.example.kotlin_appquanlycongviec.request.SignInRequest
import com.example.kotlin_appquanlycongviec.request.SignUpRequest
import com.example.kotlin_appquanlycongviec.request.Status
import com.example.kotlin_appquanlycongviec.request.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApiService {
    @POST("/Login/signin")
    suspend fun userLogin(@Body signInRequest: SignInRequest): Response<Token>
    @POST("/Login/sendMail")
    suspend fun getOTP(@Query("email") email: String): Response<OTP>
    @POST("/Login/signup")
    suspend fun userSignUp(@Body signUpRequest: SignUpRequest): Response<Status>
}