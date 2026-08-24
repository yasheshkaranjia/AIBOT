package com.example.myapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // localhost because the Python backend runs in Termux on the SAME phone.
    // If you ever test from a different device on the same WiFi,
    // replace with your phone's local IP e.g. "http://192.168.1.x:8000/"
    private const val BASE_URL = "http://localhost:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // shows full request/response in Logcat
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)   // give up connecting after 10 s
        .readTimeout(120, TimeUnit.SECONDS)     // AI can take a while — wait up to 2 min
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
