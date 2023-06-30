package com.example.turismo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.turismo.databinding.ViewPlaceItemBinding

class PlacesAdapter(
  private val places: MutableList<Place>,
  private val placeClickListener: (Place) -> Unit
) :
  RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ViewPlaceItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )

    return ViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val place = places[position]
    holder.bind(place)
    holder.itemView.setOnClickListener { placeClickListener(place) }
  }

  override fun getItemCount(): Int = places.size

  fun sortByIndex() {
    places.sortBy { it.index }
    notifyDataSetChanged()
  }

  fun sortByDistance() {
    places.sortBy { it.distance }
    notifyDataSetChanged()
  }

  class ViewHolder(private val binding: ViewPlaceItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(place: Place) {
      binding.placeTitle.text = place.title
      binding.placeImage.setImageResource(place.image)
      binding.placeAddress.text = place.address
      binding.placeDistance.text = "${place.distance} km"
    }
  }
}