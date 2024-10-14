package com.appliances.recycle

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.adapter.OrderItemAdapter
import com.appliances.recycle.dto.ItemDTO
import com.appliances.recycle.dto.MemberDTO
import com.appliances.recycle.retrofit.INetworkService
import com.appliances.recycle.retrofit.MyApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.text.NumberFormat
import java.util.Locale

class ReservationDetailActivity : AppCompatActivity() {

    private lateinit var networkService: INetworkService
    private lateinit var app_prefs: SharedPreferences
    private lateinit var my_app_prefs: SharedPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var textCollectionDate: TextView
    private lateinit var btnSelectDate: Button
    private lateinit var textMemberName: TextView
    private lateinit var textMemberPhone: TextView
    private lateinit var textAddress: TextView
    private lateinit var editMemberName: EditText
    private lateinit var editMemberPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var addressFinderLauncher: ActivityResultLauncher<Bundle>
    private var isEditMode = false
    private lateinit var btnEditInfo: Button
    private lateinit var cardButton: LinearLayout
    private lateinit var bankTransferButton: LinearLayout
    private lateinit var mobilePaymentButton: LinearLayout
    private lateinit var kakaoPayButton: LinearLayout
    private lateinit var naverPayButton: LinearLayout
    private lateinit var btnSubmit: Button

