package com.project.footprint.view.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.project.footprint.R
import com.project.footprint.view.detail_map.DetailMapActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var detailViewModel: DetailViewModel

    // 지도
    private lateinit var mMap: GoogleMap

    // content Id
    private var contentId = ""

    var mapX = 0.0
    var mapY = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        // 지도
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 초기 설정
        settingIntent()

        // 크게보기 버튼 클릭시
        button_bigger.setOnClickListener {
            var intent = Intent(it.context, DetailMapActivity::class.java)

            // intent를 넘겨준다
            intent.putExtra("title", title)
            intent.putExtra("mapX", mapX.toString())
            intent.putExtra("mapY", mapY.toString())
            it.context.startActivity(intent)
        }

        // 툴바 설정
        setToolbar()

        // liveData
        detailViewModel.getData(contentId)
        detailViewModel._currentData.observe(this, Observer {
            text_description_content.text = it.overView
        })

        // 이미지 리스트 가져오기
        detailViewModel.getImage(contentId)
        detailViewModel._currentArrayList.observe(this, Observer {
            val imageList = ArrayList<SlideModel>() // Create image list

            for (item in it) {
                imageList.add(SlideModel(item.image, null))
            }

            // 만약 데이터가 없으면 숨긴다
            if (it.size == 0) {
                text_image_title.visibility = View.GONE
            }
            image_slider.setImageList(imageList, true)
        })
    }

    private fun settingIntent() {
        // title 변경
        text_title.text = intent.getStringExtra("title").toString()

        // 여행지 id
        contentId = intent.getStringExtra("contentId").toString()

        // mapX
        mapX = intent.getStringExtra("mapX")!!.toDouble()

        // mapY
        mapY = intent.getStringExtra("mapY")!!.toDouble()

        // firstImage
        var firstImage = intent.getStringExtra("firstImage").toString()

        // 이미지 가져오기
        Glide.with(this).load(firstImage).into(img_cover)
    }

    // 지도
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val camera = LatLng(mapY, mapX)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 14F));

        // 지도에 핀 추가
        mMap = googleMap
        val position = LatLng(mapY, mapX)

        val markerOptions = MarkerOptions()
        markerOptions.position(position)
        markerOptions.title(intent.getStringExtra("title").toString())

        // 커스텀 지도 핀
        val bitmapDrawable = resources.getDrawable(R.drawable.icon_marker) as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap
        val smallMarker = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

        // 마커 추가
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
    }

    // 툴바 설정
    private fun setToolbar() {
        // 툴바 그림자 없애기
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.elevation = 0f

        // 툴바 뒤로가기 버튼
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // 툴바 텍스트 보이고 안보이게
        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        collapsingToolbar.title = " "
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        appBarLayout.setExpanded(true)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = intent.getStringExtra("title").toString()
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })
    }

    // 툴바 버튼 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // 뒤로가기 버튼 클릭시
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}