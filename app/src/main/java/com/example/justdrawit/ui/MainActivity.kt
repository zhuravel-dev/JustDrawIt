package com.example.justdrawit.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.justdrawit.R
import com.example.justdrawit.databinding.ActivityMainBinding
import com.example.justdrawit.databinding.PopupLayoutBinding
import com.google.android.material.slider.RangeSlider
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var popupBinding: PopupLayoutBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var rangeSlider: RangeSlider

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
            showPopup()
        }


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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("stroke_width", rangeSlider.values[0].toInt())
    }


    private fun showPopup() {
        popupBinding = PopupLayoutBinding.inflate(layoutInflater)
        val popupView = popupBinding.root
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupWindow.animationStyle = androidx.appcompat.R.style.Animation_AppCompat_Dialog
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        setupRangeSlider()
    }

    private fun setupRangeSlider(){
        //popupBinding = PopupLayoutBinding.inflate(layoutInflater)
        rangeSlider = popupBinding.rangeSlider
        rangeSlider.valueFrom = 0.0f
        rangeSlider.valueTo = 100.0f
        rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            binding.drawView.setStrokeWidth(slider.values[0].toInt()) })
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