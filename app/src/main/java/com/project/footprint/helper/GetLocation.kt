package com.project.footprint.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class GetLocation(context: Context, activity: Activity) {
    var context = context
    var activity = activity

    var locationManager: LocationManager? = null
    private val REQUEST_CODE_LOCATION: Int = 2

    var latitude: Double? = null
    var longitude: Double? = null

    fun getCurrentLoc() {
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
        var userLocation: Location = getLatLng()
        if (userLocation != null) {
            latitude = userLocation.latitude
            longitude = userLocation.longitude

            // 프리퍼런스에 저장
            val preferences: SharedPreferences = context.getSharedPreferences(
                "com.project.footprint",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("mapX", longitude.toString())
            editor.putString("mapY", latitude.toString())
            editor.commit()
        }
    }

    private fun getLatLng(): Location {
        var currentLatLng: Location? = null
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this.REQUEST_CODE_LOCATION
            )
            getLatLng()
        } else {
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationManager?.getLastKnownLocation(locationProvider)
        }
        return currentLatLng!!
    }
}