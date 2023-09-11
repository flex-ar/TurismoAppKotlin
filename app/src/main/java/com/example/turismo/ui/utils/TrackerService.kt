package com.example.turismo.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerService {
  @SuppressLint("MissingPermission")
  suspend fun getUserLocation(context: Context, isLocationPermission: Boolean): Location? {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnable =
      locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER
      )

    if (!isGpsEnable || !isLocationPermission) {
      return null
    }

    return suspendCancellableCoroutine { cont ->
      fusedLocationProviderClient.lastLocation.apply {
        if (isComplete) {
          if (isSuccessful) {
            cont.resume(result) {}
          } else {
            cont.resume(null) {}
          }
          return@suspendCancellableCoroutine
        }

        addOnSuccessListener {
          cont.resume(it) {}
        }

        addOnFailureListener {
          cont.resume(null) {}
        }

        addOnCanceledListener {
          cont.resume(null) {}
        }
      }
    }
  }
}