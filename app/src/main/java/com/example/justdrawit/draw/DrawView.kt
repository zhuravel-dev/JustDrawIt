package com.example.justdrawit.draw

import android.app.Application
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.justdrawit.application.JustDrawItApplication
import kotlin.math.abs

class DrawView(c: Context, attributeSet: AttributeSet) : View(c, attributeSet) {

    private var mPaint: Paint = Paint()
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mTool: Tool? = null
    private var currentColor = Color.BLACK
    private var brushStrokeWidth = 20
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var mPath = Path()
    private val TOUCH_TOLERANCE = 4f
    private var mX = 0f
    private var mY = 0f

    val drawViewModel: DrawViewModel by lazy {
        ViewModelProvider(context.applicationContext as JustDrawItApplication, ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)).get(DrawViewModel::class.java)
    }

    private var mDetector: ScaleGestureDetector? = null

    init {
        if (drawViewModel.mDetector == null) {
            mDetector = ScaleGestureDetector(context, ScaleListener())
            drawViewModel.mDetector = mDetector
        } else {
            mDetector = drawViewModel.mDetector
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mBitmap == null) return
        canvas.save()
        val backgroundColor = Color.WHITE
        mCanvas?.drawColor(backgroundColor)
        canvas.scale(drawViewModel.viewModelScaleFactor, drawViewModel.viewModelScaleFactor)
        for (brushStrokes in drawViewModel.brushStrokeView) {
            mPaint.color = brushStrokes.color
            mPaint.strokeWidth = brushStrokes.brushStrokeWidth.toFloat()
            when (mTool) {
                Tool.Pencil -> mCanvas?.drawPath(brushStrokes.path, mPaint)
                Tool.Brush -> mCanvas?.drawPath(brushStrokes.path, mPaint)
                else -> mCanvas?.drawPath(brushStrokes.path, mPaint)
            }
        }
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mBitmap = bitmap
        mCanvas = Canvas(bitmap)
    }

    init {
        //the below methods smoothens the drawings of the user
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = currentColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        //0xff=255 in decimal
        mPaint.alpha = 0xff
    }

    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)?.also {
            mCanvas = Canvas(it)
        }
        currentColor = Color.BLACK
        brushStrokeWidth = drawViewModel.strokeWidth.toInt()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val brushstroke = Brushstroke(currentColor, brushStrokeWidth, mPath)
        drawViewModel.brushStrokeView.addAll(listOf(brushstroke))

        //finally remove any curve or line from the path
        mPath.reset()
        //this methods sets the starting point of the line being drawn
        mPath.moveTo(x, y)
        //save the current coordinates of the finger
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        mDetector?.onTouchEvent(event)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            drawViewModel.viewModelScaleFactor *= detector.scaleFactor
            drawViewModel.viewModelScaleFactor = Math.max(0.1f, Math.min(drawViewModel.viewModelScaleFactor, 10.0f))
            invalidate()
            return true
        }
    }

    //buttons
    fun back() {
        if (drawViewModel.brushStrokeView.isNotEmpty()) {
            val undoBrushstroke = drawViewModel.brushStrokeView.removeAt(drawViewModel.brushStrokeView.size - 1)
            drawViewModel.brushStrokeStorage.add(undoBrushstroke)
            invalidate()
        }
    }

    fun forward() {
        if (drawViewModel.brushStrokeStorage.isNotEmpty()) {
            val forwardBrushstroke = drawViewModel.brushStrokeStorage[drawViewModel.brushStrokeStorage.lastIndex]
            drawViewModel.brushStrokeView.add(forwardBrushstroke)
            invalidate()
        }
    }

    fun setColor(color: Int) {
        currentColor = color
    }

    fun setStrokeWidth(width: Int) {
        drawViewModel.strokeWidth = width.toFloat()
        brushStrokeWidth = width
    }

    fun save() : Bitmap? {
            return mBitmap
        }
}