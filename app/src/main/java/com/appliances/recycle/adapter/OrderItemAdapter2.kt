package com.appliances.recycle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.R
import com.appliances.recycle.dto.ItemDTO

class OrderItemAdapter2(private val fullItemList: List<ItemDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isExpanded = false  // 펼쳐졌는지 여부
    private val COLLAPSED_ITEM_COUNT = 2  // 기본적으로 보여줄 항목 수
    private val TYPE_ITEM = 0  // 일반 아이템 타입
    private val TYPE_FOOTER = 1  // "더 보기/접기" 버튼 타입

    // 보일 아이템 리스트 갱신
    private fun getVisibleItemCount(): Int {
        return if (isExpanded) fullItemList.size else COLLAPSED_ITEM_COUNT
    }

    // 일반 아이템과 "더 보기/접기" 버튼을 구분하기 위해 뷰 타입을 설정
    override fun getItemViewType(position: Int): Int {
        return if (position < getVisibleItemCount()) TYPE_ITEM else TYPE_FOOTER
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_layout, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer_layout, parent, false)
            FooterViewHolder(view)
        }
    }

    // ViewHolder 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = fullItemList[position]
            holder.bind(item)
        } else if (holder is FooterViewHolder) {
            holder.bind(isExpanded)
            holder.itemView.setOnClickListener {
                isExpanded = !isExpanded  // 펼쳐짐 상태 변경
                notifyDataSetChanged()  // 데이터 변경 적용
            }
        }
    }

    override fun getItemCount(): Int {
        // 기본적으로 보여줄 항목 수 + "더 보기/접기" 버튼
        return if (fullItemList.size > COLLAPSED_ITEM_COUNT) getVisibleItemCount() + 1 else fullItemList.size
    }

    // 일반 아이템 ViewHolder
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.orderName)
        val productPrice: TextView = itemView.findViewById(R.id.orderPrice)

        fun bind(item: ItemDTO) {
            // 아이템 데이터 설정
            itemView.findViewById<TextView>(R.id.orderName).text = item.iname
            itemView.findViewById<TextView>(R.id.orderPrice).text = item.iprice.toString()
        }
    }

    // "더 보기/접기" 버튼 ViewHolder
    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(isExpanded: Boolean) {
            val text = if (isExpanded) "접기" else "더 보기"
            itemView.findViewById<Button>(R.id.btn_footer).text = text
        }
    }
}