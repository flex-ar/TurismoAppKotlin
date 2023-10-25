package com.example.turismo.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.turismo.data.PlacesRepository
import com.example.turismo.domain.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlacesViewModel(private val placesRepository: PlacesRepository) : ViewModel() {
  // State
  private val _placesFlow = MutableStateFlow(placesRepository.getPlaces())
  val placesFlow = _placesFlow.asStateFlow()
  private val _placeSelected = MutableStateFlow(placesRepository.getSelectedPlace())
  val placeSelected = _placeSelected.asStateFlow()

  // Events
  fun getClosestPlace() = placesRepository.getPlaces().sortedBy { it.distance }[0]

  fun setAudioOn(index: Int) {
    _placesFlow.update { placesRepository.setAudioOn(index) }
  }

  fun sortByIndex() {
    _placesFlow.update { placesRepository.sortByIndex() }
  }

  fun sortByDistance() {
    _placesFlow.update { placesRepository.sortByDistance() }
  }

  fun selectPlace(place: Place) {
    _placeSelected.update { placesRepository.setSelectedPlace(place) }
  }

  fun updateDistances(currentLocation: Location) {
    _placesFlow.update { placesRepository.updateDistances(currentLocation) }
  }
}

@Suppress("UNCHECKED_CAST")
class PlacesViewModelFactory(private val placesRepository: PlacesRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return PlacesViewModel(placesRepository) as T
  }
}
