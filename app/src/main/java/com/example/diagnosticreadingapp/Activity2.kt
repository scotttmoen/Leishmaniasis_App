package com.example.diagnosticreadingapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.color
import androidx.core.text.italic
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import kotlin.math.roundToInt

class Activity2 : AppCompatActivity() {
    private lateinit var rGroup: RadioGroup
    private var radioType: String = ""
    private lateinit var crossHairImageView: ImageView
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("guitar", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        //Zero out the pos, neg, and patient samples each time a new analysis image is initiated.
        val posValueList  = mutableListOf<Int>()
        val negValueList = mutableListOf<Int>()
        var patValue = 0

        //Tying bindings from xml to layout
        val imageAnalyzeView = findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val imageAnalyze = this.intent.data
        val layoutPage2 = findViewById<ConstraintLayout>(R.id.layout_page_2)

        //Set image to analysis window
        rGroup = findViewById(R.id.rGroup)
        imageAnalyzeView.setImageURI(imageAnalyze)

        //Analysis image now set, add dispensable ImageView to which I can add the draggable crossHair to
        //This is the initial overlay
        crossHairImageView = ImageView(this)
        createCrossHair(crossHairImageView, layoutPage2)

        rGroup.setOnCheckedChangeListener { rGroup, _ ->
            createCrossHair(crossHairImageView, layoutPage2)

        }

         //This registers change to radio button
        crossHairImageView.setOnTouchListener { v, event ->
            when (event.action){
                MotionEvent.ACTION_MOVE -> {

                    v.y = event.rawY - v.height
                    v.x = event.rawX - v.width/2

                    if (v.x<0){
                        Log.d("guitar pad", "x < 0")
                    }

                    Log.d("guitar location x", v.x.toString())
                    Log.d("guitar location y", v.y.toString())
                }
            }
            true
        }

        submitAnalyzeButton.setOnClickListener {

            val bitmap = Bitmap.createBitmap(layoutPage2.width, layoutPage2.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            layoutPage2.draw(canvas)

            val greenColor = getAverageColor(bitmap, crossHairImageView)

            when (radioType){
                "patient" -> {
                    Log.d("guitar", "patient")
                    //check if at least one posValueList and one negValueList before calculating
                    if (posValueList.isEmpty() || negValueList.isEmpty()){
                        Log.d("guitar","restart1")
                        Snackbar.make(layoutPage2, "You must have positive and negative samples to analyze patient samples.", Snackbar.LENGTH_INDEFINITE).apply{
                            setAction("DISMISS"){
                                Log.d("guitar","restart2")

                            }
                        }.show()

                    }

                    patValue = greenColor.toInt()
                    val patientNormalized = (patValue-negValueList.average())/(posValueList.average()-negValueList.average())
                    var myCustomString = SpannableStringBuilder()
                    Log.d("guitar patient normalized", patientNormalized.toString())
                    if (patientNormalized>0){
                        val reportNumber = (patientNormalized * 100).roundToInt()
                        //popup with normalized patient value
                        myCustomString = SpannableStringBuilder()
                            .append("The chance of the patient having ")
                            .italic{append ("Leishmaniasis ")}
                            .append("is ")
                            .color(Color.RED, {append("${reportNumber}%")})
                    }else{
                        myCustomString = SpannableStringBuilder()
                            .color(Color.RED) { append("Patient values are not within your positive and negative controls.") }
                    }


                    Snackbar.make(layoutPage2,myCustomString , Snackbar.LENGTH_INDEFINITE).apply{
                        setAction("DISMISS"){
                            Log.d("guitar","patient results")

                        }
                    }.show()
                }
                "positive" -> {
                    Log.d("guitar", "positive")
                    posValueList.add(greenColor.toInt())
                }
                "negative" -> {
                    crossHairImageView.setColorFilter(Color.RED)
                    Log.d("guitar", "negative")
                    negValueList.add(greenColor.toInt())
                }
            }

            Log.d("guitar submit",radioType+crossHairImageView.x.toString())
            Log.d("guitar submit green color", greenColor.toString())
            Log.d("guitar submit pos", posValueList.toString())
            Log.d("guitar submit neg", negValueList.toString())

            //Deselect rGroup
            rGroup.clearCheck()
        }
    }

    private fun createCrossHair(
        crossHairImageView: ImageView,
        layoutPage2: ConstraintLayout
    ) {
        try {
            layoutPage2.removeView(crossHairImageView)
        }
        catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(applicationContext,"Some error", Toast.LENGTH_SHORT).show()
        }
        crossHairImageView.setImageResource(R.drawable.baseline_gps_not_fixed_24)
        layoutPage2.addView(crossHairImageView)
        crossHairImageView.x = (layoutPage2.width / 2 - (crossHairImageView.width / 2)).toFloat()
        crossHairImageView.y = layoutPage2.height * 0.7.toFloat()

        crossHairImageView.isVisible = false
    }

    private fun getAverageColor(
        bitmap: Bitmap,
        crossHairImageView: ImageView
    ): Double {
        //Setup to only return GREEN
        val tempX = 0
        val tempY = 0
        val tempListGreen = mutableListOf<Int>()
        val tempListRed = mutableListOf<Int>()
        val tempListBlue = mutableListOf<Int>()
        for (i in tempX - 1..tempX + 1) {
            val crossX = crossHairImageView.x.toInt() + (crossHairImageView.width / 2) + i
            for (j in tempY - 1..tempY + 1) {
                val crossY = crossHairImageView.y.toInt() + (crossHairImageView.height / 2)
                val color = bitmap.getPixel(crossX, crossY)
                Log.d("guitar Raw Green OUT", Color.green(color).toString())
                tempListGreen.add(Color.green(color))
                tempListBlue.add(Color.blue(color))
                tempListRed.add(Color.red(color))
            }
        }
        Log.d ("guitar temp average green", tempListGreen.average().toString())
        return tempListGreen.average()
    }
    fun whichRadio(view: View) {
        // This is called from xml, do not delete or remove view
        crossHairImageView.isVisible = true
        //val individualRadio = rGroup.checkedRadioButtonId
        if  (rGroup.checkedRadioButtonId.toString() == R.id.patientSamples.toString()){
            radioType = "patient"
            crossHairImageView.setColorFilter(Color.MAGENTA)
        }else if (rGroup.checkedRadioButtonId.toString() == R.id.negativeControls.toString()){
            radioType = "negative"
            crossHairImageView.setColorFilter(Color.RED)
        }else if (rGroup.checkedRadioButtonId.toString() == R.id.positiveControls.toString()){
            radioType = "positive"
            crossHairImageView.setColorFilter(Color.GREEN)
        }else{
            radioType = "none"
        }
    }
}






