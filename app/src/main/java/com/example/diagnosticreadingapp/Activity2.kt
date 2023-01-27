package com.example.diagnosticreadingapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Activity2 : AppCompatActivity() {
    private lateinit var rGroup: RadioGroup

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("guitar", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        //Tying bindings from xml to layout
        val imageAnalyzeView = findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val imageAnalyze = this.intent.data
        val layoutPage2 = findViewById<ConstraintLayout>(R.id.layout_page_2)

        rGroup = findViewById(R.id.rGroup)
        Log.d("guitar imageUri", imageAnalyze.toString())
        imageAnalyzeView.setImageURI(imageAnalyze)

        //Analysis image now set, add dispensable ImageView to which I can add the draggable crossHair to
        //This is the initial overlay
        val crossHairImageView = ImageView(this)
        crossHairImageView.setImageResource(R.drawable.baseline_gps_not_fixed_24)
        layoutPage2.addView(crossHairImageView)

        rGroup.setOnCheckedChangeListener { rGroup, _ ->
            Log.d("guitar changed radio", rGroup.checkedRadioButtonId.toString())

        }

         //This registers change to radio button
        crossHairImageView.setOnTouchListener { v, event ->
            when (event.action){
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.x
                    v.y = event.y
                    Log.d("guitar location x", v.x.toString())
                    Log.d("guitar location y", v.y.toString())
                }
            }

            true
        }

        submitAnalyzeButton.setOnClickListener {
            val radioType: String = if  (rGroup.checkedRadioButtonId.toString() == R.id.patientSamples.toString()){
                "patient"
            }else if (rGroup.checkedRadioButtonId.toString() == R.id.negativeControls.toString()){
                "negative"
            }else if (rGroup.checkedRadioButtonId.toString() == R.id.positiveControls.toString()){
                "positive"
            }else{
                "none"
            }
            val bitmap = Bitmap.createBitmap(layoutPage2.width, layoutPage2.height, Bitmap.Config.ARGB_8888)
            Log.d("guitar width", crossHairImageView.width.toString())
            Log.d("guitar x", crossHairImageView.x.toString())
            val canvas = Canvas(bitmap)
            layoutPage2.draw(canvas)
            //TODO: Expand the  following to include an average area
            val color = bitmap.getPixel(crossHairImageView.x.toInt()+(crossHairImageView.width/2), crossHairImageView.y.toInt()+(crossHairImageView.height/2))
            val tempX = 0
            val tempY = 0
            for (i in tempX-1..tempX+1){
                var crossX = crossHairImageView.x.toInt()+(crossHairImageView.width/2)+i
                for (j in tempY-1..tempY+1){
                    var crossY = crossHairImageView.y.toInt()+(crossHairImageView.height/2)
                    var color = bitmap.getPixel(crossX,crossY)
                    Log.d("guitar RGB OUT", Color.green(color).toString())
                }

            }

            val redColor = Color.red(color)
            val greenColor = Color.green(color)
            val blueColor = Color.blue(color)

            Log.d("guitar submit",radioType+crossHairImageView.x.toString())
            Log.d("guitar submit red color", redColor.toString())
            Log.d("guitar submit green color", greenColor.toString())
            Log.d("guitar submit blue color", blueColor.toString())
            if (crossHairImageView.isEnabled){
                layoutPage2.removeView(crossHairImageView)
            }

            crossHairImageView.setImageResource(R.drawable.baseline_gps_not_fixed_24)
            layoutPage2.addView(crossHairImageView)

        }
    }


    fun whichRadio(view: View) {
        // This is called from xml, do not delete or remove view
        val individualRadio = rGroup.checkedRadioButtonId
        Log.d("guitar which radio", individualRadio.toString())
    }
}






