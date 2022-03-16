package com.example.application

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.LiveData

data class LocationData(val lng: Double, val lat: Double) {
    companion object {
        fun create(location: Location) = LocationData(location.longitude, location.latitude)
    }

    override fun toString() = "$lng, $lat"
}

class LocationLiveData(context: Context) : LiveData<LocationData>() {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, listener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, listener)
    }

    private fun notifyUpdates(data: LocationData) {
        if (hasActiveObservers()) value = data
    }

    private val listener = LocationListener {
        notifyUpdates(LocationData.create(it))
    }

}