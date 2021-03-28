package com.project.footprint.view.detail

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
import com.project.footprint.model.Detail
import com.project.footprint.model.DetailImage
import org.json.JSONException
import org.json.JSONObject

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    // volley 객체
    private var requestQueue: RequestQueue? = null

    // url
    private var url = application.getString(R.string.rest_url) + "/get-detail.php?contentId="

    // url
    var urlImage = application.getString(R.string.rest_url) + "/get-image.php?contentId="

    // MutableLiveData
    private var liveData = MutableLiveData<Detail>()
    val _currentData: LiveData<Detail>
        get() = liveData


    // MutableLiveData
    private var liveImage = MutableLiveData<ArrayList<DetailImage>>()
    val _currentArrayList: LiveData<ArrayList<DetailImage>>
        get() = liveImage


    init {
        // Volley 객체 생성
        requestQueue = Volley.newRequestQueue(application)
    }

    fun getData(contentId: String) {
        // url
        url += contentId
        jsonParse(url)
    }

    fun jsonParse(url: String) {
        // 여행지 데이터 가져오기
        val request =
            JsonArrayRequest(Request.Method.GET, url, null, { response ->
                try {
                    val jsonObject: JSONObject = response.getJSONObject(0)

                    val title = jsonObject.getString("title")
                    val overView = jsonObject.getString("overView")

                    // Model 추가
                    var detail = Detail(title, overView)
                    liveData.value = detail
                } catch (e: JSONException) {
                    Log.e("e", e.printStackTrace().toString())
                }
            }, { error ->
                Log.e("e", error.printStackTrace().toString())
            })
        requestQueue?.add(request)
    }


    // 여행지 이미지를 가져온다
    fun getImage(contentId: String) {
        // url
        urlImage += contentId

        // 여행지 데이터 가져오기
        val request =
            JsonArrayRequest(Request.Method.GET, urlImage, null, { response ->
                try {
                    // ArrayList
                    var list = ArrayList<DetailImage>()

                    for (i in 0 until response.length()) {
                        val jsonObject: JSONObject = response.getJSONObject(0)

                        val image = jsonObject.getString("originimgurl")

                        // Model 추가
                        var detailImage = DetailImage(image)

                        // arraylist에 추가
                        list.add(detailImage)
                    }

                    liveImage.value = list
                } catch (e: JSONException) {
                    Log.e("e", e.printStackTrace().toString())
                }
            }, { error ->
                Log.e("e", error.printStackTrace().toString())
            })
        requestQueue?.add(request)
    }
}