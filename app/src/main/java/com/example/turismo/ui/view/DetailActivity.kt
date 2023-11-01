package com.example.turismo.ui.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.turismo.R
import com.example.turismo.data.PlacesRepository
import com.example.turismo.databinding.ActivityDetailBinding
import com.example.turismo.ui.utils.PopupManager
import com.example.turismo.ui.utils.hasLocationPermission
import com.example.turismo.ui.utils.requestLocationPermission
import com.example.turismo.ui.viewmodel.PlacesViewModel
import com.example.turismo.ui.viewmodel.PlacesViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
  private lateinit var map: GoogleMap
  private lateinit var binding: ActivityDetailBinding
  private val viewModel: PlacesViewModel by viewModels { PlacesViewModelFactory(PlacesRepository) }
  private val list = mutableListOf<CarouselItem>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    binding.carousel.registerLifecycle(lifecycle)

    val mapFragment = supportFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment
    mapFragment?.getMapAsync(this)

    subscribeToObservable()

//    binding.buttonAudio.setOnClickListener {}

    binding.buttonAudio.setOnClickListener {
      showPopup()
    }
  }

  private fun showPopup() {
    lifecycleScope.launch {
      viewModel.placeSelected.collectLatest {
        PopupManager.showPopup(this@DetailActivity, it)
      }
    }
  }

  private fun subscribeToObservable() {
    lifecycleScope.launch {
      viewModel.placeSelected.collectLatest {
        binding.placeTitle.text = it.title
        binding.placeAddress.text = it.address
        binding.placeDistance.text = "${it.distance} km"
        binding.placeDescription.text = it.description
        list.add(CarouselItem(imageDrawable = it.image))
        list.add(CarouselItem(imageDrawable = R.drawable.wp2))
        list.add(CarouselItem(imageDrawable = R.drawable.wp3))
        binding.carousel.setData(list)
      }
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.setOnMyLocationButtonClickListener { false }
    lifecycleScope.launch {
      viewModel.placeSelected.collectLatest {
        createMarker(LatLng(it.latitude, it.longitude), it.title)
        enableLocation()
      }
    }
  }

  private fun createMarker(latLng: LatLng, title: String) {
    map.addMarker(
      MarkerOptions()
        .position(latLng)
        .title(title)
    )
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
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