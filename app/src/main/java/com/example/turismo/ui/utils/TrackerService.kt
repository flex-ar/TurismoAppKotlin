package com.example.turismo.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class TrackerService(private val context: Context, private val callback: (Location) -> Unit) {
  private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
  private val locationCallback = object: LocationCallback() {
    override fun onLocationResult(locationResult:  LocationResult) {
      callback(locationResult.lastLocation ?: return)
    }
  }

  @SuppressLint("MissingPermission")
  fun startLocationUpdates() {
    if (context.isGpsEnable()) {
      fusedLocationProviderClient.lastLocation.addOnSuccessListener {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000L
        locationRequest.fastestInterval = 2000L
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
      }
    }
  }

  fun removeLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
  }
}