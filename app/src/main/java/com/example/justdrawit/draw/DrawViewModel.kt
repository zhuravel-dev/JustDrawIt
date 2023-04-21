package com.example.justdrawit.draw
import androidx.lifecycle.ViewModel

class DrawViewModel : ViewModel() {

    var brushStrokeView = mutableListOf<Brushstroke>()
    val brushStrokeStorage = mutableListOf<Brushstroke>()

}