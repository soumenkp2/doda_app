package com.example.doda

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ScaleGestureDetector



var matrix: Matrix = Matrix()

val NONE = 0
val DRAG = 1
val ZOOM = 2
val CLICK = 3
var mode = NONE

var last = PointF()
var start = PointF()
var minScale = 1f
var maxScale = 4f
//var m: FloatArray = TODO();
var redundantXSpace = 0f
var redundantYSpace:kotlin.Float = 0f
var width = 0f
var height:kotlin.Float = 0f
var saveScale = 1f
var right =
    0f
var bottom:kotlin.Float = 0f
var origWidth:kotlin.Float = 0f
var origHeight:kotlin.Float = 0f
var bmWidth:kotlin.Float = 0f
var bmHeight:kotlin.Float = 0f

var mScaleDetector: ScaleGestureDetector? = null
@SuppressLint("StaticFieldLeak")
//var context: Context? = null

class MarkerView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : super(context)

    // constructor 2
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    // constructor 3
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )






    // list of already drawn points
    val listOfDrawnPoints = mutableListOf<Point>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        listOfDrawnPoints.forEach {
            val paint = getPaint()
            val density = this.getResources().getDisplayMetrics().density
            val radius = 10 * density
            canvas!!.drawCircle(it.x.toFloat(), it.y.toFloat(), radius, paint)
        }
    }

    // define the paint
    fun getPaint(): Paint {
        val paint = Paint()
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        val density: Float = this.getResources().getDisplayMetrics().density
        paint.strokeWidth = 5 * density
        return paint
    }

    fun markPoint(x: Int, y: Int) {
        listOfDrawnPoints.add(Point(x, y))
        invalidate()
    }






}