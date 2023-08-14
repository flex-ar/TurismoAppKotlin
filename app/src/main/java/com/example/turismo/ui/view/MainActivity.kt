package com.example.turismo.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.turismo.R
import com.example.turismo.databinding.ActivityMainBinding
import com.example.turismo.domain.Place
import com.example.turismo.ui.adapter.PlacesAdapter
import com.example.turismo.ui.viewmodel.PlacesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var placesAdapter: PlacesAdapter
  private lateinit var placesViewModel: PlacesViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    if (!isLocationPermissionGranted()) {
      requestLocationPermission()
    }

    placesViewModel = ViewModelProvider(this)[PlacesViewModel::class.java]
    placesAdapter = PlacesAdapter() { navigateTo(it) }
    binding.recycler.adapter = placesAdapter

    binding.btnMap.setOnClickListener {
      val intent = Intent(this, MapActivity::class.java)
      startActivity(intent)
    }

    binding.btnSort.setOnClickListener { showMenu(it, R.menu.popup_menu) }

    subscribeToObservable()
  }

  private fun subscribeToObservable() {
    lifecycleScope.launch {
      placesViewModel.placesFlow.collectLatest {
        placesAdapter.updatePlaceList(it)
      }
    }
  }

  private fun showMenu(v: View, @MenuRes menuRes: Int) {
    val popup = PopupMenu(this, v)
    popup.menuInflater.inflate(menuRes, popup.menu)
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.menu_option_default -> {
          placesViewModel.sortByIndex()
          true
        }
        R.id.menu_option_distance -> {
          placesViewModel.sortByDistance()
          true
        }
        else -> false
      }
    }

    popup.show()
  }

  private fun navigateTo(place: Place) {
    val intent = Intent(this, DetailActivity::class.java)
    intent.putExtra(DetailActivity.EXTRA_PLACE, place)
    startActivity(intent)
  }

  private val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

  private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
    this,
    fineLocationPermission
  ) == PackageManager.PERMISSION_GRANTED

  private fun locationPermissionIsReject() = ActivityCompat.shouldShowRequestPermissionRationale(
    this,
    fineLocationPermission
  )

  private fun requestLocationPermission() {
    if (locationPermissionIsReject()) {
      return
    }

    ActivityCompat.requestPermissions(this, arrayOf(fineLocationPermission), 0)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      0 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) return
      else -> return
    }
  }
}