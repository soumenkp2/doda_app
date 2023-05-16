package com.example.doda.Screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doda.DrawingInfo
import com.example.doda.MarkerView
import com.example.doda.Marker_Info
import com.example.doda.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.lang.Math.abs


class drawing : AppCompatActivity(), View.OnClickListener {

    private var drawing_name : String ?= null
    var x : Int = -1;
    var y : Int = -1;

    var lastEvent: FloatArray? = null
    var d = 0f
    var newRot = 0f
    private var isZoomAndRotate = false
    private var isOutSide = false
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1f
    private var xCoOrdinate = 0f
    private  var yCoOrdinate:kotlin.Float = 0f

    private lateinit var imageView: MarkerView
    private lateinit var marker_info : TextView

    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private var ev : MotionEvent? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    companion object {
        const val oldDeviceWidth = 1080f
        const val oldDeviceHeight = 1794f
    }

    val currentDeviceWidth by lazy {
        val currentResolution = windowManager.defaultDisplay
        currentResolution.width
    }

    val currentDeviceHeight by lazy {
        val currentResolution = windowManager.defaultDisplay
        currentResolution.height
    }

    var density: Float = 0f
    val threshold by lazy {
        15 * density
    }


    var firstTouch = 0
    var click = false

    var info_list = arrayListOf<String>()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        var b = intent.getStringExtra("name").toString()
        drawing_name = b
        //Toast.makeText(applicationContext,b,Toast.LENGTH_SHORT).show()

        // Initialize ImageView and set its OnClickListener
        imageView = findViewById(R.id.image_view)
        marker_info = findViewById(R.id.textView)
        //imageView.setOnClickListener(this)

        getData()
        Picasso.with(applicationContext).load(intent.getStringExtra("uri").toString()).into(imageView)

        //imageView.setOnTouchListener() { v, ev -> onTouchs(v, ev) }

        val handler = Handler()
        val delay: Long = 2000 // 1000 milliseconds == 1 second

        handler.postDelayed(object : Runnable {
            override fun run() {
                firstTouch = 0
                handler.postDelayed(this, delay)
            }
        }, delay)


        var mm : MotionEvent? = null


        imageView.setOnTouchListener(object : View.OnTouchListener {

            private val gestureDetector = GestureDetector(applicationContext, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    //Toast.makeText(applicationContext,"dt",Toast.LENGTH_SHORT).show()

                    click = true
                    firstTouch = 1

                    //Toast.makeText(applicationContext,"ondouble",Toast.LENGTH_SHORT).show()
                    //mm?.let { onTouchs(it,imageView) }
                    //Log.d("motion 2",click.toString())
                    //click = true

                    return true
                }

                // implement other callback methods like onFling, onScroll as necessary
            })

            override fun onTouch(v: View, m: MotionEvent): Boolean {

                mm = m
                gestureDetector.onTouchEvent(mm!!);
                //Log.d("motion 1",click.toString())
                //Toast.makeText(applicationContext,"ontouch",Toast.LENGTH_SHORT).show()

                if(click && firstTouch==2)
                {
                    onTouchs(m,imageView)
                    click = false
                    firstTouch = 0
                }
                else{
                    firstTouch++
                }

                val view: MarkerView = imageView as MarkerView
                view.bringToFront()
                viewTransformation(view, m)

                Log.d("mssg",HeatPointsOfImage.size.toString())
                if(m.x.let { touchMatchesHeatPoints(it.toInt(), m.y.toInt()) } == true)
                    {
                        //Toast.makeText(applicationContext,m.x.toInt().toString() + "," + m.y.toInt().toString(),Toast.LENGTH_SHORT).show()

                        for (i in HeatPointsOfImage.indices) {
                            val p = HeatPointsOfImage[i]
                            Log.d(i.toString(),p.x.toString()+p.y.toString()+info_list[i])
                            if(abs(p.x.toInt() - m.x.toInt()) <=20  && abs(p.y.toInt() - m.y.toInt()) <=20)
                            {
                                marker_info.text = info_list[i]
                                //Toast.makeText(applicationContext,info_list[i].toString() + i,Toast.LENGTH_SHORT).show()
                            }
                        }

                    }


//                if(firstTouch>1)
//                {
//                    //show_box()
//                    onTouchs(v,m)
//                    firstTouch=0
//
//                }
//                else
//                {
//                    x=-1
//                    y=-1
//
//                    if(ev?.x?.let { touchMatchesHeatPoints(it.toInt(), ev!!.y.toInt()) } == false)
//                    {
//                        Toast.makeText(applicationContext,x.toString() + "," + y.toString(),Toast.LENGTH_SHORT).show()
//                    }
//                    firstTouch++
//                }






                return true
            }
        })

        density = this.getResources().getDisplayMetrics().density







    }


    private fun getData()
    {
        val imagesRef = FirebaseDatabase.getInstance().reference.child("projects").child(drawing_name.toString()).child("Markers")
        imagesRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val imageData = dataSnapshot.getValue(Marker_Info::class.java)
                    imageData?.let {

                        info_list.add(it.info.toString())

                        it.y?.let { it1 -> it.x?.let { it2 -> Point(it2.toInt(), it1.toInt()) } }
                            ?.let { it2 -> HeatPointsOfImage.add(it2) }
                        it.y?.let { it1 -> it.x?.let { it2 -> imageView.markPoint(it2.toInt(), it1.toInt()) } }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })


