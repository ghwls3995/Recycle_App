package com.appliances.recycle.retrofit

import com.appliances.recycle.dto.ItemDTO
import com.appliances.recycle.dto.RegisterRequest
import com.appliances.recycle.dto.LoginRequest
import com.appliances.recycle.dto.LoginResponse
import com.appliances.recycle.dto.PredictionResult
import com.appliances.recycle.dto.Notice
import com.appliances.recycle.dto.UserDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface INetworkService {

//    @GET("/api/users/{id}")
//    fun getUserById(@Path("id") id: String): Call<UserDTO>

    @Multipart
    @POST("/uploadImage")
    fun predictImage(
//        @Part("user") user: RequestBody?,          // JSON 데이터
        @Part image: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<String>

    @Multipart
    @POST("/classify")
    fun classifyImage(
        @Part image: MultipartBody.Part
    ): Call<PredictionResult>

    @Multipart
    @POST("/public/users")
//    fun registerUser(@Body userDTO: UserDTO): Call<Void>
    fun registerUser(
        @Part("user") user: RequestBody,          // JSON 데이터
        @Part profileImage: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<ResponseBody>

    @POST("/generateToken")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @FormUrlEncoded
    @POST("/echopickup/member/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Void>

    @POST("/echopickup/member/join")
    fun join(@Body registerRequest: RegisterRequest): Call<ResponseBody>

    @GET("/echopickup/api/notices")
    fun getNotices(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<Notice>>

    // 공지사항 상세 정보 가져오기
    @GET("/echopickup/api/notices/{nno}")
    fun getNoticeDetail(@Path("nno") nno: Long): Call<Notice>

    @GET("/api/getAllItems")
    fun getAllItems(): Call<MutableList<ItemDTO>> // 서버에서 아이템 목록을 가져오는 API

//    @GET("/api/users/page")
//    fun getItems(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): Call<PageResponse<UserItem>>
//
//    @GET("/api/users/page")
//    suspend fun getItems2(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): Response<PageResponse<UserItem>>
}