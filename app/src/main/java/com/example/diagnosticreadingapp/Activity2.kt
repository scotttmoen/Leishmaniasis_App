package com.example.diagnosticreadingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class Activity2 : AppCompatActivity() {
    private val rGroup = findViewById<RadioGroup>(R.id.rGroup)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("guitar", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)


        findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val imageAnalyze = this.intent.getStringExtra("photoFile")
        Log.d("guitar", imageAnalyze.toString())

        rGroup?.checkedRadioButtonId
        //val uri = Uri.parse(imageAnalyze)
        //imageAnalyzeView.setImageURI(uri)

         //This registers change to radio button
        rGroup?.setOnCheckedChangeListener { rGroup, _ ->
            Log.d("guitar", rGroup.toString())

        }
        submitAnalyzeButton.setOnClickListener {
            Log.d("guitar", rGroup.toString())

        }
    }

    fun whichRadio() {
        Log.d("guitar", "General press.")
        val individualRadio = rGroup?.checkedRadioButtonId
        val selectedRadio = individualRadio?.let { rGroup?.findViewById<RadioGroup>(it) }
        val selectedString = selectedRadio?.checkedRadioButtonId
        Log.d("guitar", selectedString.toString())

    }
}




