package com.example.turismo.ui.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission(): Boolean {
  return ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
  ) == PackageManager.PERMISSION_GRANTED &&
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
}

fun locationPermissionIsReject(activity: Activity): Boolean {
  return ActivityCompat.shouldShowRequestPermissionRationale(
    activity,
    Manifest.permission.ACCESS_FINE_LOCATION
  ) || ActivityCompat.shouldShowRequestPermissionRationale(
    activity,
    Manifest.permission.ACCESS_COARSE_LOCATION
  )
}

fun requestLocationPermission(activity: Activity, permissionRejected: () -> Unit) {
  if (locationPermissionIsReject(activity)) {
    permissionRejected()
    return
  }

  ActivityCompat.requestPermissions(
    activity, arrayOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ), 0
  )
}
