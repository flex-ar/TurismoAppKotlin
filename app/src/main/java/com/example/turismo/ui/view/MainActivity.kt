package com.example.turismo.ui.view

import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.turismo.ui.utils.TrackerService
import com.example.turismo.data.PlacesRepository
import com.example.turismo.databinding.ActivityMainBinding
import com.example.turismo.domain.Place
import com.example.turismo.ui.adapter.PlacesAdapter
import com.example.turismo.ui.utils.hasLocationPermission
import com.example.turismo.ui.utils.requestLocationPermission
import com.example.turismo.ui.viewmodel.PlacesViewModel
import com.example.turismo.ui.viewmodel.PlacesViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var placesAdapter: PlacesAdapter
  private lateinit var binding: ActivityMainBinding
  private val viewModel: PlacesViewModel by viewModels { PlacesViewModelFactory(PlacesRepository) }
  private val trackerService = TrackerService()

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

    binding.btnMap.setOnClickListener {
      val intent = Intent(this, MapActivity::class.java)
      startActivity(intent)
    }

    binding.btnSort.setOnClickListener { showMenu(it, R.menu.popup_menu) }

    subscribeToObservable()
  }

  private fun subscribeToObservable() {
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.placesFlow.collectLatest {
          placesAdapter.updatePlaceList(it)
        }
      }
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        while (true) {
          val result = trackerService.getUserLocation(this@MainActivity, hasLocationPermission())
          if (result != null) {
            viewModel.updateDistances(result)
          }
          delay(2000)
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
          true
        }

        R.id.menu_option_distance -> {
          viewModel.sortByDistance()
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
        // lanzar la activity
        Toast.makeText(this, "Reiniciar", Toast.LENGTH_LONG).show()
      } else {
        Toast.makeText(this, "Tiene que aceptar los permisos!!", Toast.LENGTH_LONG).show()
      }

      else -> return
    }
  }
}