package com.project.footprint.view.detail_map

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.footprint.R

class DetailMapActivity : AppCompatActivity(), OnMapReadyCallback {
    // 지도
    private lateinit var mMap: GoogleMap

    // title
    var title = ""

    // mapX
    var mapX = 0.0

    // mapY
    var mapY = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_map)

        // 뒤로가기 버튼 추가
        val toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)

        // title
        title = intent.getStringExtra("title").toString()
        setTitle(title)

        // mapX
        mapX = intent.getStringExtra("mapX")!!.toDouble()

        // mapY
        mapY = intent.getStringExtra("mapY")!!.toDouble()

        // 지도
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 지도
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 마커 클릭 이벤트
        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.hideInfoWindow()
            }
            true
        }

        // 카메라
        val camera = LatLng(mapY, mapX)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camera, 14F))

        // 마커 추가
        val position = LatLng(mapY, mapX)
        val markerOptions = MarkerOptions()
        markerOptions.position(position)
        markerOptions.title(title)

        // 커스텀 지도 마커
        val bitmapDrawable = resources.getDrawable(R.drawable.icon_marker) as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap
        val smallMarker = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

        // 마커 추가
        mMap.addMarker(markerOptions)
    }

    // 툴바 버튼 추가
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