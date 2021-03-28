package com.project.footprint.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.footprint.R
import com.project.footprint.model.Travel
import com.project.footprint.view.detail.DetailActivity
import kotlinx.android.synthetic.main.item_travel.view.*
import kotlin.math.roundToInt

class TravelAdapter(private val items: ArrayList<Travel>) :
    RecyclerView.Adapter<TravelAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            var intent = Intent(it.context, DetailActivity::class.java)

            // intent를 넘겨준다
            intent.putExtra("title", item.title)
            intent.putExtra("contentId", item.contentId)
            intent.putExtra("firstImage", item.firstImage)
            intent.putExtra("mapX", item.mapX)
            intent.putExtra("mapY", item.mapY)
            it.context.startActivity(intent)
        }

        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_travel, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var view: View = view

        fun bind(clickEvent: View.OnClickListener, item: Travel) {
            // title 설정
            view.text_title.text = item.title

            // 이미지 설정
            Glide.with(itemView.context)
                .load(item.firstImage)
                .into(itemView.img_thumbnail)

            // 카테고리
            when (item.contentTypeId) {
                "12" -> view.text_content_type.text = "관광지"
                "14" -> view.text_content_type.text = "문화시설"
                "15" -> view.text_content_type.text = "행사"
                "25" -> view.text_content_type.text = "여행코스"
                "32" -> view.text_content_type.text = "숙박"
                "39" -> view.text_content_type.text = "음식점"
                else -> view.text_content_type.text = "기타"
            }

            // 거리
            var dist: Float = item.dist.toFloat() / 1000
            dist = ((dist * 100).roundToInt() / 100.0).toFloat()
            val distString = dist.toString() + "km"
            view.text_dist.text = distString

            // 클릭 이벤트 추가하기
            view.setOnClickListener(clickEvent)
        }
    }
}