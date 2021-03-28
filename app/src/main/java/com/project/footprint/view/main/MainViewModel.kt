package com.project.footprint.view.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.project.footprint.R
import com.project.footprint.model.Travel
import org.json.JSONException
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // volley 객체
    private var requestQueue: RequestQueue? = null

    // 파싱 주소
    var url: String = ""


    // MutableLiveData
    private val liveList = MutableLiveData<ArrayList<Travel>>()
    private var list = ArrayList<Travel>()
    private var listSearch = ArrayList<Travel>()
    val _currentArrayList: LiveData<ArrayList<Travel>>
        get() = liveList


    init {
        // Volley 객체 생성
        requestQueue = Volley.newRequestQueue(application)

        // url
        url = application.getString(R.string.rest_url)
    }

    fun getLocation(mapX: String?, mapY: String?) {
        // url 주소
        url += "/get-around.php?mapX=$mapX&mapY=$mapY"
        jsonParse(url)
    }

    // 여행지 데이터를 가져온다
    private fun jsonParse(url: String) {
        list.clear()

        val request =
            JsonArrayRequest(Request.Method.GET, url, null, { response ->
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject: JSONObject = response.getJSONObject(i)

                        val title = jsonObject.getString("title")
                        val firstImage = jsonObject.getString("firstImage")
                        val mapX = jsonObject.getString("mapX")
                        val mapY = jsonObject.getString("mapY")
                        val contentId = jsonObject.getString("contentId")
                        val contentTypeId = jsonObject.getString("contentTypeId")
                        val address = jsonObject.getString("address")
                        val dist = jsonObject.getString("dist")

                        // Model 추가
                        var travelItem = Travel(
                            title,
                            firstImage,
                            mapX,
                            mapY,
                            contentId,
                            contentTypeId,
                            address,
                            dist
                        )

                        // arraylist에 model 추가
                        list.add(travelItem)
                    }

                    liveList.value = list
                } catch (e: JSONException) {
                    Log.e("e", e.printStackTrace().toString())
                }
            }, { error ->
                Log.e("e", error.printStackTrace().toString())
            })
        requestQueue?.add(request)
    }

    // 검색기능
    fun search(query: String) {
        // arraylist를 비운다
        listSearch.clear()

        for (item in list) {
            // 만약 국가 이름이나 국가 코드, 국가 번호가 같다면
            if (item.title.toLowerCase().contains(query)) {
                listSearch.add(item)
            }
        }

        liveList.value = listSearch
    }
}