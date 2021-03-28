package com.project.footprint.view.map

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.footprint.R
import com.project.footprint.model.Travel
import com.project.footprint.view.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.item_travel.view.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapViewModel: MapViewModel

    // 지도
    private lateinit var mMap: GoogleMap

    // Arraylist
    var markerList = ArrayList<Travel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        // 지도
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // liveData
        mapViewModel._currentArrayList.observe(this, Observer {
            markerList = it

            for (list in it) {
                // 마커 추가
                val position = LatLng(list.mapY.toDouble(), list.mapX.toDouble())
                val markerOptions = MarkerOptions()
                markerOptions.position(position)
                markerOptions.title(list.title)
                markerOptions.snippet(list.contentId)

                // 커스텀 지도 핀
                val bitmapDrawable =
                    resources.getDrawable(R.drawable.icon_marker) as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val smallMarker = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

                // 마커 추가
                mMap.addMarker(markerOptions)
            }
        })
    }

    // 지도
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 저장된 프리퍼런스 가져오기
        val preferences: SharedPreferences =
            getSharedPreferences("com.project.footprint", MODE_PRIVATE)

        var mapX = preferences.getString("mapX", "")
        var mapY = preferences.getString("mapY", "")

        // 만약 저장된 좌표가 없다면
        if (mapX.equals("") || mapY.equals("")) {
            // 기본 주소를 서울역으로 바꿈
            mapX = "126.9669363"
            mapY = "37.5534992"
        }

        // camera 이동
        val camera = LatLng(mapY!!.toDouble(), mapX!!.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 14F))

        // 좌표 주변 여행지 불러오기
        mapViewModel.getLocation(mapX, mapY)

        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
                false
            } else {
                item_travel.visibility = View.VISIBLE
                marker.hideInfoWindow()

                // 클릭 이벤트
                for (item in markerList) {
                    if (item.contentId.equals(marker.snippet)) {
                        var mapX = item.mapX.toDouble()
                        var mapY = item.mapY.toDouble()
                        var title = item.title
                        var firstImage = item.firstImage

                        val camera = LatLng(mapY, mapX)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 14F))
                        item_travel.text_title.text = title

                        // 이미지 설정
                        Glide.with(this)
                            .load(firstImage)
                            .into(item_travel.img_thumbnail)

                        // 클릭 이벤트
                        item_travel.setOnClickListener {
                            var intent = Intent(it.context, DetailActivity::class.java)

                            // intent를 넘겨준다
                            intent.putExtra("title", item.title)
                            intent.putExtra("contentId", item.contentId)
                            intent.putExtra("firstImage", item.firstImage)
                            intent.putExtra("mapX", item.mapX)
                            intent.putExtra("mapY", item.mapY)
                            it.context.startActivity(intent)
                        }
                        break
                    }
                }
                true
            }
        }
    }
}