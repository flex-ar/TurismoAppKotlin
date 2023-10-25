package com.example.turismo.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
  val title: String,
  val image: Int,
  val audio: Int,
  val activateAudio: Boolean,
  val description: String,
  val distance: Double,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val index: Int
): Parcelable