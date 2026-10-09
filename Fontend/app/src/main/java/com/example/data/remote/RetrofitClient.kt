package com.example.data.remote

import android.content.Context
import com.example.untils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
// nếu dùng máy ảo thì dùng cái này
//    private const val BASE_URL =
//        "http://10.0.2.2:8080/"
    //http://192.168.1.7:8080/

    private const val BASE_URL = "http://10.0.2.2:8080/"
    @Volatile
    private var apiService: ApiService? = null

    fun getApiService(
        context: Context
    ): ApiService {

        return apiService ?: synchronized(this) {

            apiService ?: createApiService(context).also {
                apiService = it
            }
        }
    }

    private fun createApiService(
        context: Context
    ): ApiService {

        val tokenManager = TokenManager(context)

        val loggingInterceptor =
            HttpLoggingInterceptor().apply {

                level =
                    HttpLoggingInterceptor.Level.BASIC
            }

        val authInterceptor = Interceptor { chain ->

            val originalRequest =
                chain.request()

            val token =
                tokenManager.getToken()

            val requestBuilder =
                originalRequest.newBuilder()

            if (!token.isNullOrBlank()) {

                requestBuilder.header(
                    "Authorization",
                    "Bearer $token"
                )
            }

            chain.proceed(
                requestBuilder.build()
            )
        }

        val okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}