package com.appliances.recycle

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appliances.recycle.databinding.ActivityRegisterBinding
import com.appliances.recycle.repository.RegisterRepository
import com.appliances.recycle.viewModel.RegisterViewModel
import com.appliances.recycle.viewModelFactory.RegisterViewModelFactory
import com.appliances.recycle.retrofit.INetworkService
import com.appliances.recycle.retrofit.MyApplication

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var networkService: INetworkService

    // RegisterViewModel을 ViewModelProvider로 초기화
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(RegisterRepository(networkService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // DataBinding 초기화
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myApplication = applicationContext as MyApplication
        networkService = myApplication.networkService  // 인증이 필요 없는 API 사용

        // Register 버튼 클릭 이벤트 처리
        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val mname = binding.editTextMname.text.toString()
            val pw = binding.editTextPassword.text.toString()
            val address = binding.editTextAdress.text.toString()
            val phone = binding.editTextPhone.text.toString()


            // ViewModel에서 회원가입 처리 호출
            registerViewModel.join(
                email = email,
                mname = mname,
                pw = pw,
                address = address,   // 필요한 경우 주소와 전화번호 추가
                phone = phone,
                onSuccess = {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // 메인 페이지로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    // 현재 창 닫기
                    finish()
                },
                onFailure = {
                    Log.e("RegisterActivity", "Registration failed: 서버에서 받은 응답을 확인하세요."+registerViewModel + "  "+email+ "  "+ pw )
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 상태바 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.parseColor("#48b8e7")
            }
        }
    }
}
