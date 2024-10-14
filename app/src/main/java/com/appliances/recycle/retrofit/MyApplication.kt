package com.appliances.recycle.retrofit

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.appliances.recycle.interceptorN.AuthInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyApplication : Application(){

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit_token: Retrofit
    private lateinit var apiService: INetworkService

    // http 퍼미션 허용 및, 로컬호스트 안될시 아이피로 확인 하기.
    val BASE_URL = "http://10.100.201.12:8080"

    var networkService: INetworkService
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // 앱 초기화 시 네트워크 서비스도 초기화
        initialize(this)
    }

    // OkHttpClient를 설정하고, sharedPreferences에서 토큰을 가져오도록 수정
    val client: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()

                    // JWT 토큰을 sharedPreferences에서 가져옴
                    val token = sharedPreferences.getString("jwt_token", null)
                    if (!token.isNullOrEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                    }

                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃 30초로 설정
                .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃 30초로 설정
                .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 30초로 설정
                .build()
        }

    val gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    // 토큰 가져오기 작업 및 API 서비스 초기화
    fun initialize(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // JWT 토큰을 sharedPreferences에서 가져옴
                val token = sharedPreferences.getString("jwt_token", null)
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit_token = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit_token.create(INetworkService::class.java)
    }

    fun getApiService(): INetworkService {
        return apiService
    }

    init {
        networkService = retrofit.create(INetworkService::class.java)
    }
}