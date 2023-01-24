package com.example.diagnosticreadingapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class Activity2 : AppCompatActivity() {
    private lateinit var rGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("guitar", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        val imageAnalyzeView = findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val imageAnalyze = this.intent.data

        rGroup = findViewById(R.id.rGroup)
        Log.d("guitar imageUri", imageAnalyze.toString())

        //rGroup.checkedRadioButtonId

        imageAnalyzeView.setImageURI(imageAnalyze)

         //This registers change to radio button
        rGroup.setOnCheckedChangeListener { rGroup, _ ->
            Log.d("guitar changed radio", rGroup.toString())

        }
        submitAnalyzeButton.setOnClickListener {
            Log.d("guitar radio submit", rGroup.toString())
            //whichRadio()

        }
    }


    fun whichRadio(view: View) {

        val individualRadio = rGroup.checkedRadioButtonId
        Log.d("guitar", "General press1.")
        ///THis line
        //val selectedRadio = individualRadio.let { rGroup.findViewById<RadioGroup>(it) }
        Log.d("guitar", "General press2.")
        //val selectedString = selectedRadio?.checkedRadioButtonId
        val selectedString = individualRadio
        Log.d("guitar which radio", individualRadio.toString())
    }
}




