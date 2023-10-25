package com.example.turismo.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.turismo.databinding.PopupLayoutBinding
import com.example.turismo.domain.Place

object PopupManager {
  var isActive = false;

  fun showPopup(context: Context, place: Place) {
    isActive = true
    val dialogBuilder = AlertDialog.Builder(context)
    val binding = PopupLayoutBinding.inflate(LayoutInflater.from(context))
    dialogBuilder.setView(binding.root)
    val alertDialog = dialogBuilder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    val mediaPlayer = MediaPlayer.create(context, place.audio)
    var isThreadRunning = true

    binding.title.text = place.title

    binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
      if (isChecked) {
        when (checkedId) {
          binding.playButton.id -> {
            mediaPlayer.start()
            binding.playButton.visibility = View.GONE
            binding.pauseButton.visibility = View.VISIBLE
          }

          binding.pauseButton.id -> {
            mediaPlayer.pause()
            binding.pauseButton.visibility = View.GONE
            binding.playButton.visibility = View.VISIBLE
          }
        }
      }
    }

    binding.slider.valueFrom = 0f
    binding.slider.valueTo = mediaPlayer.duration.toFloat()

    binding.slider.addOnChangeListener { _, value, fromUser ->
      if (fromUser) mediaPlayer.seekTo(value.toInt())
    }

    val handler = object : Handler() {
      override fun handleMessage(msg: Message) {
        val currentPosition = msg.what
        binding.slider.value = currentPosition.toFloat()
      }
    }

    Thread(Runnable {
      while (isThreadRunning) {
        try {
          if (!isThreadRunning) break
          val msg = Message()
          if (mediaPlayer.currentPosition >= 0 && mediaPlayer.currentPosition <= mediaPlayer.duration) {
            msg.what = mediaPlayer.currentPosition
            handler.sendMessage(msg)
          }
          Thread.sleep(1000)
        } catch (e: InterruptedException) {
          Log.d("Thread", e.message.toString())
        }
      }
    }).start()

    binding.buttonClose.setOnClickListener {
      mediaPlayer.release()
      isThreadRunning = false
      isActive = false
      alertDialog.dismiss()
    }
    alertDialog.show()
  }
}