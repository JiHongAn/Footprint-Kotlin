package com.project.footprint.view.main

import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.footprint.R
import com.project.footprint.adapter.TravelAdapter
import com.project.footprint.helper.GetLocation
import com.project.footprint.view.map.MapActivity
import com.project.footprint.view.pick.PickActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // recyclerview 스크롤
        recyclerview.isNestedScrollingEnabled = false

        // liveData
        mainViewModel._currentArrayList.observe(this, Observer {
            recyclerview.visibility = View.VISIBLE
            progress.visibility = View.GONE
            var adapter = TravelAdapter(it)
            recyclerview.adapter = adapter
        })

        // 내 위치 바꾸기
        layout_search.setOnClickListener {
            val intent = Intent(this, PickActivity::class.java)
            startActivity(intent)
        }

        // 내 위치 찾기
        layout_my_location.setOnClickListener {
            // 내 위치 가져오기
            GetLocation(this, this).getCurrentLoc()
            setData()
        }

        // 지도로 이동하기
        fab.setOnClickListener {
            var intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    // on Resume
    override fun onResume() {
        super.onResume()
        setData()
    }

    // setData
    private fun setData() {
        recyclerview.visibility = View.GONE
        progress.visibility = View.VISIBLE

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

        // 주소
        var geoCoder = Geocoder(this)
        var address: List<Address>
        try {
            if (geoCoder != null) {
                address = geoCoder.getFromLocation(mapY!!.toDouble(), mapX!!.toDouble(), 1)
                if (address != null && address.isNotEmpty()) {
                    val addressData =
                        address[0].getAddressLine(0).split(" ".toRegex()).toTypedArray()
                    var title = addressData[1] + " " + addressData[2]
                    text_city.text = title
                    text_full_city.text = addressData[3]
                }
            }
        } catch (e: Exception) {
        }

        // 좌표 주변 여행지 불러오기
        mainViewModel.getLocation(mapX, mapY)
    }
}