package com.example.turismo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.example.turismo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var recyclerAdapter: PlacesAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    recyclerAdapter = PlacesAdapter(places) { navigateTo(it) }
    binding.recycler.adapter = recyclerAdapter
    binding.btnMap.setOnClickListener {
      val intent = Intent(this, MapActivity::class.java)
      startActivity(intent)
    }

    binding.btnSort.setOnClickListener { showMenu(it, R.menu.popup_menu) }
  }

  private fun showMenu(v: View, @MenuRes menuRes: Int) {
    val popup = PopupMenu(this, v)
    popup.menuInflater.inflate(menuRes, popup.menu)

    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.menu_option_default -> {
          recyclerAdapter.sortByIndex()
          true
        }

        R.id.menu_option_distance -> {
          recyclerAdapter.sortByDistance()
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
}