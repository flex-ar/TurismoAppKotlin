package com.example.turismo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.turismo.R
import com.example.turismo.domain.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private var _places =
  listOf(
    Place(
      "Vivienda de Ernesto Romberg",
      R.drawable.wp1,
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
      2.0,
      "address",
      -46.44181409663365, -67.51755183602512,
      1
    ),
    Place(
      "Escuela N ° 14 “20 de Noviembre”",
      R.drawable.wp2,
      "description",
      3.0,
      "address",
      -46.43532992448658, -67.51938646683469,
      2
    ),
    Place(
      "Vivienda de la familia Maimo",
      R.drawable.wp3,
      "description",
      1.0,
      "address",
      -46.43731492713906, -67.51864649518306,
      3
    ),
    Place(
      "title 4",
      R.drawable.wp1,
      "description",
      7.0,
      "address",
      -46.439316469487316, -67.5495337329417,
      4
    ),
    Place(
      "title 5",
      R.drawable.wp2,
      "description",
      5.0,
      "address",
      -46.451144756221886, -67.53352631003189,
      5
    ),
    Place(
      "title 6",
      R.drawable.wp3,
      "description",
      6.0, "address",
      -46.455313615047636, -67.49829281286121,
      6
    ),
    Place(
      "title 7",
      R.drawable.wp1,
      "description",
      4.0,
      "address",
      -46.433092774032296, -67.52073605622193,
      7
    ),
  )

class PlacesViewModel : ViewModel() {
  fun getPlaces() = _places

  // State
  private val _placesFlow = MutableStateFlow(_places)
  val placesFlow = _placesFlow.asStateFlow()

  // Events
  fun sortByIndex() {
    _places = _places.sortedBy { it.index }
    _placesFlow.value = _places
  }

  fun sortByDistance() {
    _places = _places.sortedBy { it.distance }
    _placesFlow.value = _places
  }

//  fun updateDistances(currentLocation: Location) {
//    _placesFlow.value = _placesFlow.value.map { place ->
//      val locationPlace = Location("location place")
//      locationPlace.latitude = place.latitude
//      locationPlace.longitude = place.longitude
//      val distance = (currentLocation.distanceTo(locationPlace) / 1000).toDouble()
//      place.copy(distance = distance)
//    }
//  }
}
