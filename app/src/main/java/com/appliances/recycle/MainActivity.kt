package com.appliances.recycle

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.activity.viewModels
import com.appliances.recycle.repository.LoginRepository
import com.appliances.recycle.viewModel.LoginViewModel
import com.appliances.recycle.viewModelFactory.LoginViewModelFactory
import com.appliances.recycle.retrofit.INetworkService
import com.appliances.recycle.retrofit.MyApplication

class MainActivity : AppCompatActivity() {

    private lateinit var networkService: INetworkService
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        networkService = myApplication.getApiService()

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        // 로그인 버튼 클릭 이벤트 처리
        loginButton.setOnClickListener {
            // 로그인 처리 로직 구현
            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginViewModel.login(username, password)
        }

        loginViewModel.loginResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                // 로그인 성공 시 다음 화면으로 이동
                startActivity(Intent(this, MainPageActivity::class.java))
                finish()
            } else {
                // 로그인 실패 시 메시지 표시
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

//        // 아이디 찾기, 비밀번호 찾기, 회원가입 이벤트 처리
//        findViewById<TextView>(R.id.forgotId).setOnClickListener {
//            // 아이디 찾기 로직 구현
//        }
//
//        findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
//            // 비밀번호 찾기 로직 구현
//        }

        // 회원가입 버튼 클릭 리스너
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            // 회원가입 화면으로 이동하는 예시 Intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

    private val loginViewModel: LoginViewModel by viewModels {
        val loginRepository = LoginRepository(networkService, sharedPreferences)
        LoginViewModelFactory(loginRepository)
    }

}
