package com.example.justdrawit.draw
import android.view.ScaleGestureDetector
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {
    var brushStrokeView = mutableListOf<Brushstroke>()
    val brushStrokeStorage = mutableListOf<Brushstroke>()

    var strokeWidth = 20.0f

    var mDetector: ScaleGestureDetector? = null
    var viewModelScaleFactor = 1f
}