    private var totalAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view2)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화 및 SharedPreferences에서 데이터 불러오기
        val initialItems = loadItemsFromSharedPrefs()
        orderItemAdapter = OrderItemAdapter(initialItems.toMutableList(), context = this)

        recyclerView.adapter = orderItemAdapter

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        networkService = myApplication.getApiService()

        app_prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        my_app_prefs = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

        // app_prefs에서 저장된 값 가져오기
        val name = app_prefs.getString("name", "이름 없음")
        val phone = app_prefs.getString("phone", "전화번호 없음")
        val address = app_prefs.getString("address", "주소 없음")

        // View 초기화
        textCollectionDate = findViewById(R.id.collection_date)
        textMemberName = findViewById(R.id.member_name)
        textMemberPhone = findViewById(R.id.member_phone)
        textAddress = findViewById(R.id.member_address)
        editMemberName = findViewById(R.id.edit_member_name)
        editMemberPhone = findViewById(R.id.edit_member_phone)
        editAddress = findViewById(R.id.edit_address)
        btnSelectDate = findViewById(R.id.btn_select_date)
        btnEditInfo = findViewById(R.id.btn_edit_info)

        cardButton = findViewById(R.id.Card)
        bankTransferButton = findViewById(R.id.BankTransfer)
        mobilePaymentButton = findViewById(R.id.MobilePayment)
        kakaoPayButton = findViewById(R.id.KakaoPay)
        naverPayButton = findViewById(R.id.NaverPay)
        btnSubmit = findViewById(R.id.btn_submit)

        updateTotalAmount()

        // DB에서 값 가져오기 (여기서는 예제로 하드코딩)
        textMemberName.text = name ?: "이름 없음"
        textAddress.text = address ?: "주소 없음"
        textMemberPhone.text = phone ?: "전화번호 없음"

        // 예약 예정일 수정 버튼 클릭 이벤트
        btnSelectDate.setOnClickListener {
            // 오늘 날짜를 기준으로 시작
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성
            val datePickerDialog = DatePickerDialog(
                this,
                { view, selectedYear, selectedMonth, selectedDay ->
                    // 날짜 선택 후 처리 (선택된 날짜를 텍스트뷰나 변수에 저장)
                    val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    // 선택된 날짜를 처리할 수 있도록 추가 로직 작성
                    // 예: 텍스트뷰에 선택된 날짜 표시
                    textCollectionDate.text = selectedDate
                },
                year, month, day
            )

            // 1년 내에서만 선택 가능하도록 범위 설정
            val oneYearLater = Calendar.getInstance()
            oneYearLater.add(Calendar.YEAR, 1)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis // 오늘 날짜부터
            datePickerDialog.datePicker.maxDate = oneYearLater.timeInMillis // 1년 후까지

            // DatePickerDialog 보여주기
            datePickerDialog.show()
        }

        editMemberPhone.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var prevLength: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                prevLength = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 아무 동작하지 않음
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                s?.let {
                    // 숫자만 남기기
                    val digitsOnly = s.toString().replace(Regex("[^\\d]"), "")

                    // 11자리까지만 허용
                    if (digitsOnly.length > 11) {
                        s.delete(11, s.length)
                        isFormatting = false
                        return
                    }

                    // 전화번호 형식으로 포맷 적용
                    val formattedNumber = formatPhoneNumber(digitsOnly)
                    s.replace(0, s.length, formattedNumber)
                }

                isFormatting = false
            }

            // 전화번호 포맷 적용 (000-0000-0000 형식)
            private fun formatPhoneNumber(digits: String): String {
                return when {
                    digits.length <= 3 -> digits // 3자리 이하일 때는 그대로 출력
                    digits.length <= 7 -> "${digits.substring(0, 3)}-${digits.substring(3)}" // 000-0000
                    else -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}" // 000-0000-0000
                }
            }
        })

        addressFinderLauncher = registerForActivityResult(AddressFinder.contract) { result ->
            if (result != Bundle.EMPTY) {
                // address와 zipcode 값을 받아온 후 처리
                val address = result.getString(AddressFinder.ADDRESS)
                val zipCode = result.getString(AddressFinder.ZIPCODE)
//                val editableText: Editable = Editable.Factory.getInstance().newEditable("[$zipCode] $address")
                val editableText: Editable = Editable.Factory.getInstance().newEditable("$address")
                // 받은 데이터를 사용해 필요한 작업 수행
                editAddress.text = editableText
            }
        }

        // 정보 수정 버튼 클릭 이벤트
        btnEditInfo.setOnClickListener {
            if (isEditMode) {
                textMemberName.text = editMemberName.text
                textAddress.text = editAddress.text
                textMemberPhone.text = editMemberPhone.text

                // EditText 숨기기
                editMemberName.visibility = View.GONE
                editAddress.visibility = View.GONE
                editMemberPhone.visibility = View.GONE

                // TextView 보이기
                textMemberName.visibility = View.VISIBLE
                textAddress.visibility = View.VISIBLE
                textMemberPhone.visibility = View.VISIBLE

                btnEditInfo.text = "수정"

                isEditMode = false

                networkService.getOrders().enqueue(object : Callback<MemberDTO> {
                    override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                        if (response.isSuccessful) {
                            val member = response.body()
                            if (member != null) {
                                Log.d("API", "User Info: ${member.mname}, ${member.address}, ${member.phone}")

                                textMemberName.text = member.mname ?: "이름 없음"
                                textAddress.text = member.address ?: "주소 없음"
                                textMemberPhone.text = member.phone ?: "전화번호 없음"
                            } else {
                                Log.e("API", "Received empty body")
                            }
                        } else {
                            val errorMessage = response.errorBody()?.string()
                            Log.e("API", "Failed to retrieve data: $errorMessage")
                            when (response.code()) {
                                401 -> Log.e("API", "Unauthorized: 로그인 필요")
                                404 -> Log.e("API", "User not found")
                                else -> Log.e("API", "Unexpected error: ${response.code()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                        Log.e("API", "Network request failed: ${t.message}")
                    }
                })
            } else {
                editMemberName.setText(textMemberName.text)
                editAddress.setText(textAddress.text)
                editMemberPhone.setText(textMemberPhone.text)

                textMemberName.visibility = View.GONE
                textAddress.visibility = View.GONE
                textMemberPhone.visibility = View.GONE

                editMemberName.visibility = View.VISIBLE
                editAddress.visibility = View.VISIBLE
                editMemberPhone.visibility = View.VISIBLE

                editAddress.setOnClickListener{
                    addressFinderLauncher.launch(Bundle())
                }

                btnEditInfo.text = "저장"

                isEditMode = true
            }
        }

        // 카드 버튼 클릭 리스너
        cardButton.setOnClickListener {
            // 카드 결제 클릭 시 처리할 작업
            Toast.makeText(this, "카드 결제가 선택되었습니다.", Toast.LENGTH_SHORT).show()
            // 여기서 카드 결제 페이지로 이동하는 코드 등을 추가할 수 있습니다.
        }

        // 무통장입금 버튼 클릭 리스너
        bankTransferButton.setOnClickListener {
            // 무통장입금 클릭 시 처리할 작업
            Toast.makeText(this, "무통장입금이 선택되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 핸드폰 결제 버튼 클릭 리스너
        mobilePaymentButton.setOnClickListener {
            // 핸드폰 결제 클릭 시 처리할 작업
            Toast.makeText(this, "핸드폰 결제가 선택되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 카카오페이 버튼 클릭 리스너
        kakaoPayButton.setOnClickListener {
            // 카카오페이 클릭 시 처리할 작업
            Toast.makeText(this, "카카오페이가 선택되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 네이버페이 버튼 클릭 리스너
        naverPayButton.setOnClickListener {
            // 네이버페이 클릭 시 처리할 작업
            Toast.makeText(this, "네이버페이가 선택되었습니다.", Toast.LENGTH_SHORT).show()
        }

        btnSubmit.setOnClickListener {
//            val intent = Intent(this, PayActivity::class.java)
//            startActivity(intent)
        }

    }

    // SharedPreferences에서 데이터를 불러오는 함수
    private fun loadItemsFromSharedPrefs(): List<ItemDTO> {
        val sharedPrefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val jsonString = sharedPrefs.getString("items_key", null)
        return if (jsonString != null) {
            val type = object : com.google.gson.reflect.TypeToken<List<ItemDTO>>() {}.type
            com.google.gson.Gson().fromJson(jsonString, type)
        } else {
            listOf() // 데이터가 없으면 빈 리스트 반환
        }
    }

    fun calculateTotalAmount(): Int {
        // SharedPreferences에서 JSON 문자열을 불러오기
        val sharedPrefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val jsonString = sharedPrefs.getString("items_key", null)

        return if (jsonString != null) {
            val type = object : TypeToken<List<ItemDTO>>() {}.type
            val itemList: List<ItemDTO> = Gson().fromJson(jsonString, type)

            // 모든 아이템의 iprice 값을 더함
            var totalAmount = 0
            for (item in itemList) {
                totalAmount += item.iprice.toInt() ?: 0 // iprice 값이 없으면 0 처리
            }
            totalAmount
        } else {
            0 // SharedPreferences에 데이터가 없을 경우 0 반환
        }
    }

    private fun updateTotalAmount() {
        val totalAmount = calculateTotalAmount()
        val formattedAmount = formatCurrency(totalAmount)
        btnSubmit.text = "${formattedAmount}원 결제"
    }

    fun formatCurrency(amount: Int): String {
        val numberFormat = NumberFormat.getInstance(Locale.KOREA) // 한국 형식으로 포맷팅
        return numberFormat.format(amount)
    }

}