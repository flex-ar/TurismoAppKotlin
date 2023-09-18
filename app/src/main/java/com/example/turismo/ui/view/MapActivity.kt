package com.example.turismo.ui.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.turismo.data.PlacesRepository
import com.example.turismo.databinding.ActivityMapBinding
import com.example.turismo.ui.utils.hasLocationPermission
import com.example.turismo.ui.utils.requestLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
  private lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMapBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val mapFragment =
      supportFragmentManager.findFragmentById(binding.generalMap.id) as? SupportMapFragment
    mapFragment?.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.setOnMyLocationButtonClickListener { false }
    PlacesRepository.getPlaces().forEach {
      createMarker(
        LatLng(it.latitude, it.longitude),
        it.title
      )
    }
    enableLocation()
    val latLng = LatLng(-46.44178452384911, -67.51758402307193)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
  }

  private fun createMarker(latLng: LatLng, title: String) {
    map.addMarker(
      MarkerOptions()
        .position(latLng)
        .title(title)
    )
  }

  @SuppressLint("MissingPermission")
  private fun enableLocation() {
    if (!::map.isInitialized) return
    if (hasLocationPermission()) {
      map.isMyLocationEnabled = true
    } else {
      requestLocationPermission(this) {
        Toast.makeText(this, "Por favor acepte los permisos", Toast.LENGTH_LONG).show()
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      0 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        map.isMyLocationEnabled = true
      } else {
        Toast.makeText(
          this,
          "Para activar la localizacion, ve a ajustes y acepta los permisos",
          Toast.LENGTH_LONG
        ).show()
      }

      else -> return
    }
  }
}