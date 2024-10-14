package com.appliances.recycle.repository

import android.content.SharedPreferences
import android.util.Log
import com.appliances.recycle.dto.LoginRequest
import com.appliances.recycle.retrofit.INetworkService

class LoginRepository(private val apiService: INetworkService, private val sharedPreferences: SharedPreferences) {

    suspend fun login(username: String, password: String): Boolean {
        val loginRequest = LoginRequest(username, password)
        val response = apiService.login(loginRequest)

        return if (response.isSuccessful && response.body() != null) {
            val accessToken = response.body()?.accessToken
            val refreshToken = response.body()?.refreshToken
            val username = response.body()?.username

            val name = response.body()?.name
            val phone = response.body()?.phone
            val address = response.body()?.address
            val social = response.body()?.social
            Log.d("name ","${name}" )
            Log.d("address ","${address}" )
            Log.d("name", String(name?.toByteArray(Charsets.UTF_8) ?: byteArrayOf(), Charsets.UTF_8))
            Log.d("address", String(address?.toByteArray(Charsets.UTF_8) ?: byteArrayOf(), Charsets.UTF_8))

            // JWT 토큰을 SharedPreferences에 저장
            sharedPreferences.edit().putString("jwt_token", accessToken).apply()
            sharedPreferences.edit().putString("refreshToken", refreshToken).apply()
            sharedPreferences.edit().putString("username", username).apply()

            sharedPreferences.edit().putString("name", name).apply()
            sharedPreferences.edit().putString("phone", phone).apply()
            sharedPreferences.edit().putString("address", address).apply()
            sharedPreferences.edit().putString("social", social).apply()

            true
        } else {
            false
        }
    }

}


