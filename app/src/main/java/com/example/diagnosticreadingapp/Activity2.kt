package com.example.diagnosticreadingapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Activity2 : AppCompatActivity() {
    private lateinit var rGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("guitar", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        val imageAnalyzeView = findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val imageAnalyze = this.intent.data

        //Before this is basic, this will be added to imageview that is added to the layout
        var crossHairIcon = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.ic_launcher_background  //<-- name of xml file in res folder
        )


        rGroup = findViewById(R.id.rGroup)
        Log.d("guitar imageUri", imageAnalyze.toString())
        imageAnalyzeView.setImageURI(imageAnalyze)

         //This registers change to radio button

        imageAnalyzeView.setOnTouchListener { v, event ->
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
            var radioType = ""
            if  (rGroup.checkedRadioButtonId.toString() == R.id.patientSamples.toString()){
                radioType = "patient"
            }else if (rGroup.checkedRadioButtonId.toString() == R.id.negativeControls.toString()){
                radioType = "negative"
            }else if (rGroup.checkedRadioButtonId.toString() == R.id.positiveControls.toString()){
                radioType = "positive"
            }else{
                radioType = "none"
            }
            Log.d("guitar submit",radioType)

        }
    }


    fun whichRadio(view: View) {

        val individualRadio = rGroup.checkedRadioButtonId
        val selectedString = individualRadio
        Log.d("guitar which radio", individualRadio.toString())
    }
}




