package com.example.turismo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.turismo.databinding.ViewPlaceItemBinding
import com.example.turismo.domain.Place

class PlacesAdapter(
  private val placeClickListener: (Place) -> Unit
) :
  RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {
  private var places: List<Place> = emptyList()

  fun updatePlaceList(newPlaces: List<Place>) {
    places = newPlaces
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
    val binding = ViewPlaceItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )

    return PlaceViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
    val place = places[position]
    holder.bind(place)
    holder.itemView.setOnClickListener { placeClickListener(place) }
  }

  override fun getItemCount(): Int = places.size

  class PlaceViewHolder(private val binding: ViewPlaceItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(place: Place) {
      binding.placeTitle.text = place.title
      binding.placeImage.setImageResource(place.image)
      binding.placeAddress.text = place.address
      binding.placeDistance.text = "${place.distance} km"
    }
  }
}