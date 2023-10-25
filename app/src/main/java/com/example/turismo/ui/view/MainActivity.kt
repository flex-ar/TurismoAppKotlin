package com.example.turismo.ui.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.turismo.R
import com.example.turismo.data.PlacesRepository
import com.example.turismo.databinding.ActivityMainBinding
import com.example.turismo.domain.Place
import com.example.turismo.ui.adapter.PlacesAdapter
import com.example.turismo.ui.utils.PopupManager
import com.example.turismo.ui.utils.TrackerService
import com.example.turismo.ui.utils.hasLocationPermission
import com.example.turismo.ui.utils.requestLocationPermission
import com.example.turismo.ui.viewmodel.PlacesViewModel
import com.example.turismo.ui.viewmodel.PlacesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var placesAdapter: PlacesAdapter
  private lateinit var binding: ActivityMainBinding
  private lateinit var trackerService: TrackerService
  private val viewModel: PlacesViewModel by viewModels { PlacesViewModelFactory(PlacesRepository) }
  private var isOrdered = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    if (!hasLocationPermission()) {
      requestLocationPermission(this) {
        Toast.makeText(this, "Por favor acepte los permisos", Toast.LENGTH_LONG).show()
      }
      return
    }

    placesAdapter = PlacesAdapter() { navigateTo(it) }
    binding.recycler.adapter = placesAdapter

    trackerService = TrackerService(this) { trackerServiceCallback(it) }

    binding.btnMap.setOnClickListener {
      val intent = Intent(this, MapActivity::class.java)
      startActivity(intent)
    }

    binding.btnSort.setOnClickListener { showMenu(it, R.menu.popup_menu) }

    trackerService.startLocationUpdates()
    subscribeToObservable()
  }

  override fun onPause() {
    super.onPause()
    trackerService.removeLocationUpdates()
  }

  private fun trackerServiceCallback(location: Location) {
    viewModel.updateDistances(location)
    val place = viewModel.getClosestPlace()
    if (place.distance < 0.4 && place.activateAudio) {
      viewModel.setAudioOn(place.index)
      if (!PopupManager.isActive) {
        PopupManager.showPopup(this, place.audio)
      }
    }
    if (isOrdered) {
      viewModel.sortByDistance()
    }
  }

  private fun subscribeToObservable() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.placesFlow.collectLatest {
          placesAdapter.updatePlaceList(it)
        }
      }
    }
  }

  private fun showMenu(v: View, @MenuRes menuRes: Int) {
    val popup = PopupMenu(this, v)
    popup.menuInflater.inflate(menuRes, popup.menu)
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.menu_option_default -> {
          viewModel.sortByIndex()
          isOrdered = false
          true
        }

        R.id.menu_option_distance -> {
          viewModel.sortByDistance()
          isOrdered = true
          true
        }

        else -> false
      }
    }

    popup.show()
  }

  private fun navigateTo(place: Place) {
    viewModel.selectPlace(place)
    val intent = Intent(this, DetailActivity::class.java)
    startActivity(intent)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      0 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
      } else {
        Toast.makeText(this, "Tiene que aceptar los permisos!!", Toast.LENGTH_LONG).show()
      }

      else -> return
    }
  }
}