package com.example.justdrawit.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


class DrawView(c: Context, attributeSet: AttributeSet) : View(c, attributeSet) {

    private var mPaint: Paint = Paint()
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private lateinit var mPath: Path
    private var mTool: Tool? = null
    private val paths = mutableListOf<Brushstroke>()
    private var currentColor = Color.BLACK
    private var brushStrokeWidth = 0
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    private val TOUCH_TOLERANCE = 4f
    private var mX = 0f
    private var mY = 0f

    override fun onDraw(canvas: Canvas) {
        if (mBitmap == null) return
        canvas.save()
        val backgroundColor = Color.WHITE
        mCanvas?.drawColor(backgroundColor)

        for (brushStrokes in paths) {
            mPaint.color = brushStrokes.color
            mPaint.strokeWidth = brushStrokes.brushStrokeWidth.toFloat()
            when (mTool) {
                Tool.Pencil -> mCanvas?.drawPath(brushStrokes.path, mPaint)
                else -> mCanvas?.drawPath(brushStrokes.path, mPaint)
            }
        }
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)?.also {
            mCanvas = Canvas(it)
        }
        currentColor = Color.BLACK
        brushStrokeWidth = 20
    }

    fun back() {
        if (paths.size != 0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val brushstroke = Brushstroke(currentColor, brushStrokeWidth, mPath)
        paths.add(brushstroke)

        //finally remove any curve or line from the path
        mPath.reset()
        //this methods sets the starting point of the line being drawn
        mPath.moveTo(x, y)
        //we save the current coordinates of the finger
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
        return true
    }
}