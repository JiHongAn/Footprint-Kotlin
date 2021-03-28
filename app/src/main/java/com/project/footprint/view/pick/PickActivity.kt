package com.project.footprint.view.pick

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.project.footprint.R
import kotlinx.android.synthetic.main.activity_pick.*

class PickActivity : AppCompatActivity(), OnMapReadyCallback {

    // 지도
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick)

        // 뒤로가기 버튼 추가
        val toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)

        // 지도
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 완료 버튼
        button_pick.setOnClickListener {
            var mapX = mMap.cameraPosition.target.longitude.toString()
            var mapY = mMap.cameraPosition.target.latitude.toString()

            // 프리퍼런스에 저장
            val preferences: SharedPreferences = getSharedPreferences("com.project.footprint", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("mapX", mapX)
            editor.putString("mapY", mapY)
            editor.commit()
            finish()
        }
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