package com.appliances.recycle

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.adapter.ItemAdapter
import com.appliances.recycle.adapter.OrderItemAdapter
import com.appliances.recycle.dto.ItemDTO
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class ReservationDetailActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_reservation_detail)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resv)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }

    private lateinit var textCollectionDate: TextView
    private lateinit var textMemberName: TextView
    private lateinit var textMemberPhone: TextView
    private lateinit var textAddress: TextView
    private lateinit var editMemberName: EditText
    private lateinit var editMemberPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnEditInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

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

        // DB에서 값 가져오기 (여기서는 예제로 하드코딩)
//        textCollectionDate.text =
//        textMemberName =
//        textAddress.text =
//        textMemberPhone =

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



        // 정보 수정 버튼 클릭 이벤트
        btnEditInfo.setOnClickListener {
            // TextView 숨기기
            textMemberName.visibility = View.GONE
            textAddress.visibility = View.GONE
            textMemberPhone.visibility = View.GONE

            // EditText 보이기
            editMemberName.visibility = View.VISIBLE
            editAddress.visibility = View.VISIBLE
            editMemberPhone.visibility = View.VISIBLE

            // 기존 TextView의 값을 EditText에 복사
            editMemberName.setText(textMemberName.text)
            editAddress.setText(textAddress.text)
            editMemberPhone.setText(textMemberPhone.text)

//            val itemDTO = loadItemDTOFromDB()
//
//            // 멤버 이름, 전화번호, 주소 등을 TextView에 설정
//            textMemberName.text = itemDTO?.memberName ?: "이름 없음"
//            textMemberPhone.text = itemDTO?.memberPhone ?: "전화번호 없음"
//            textAddress.text = itemDTO?.memberAddress ?: "주소 없음"
        }

    }

}