package com.example.justdrawit.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null && it.data?.data != null) {
                    val bmp = binding.drawView.save()
                    it.data?.data?.let { uri ->
                        contentResolver.openOutputStream(uri)?.use { op ->
                            bmp?.compress(Bitmap.CompressFormat.PNG, 100, op)
                        }
                    }
                } else {
                    Toast.makeText(this, "Some error ocured", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // buttons
        binding.btnBack.setOnClickListener { binding.drawView.back() }
        binding.btnForward.setOnClickListener { binding.drawView.forward() }
        binding.btnColors.setOnClickListener { showColorPickerDialog() }
        binding.btnTools.setOnClickListener {
            showPopup()
        }
        binding.btnSettings.setOnClickListener { showSettings(it) }

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

    private fun showSettings(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup_settings)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item1_save -> {
                    saveImage("image", startActivityForResult)
                    //it.setIcon(R.drawable.ic_baseline_save_24)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun saveImage(fileName: String, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // file type
            intent.type = "image/*"
            // file name
            intent.putExtra(Intent.EXTRA_TITLE, fileName)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            )
            launcher.launch(intent)
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