//        val it = HeatPointsOfImage.listIterator()
//        while(it.hasNext())
//        {
//            val p = it
//
//        }


    }


    private fun viewTransformation(view: View, event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xCoOrdinate = view.x - event.rawX
                yCoOrdinate = view.y - event.rawY
                start[event.x] = event.y
                isOutSide = false
                mode = DRAG
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                d = rotation(event)
            }
            MotionEvent.ACTION_UP -> {
                isZoomAndRotate = false
                if (mode === DRAG) {
                    val x = event.x
                    val y = event.y
                }
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_OUTSIDE -> {
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (!isOutSide) {
                if (mode === DRAG) {
                    isZoomAndRotate = false
                    view.animate().x(event.rawX + xCoOrdinate).y(event.rawY + yCoOrdinate)
                        .setDuration(0).start()
                }
                if (mode === ZOOM && event.pointerCount == 2) {
                    val newDist1 = spacing(event)
                    if (newDist1 > 10f) {
                        val scale = newDist1 / oldDist * view.scaleX
                        view.scaleX = scale
                        view.scaleY = scale
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event)
                        view.rotation = (view.rotation + (newRot - d))
                    }
                }
            }
        }
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    val HeatPointsOfImage = mutableListOf<Point>()

    fun touchMatchesHeatPoints(xCord: Int, yCord: Int): Boolean {
        val heatPoints = HeatPointsOfImage
        return heatPoints.filter {
            // https://stackoverflow.com/questions/32755825/extrapolation-of-x-y-coordinates-from-one-screen-size-and-resolution-to-other-sc
            // This is just the distance between two points
            val tempX = it.x - xCord
            val tempY = it.y - yCord
            val xPosTemp = tempX * tempX
            val yPosTemp = tempY * tempY
            val dist = Math.sqrt((xPosTemp + yPosTemp).toDouble())

            x = it.x
            y=it.y

            dist <= threshold
        }.map {
            it
        }.isNotEmpty()
    }

    fun onTouchs(ev: MotionEvent, v : MarkerView): Boolean {

        val action = ev.action
        val evX = ev.x.toInt()
        val evY = ev.y.toInt()

        //HeatPointsOfImage.add(Point(evX,evY))

        when (action) {
            MotionEvent.ACTION_UP -> {
                v.markPoint(evX, evY)
                Toast.makeText(applicationContext,drawing_name.toString(),Toast.LENGTH_SHORT).show()
                val alertDialog = MarkerDialog(evX,evY,"",drawing_name.toString())
                alertDialog.show(supportFragmentManager, "fragment_alert")
            }
        }

        Log.d("click",evX.toString() + " , " + evY.toString())

        //Toast.makeText(this,evX.toString(),Toast.LENGTH_SHORT).show()

        return true
    }

    abstract class DoubleTouchListener: View.OnTouchListener
    {
        var lastClickTime: Long = 0
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleTouch(p0,p1)
            }
            lastClickTime = clickTime

            return true
        }

        abstract fun onDoubleTouch(v: View?, p1: MotionEvent?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

    abstract class DoubleClickListener : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}