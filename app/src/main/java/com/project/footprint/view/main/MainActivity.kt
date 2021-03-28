package com.project.footprint.view.main

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.footprint.R
import com.project.footprint.adapter.TravelAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // liveData
        mainViewModel._currentArrayList.observe(this, Observer {
            progress.visibility = View.GONE
            var adapter = TravelAdapter(it)
            recyclerview.adapter = adapter
        })

        // recyclerview 스크롤
        recyclerview.isNestedScrollingEnabled = false

        // 저장된 프리퍼런스 가져오기
        val pref = this.getPreferences(0)
        var mapX = pref.getString("mapX", "")
        var mapY = pref.getString("mapY", "")

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