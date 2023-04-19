package com.example.justdrawit.ui

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.example.justdrawit.databinding.ActivityMainBinding
import com.google.android.material.slider.RangeSlider
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // buttons
        binding.btnBack.setOnClickListener { binding.drawView.back() }
        binding.btnForward.setOnClickListener { binding.drawView.forward() }
        binding.btnColors.setOnClickListener { showColorPickerDialog() }
        binding.btnSettings.setOnClickListener { binding.drawView.back() }
        binding.btnTools.setOnClickListener {
            if (binding.rangeSlider.visibility == View.VISIBLE)
                binding.rangeSlider.visibility = View.GONE
            else binding.rangeSlider.visibility = View.VISIBLE
        }

        //set the range of the RangeSlider
        binding.rangeSlider.valueFrom = 0.0f
        binding.rangeSlider.valueTo = 100.0f
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        binding.rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            binding.drawView.setStrokeWidth(
                value.toInt()
            )
        })

        val vto: ViewTreeObserver = binding.drawView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.drawView.measuredWidth
                val height = binding.drawView.measuredHeight
                binding.drawView.init(height, width)
            }
        })

    }

    private fun showColorPickerDialog() {
        val pickerDialog = ColorPickerDialog.Builder(this)
            .setPreferenceName("MyColorPickerDialog")
            .attachAlphaSlideBar(false) // the default value is true.
            .attachBrightnessSlideBar(false) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
        val alertDialog = pickerDialog.show()
        pickerDialog.colorPickerView.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                binding.drawView.setColor(color)
                if (fromUser) alertDialog.dismiss()
            }
        })
    }

}