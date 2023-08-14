package com.example.turismo.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.turismo.domain.Place
import com.example.turismo.R
import com.example.turismo.databinding.ActivityDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
  companion object {
    const val EXTRA_PLACE = "DetailActivity:place"
  }

  private lateinit var map: GoogleMap
  private val list = mutableListOf<CarouselItem>()

  private var latitude: Double = -34.603731
  private var longitude: Double = -58.381561
  private var title: String = "test"

  @Suppress("DEPRECATION")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val place = intent.getParcelableExtra<Place>(EXTRA_PLACE)
    binding.carousel.registerLifecycle(lifecycle)

    if (place != null) {
      binding.placeTitle.text = place.title
      binding.placeAddress.text = place.address
      binding.placeDistance.text = "${place.distance} km"
      binding.placeDescription.text = place.description

      list.add(CarouselItem(imageDrawable = place.image))
      list.add(CarouselItem(imageDrawable = R.drawable.wp2))
      list.add(CarouselItem(imageDrawable = R.drawable.wp3))

      latitude = place.latitude
      longitude = place.longitude
      title = place.title
    }

    binding.carousel.setData(list)

    val mapFragment = supportFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment
    mapFragment?.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    createMarker(LatLng(latitude, longitude))
  }

  private fun createMarker(latLng: LatLng) {
    map.addMarker(
      MarkerOptions()
        .position(latLng)
        .title(title)
    )
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
  }
}