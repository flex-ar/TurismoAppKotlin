package com.example.turismo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.turismo.databinding.ActivityMapBinding
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
    places.forEach {
      createMarker(
        LatLng(it.latitude, it.longitude),
        it.title
      )
    }
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
}