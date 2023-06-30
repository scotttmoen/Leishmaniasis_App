package com.scott.diagnosticreadingapp

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
        Log.d("dog", "Second activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        //Zero out the pos, neg, and patient samples each time a new analysis image is initiated.
        var posValueList = mutableListOf<Int>()
        var negValueList = mutableListOf<Int>()
        var patValue: Int

        //Tying bindings from xml to layout
        val imageAnalyzeView = findViewById<ImageView>(R.id.imageViewAnalyze)
        val submitAnalyzeButton = findViewById<Button>(R.id.submitAnalyze)
        val eraseButton = findViewById<Button>(R.id.eraseValues)
        val imageAnalyze = this.intent.data
        val imageConstraint = findViewById<ConstraintLayout>(R.id.image_constraint)

        //Set image to analysis window
        this.rGroup = this.findViewById(R.id.rGroup)
        imageAnalyzeView.setImageURI(imageAnalyze)

        //Analysis image now set, add dispensable ImageView to which I can add the draggable crossHair to
        //This is the initial overlay
        crossHairImageView = ImageView(this)
        createCrossHair(crossHairImageView, imageConstraint, imageAnalyzeView)

        this.rGroup.setOnCheckedChangeListener { _, _ ->
            createCrossHair(crossHairImageView, imageConstraint, imageAnalyzeView)

        }


        //This registers change to radio button
        crossHairImageView.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_MOVE -> {
//
                    v.y = event.rawY - 400
                    v.x = event.rawX

//                    Log.d("dog iConst x at v", imageConstraint.x.toString())
//                    Log.d("dog iConst y at v", imageConstraint.y.toString())
//                    Log.d("dog imageAView max height", " " + imageAnalyzeView.measuredHeight)
//                    Log.d("dog imageAView max width", " " + imageAnalyzeView.measuredWidth)
//                    Log.d("dog rawEvent x", " " + event.rawX)
//                    Log.d("dog rawEvent y", " " + event.rawY)

                }

                MotionEvent.ACTION_UP -> {
                    Log.d("up dog rawEvent x", " " + event.rawX)
                    if ((crossHairImageView.x) > (imageAnalyzeView.measuredWidth - crossHairImageView.width)) {
                        crossHairImageView.x =
                            (imageAnalyzeView.measuredWidth - crossHairImageView.width).toFloat()
                    } else if ((crossHairImageView.y) > (imageAnalyzeView.measuredHeight - crossHairImageView.height)) {
                        crossHairImageView.y =
                            (imageAnalyzeView.measuredHeight - crossHairImageView.height).toFloat()
                    } else if ((crossHairImageView.x) < 0) {
                        crossHairImageView.x = 0F
                    } else if ((crossHairImageView.y) < 0) {
                        crossHairImageView.y = 0F
                    }
                }
            }
            true
        }
        eraseButton.setOnClickListener {
            posValueList = mutableListOf()
            negValueList = mutableListOf()

        }
        submitAnalyzeButton.setOnClickListener {

            val bitmap = Bitmap.createBitmap(
                imageAnalyzeView.width,
                imageAnalyzeView.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            imageAnalyzeView.draw(canvas)

            val greenColor = getAverageColor(bitmap, crossHairImageView)

            when (radioType) {
                "patient" -> {
                    Log.d("dog", "patient")
                    //check if at least one posValueList and one negValueList before calculating
                    if (posValueList.isEmpty() || negValueList.isEmpty()) {
                        Log.d("dog", "restart1")
                        Snackbar.make(
                            imageConstraint,
                            "You must have positive and negative samples to analyze patient samples.",
                            Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction("DISMISS") {
                                Log.d("dog", "restart2")

                            }
                        }.show()
                    }
                    patValue = greenColor.toInt()
                    val patientNormalized =
                        (patValue - negValueList.average()) / (posValueList.average() - negValueList.average())
                    val myCustomString: SpannableStringBuilder = if (patientNormalized > 0) {
                        val reportNumber = (patientNormalized * 100).roundToInt()
                        //popup with normalized patient value
                        SpannableStringBuilder()
                            .append("The chance of the patient having ")
                            .italic { append("Leishmaniasis ") }
                            .append("is ")
                            .color(Color.RED) { append("${reportNumber}%") }
                    } else {
                        SpannableStringBuilder()
                            .color(Color.RED) { append("Patient values are not within your positive and negative controls.") }
                    }
                    Snackbar.make(imageConstraint, myCustomString, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAction("DISMISS") {
                                Log.d("dog", "patient results")
                            }
                        }.show()
                }

                "positive" -> {
                    Log.d("dog", "positive")
                    posValueList.add(greenColor.toInt())
                }

                "negative" -> {
                    crossHairImageView.setColorFilter(Color.RED)
                    Log.d("dog", "negative")
                    negValueList.add(greenColor.toInt())
                }

            }
            Log.d("dog submit", radioType + crossHairImageView.x.toString())
            Log.d("dog submit green color", greenColor.toString())
            Log.d("dog submit pos", posValueList.toString())
            Log.d("dog submit neg", negValueList.toString())

            //Deselect rGroup
            rGroup.clearCheck()
        }
    }

    private fun createCrossHair(
        crossHairImageView: ImageView,
        imageConstraint: ConstraintLayout,
        imageAnalyzeView: ImageView
    ) {
        try {
            imageConstraint.removeView(crossHairImageView)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Some error", Toast.LENGTH_SHORT).show()
        }
        crossHairImageView.setImageResource(R.drawable.baseline_gps_not_fixed_24)
        imageConstraint.addView(crossHairImageView)
        crossHairImageView.x =
            (imageAnalyzeView.width / 2 - (crossHairImageView.width / 2)).toFloat()
        crossHairImageView.y = imageAnalyzeView.height * 0.5.toFloat()
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
            Log.d("dog", "cross hairX is $crossX")
            for (j in tempY - 1..tempY + 1) {
                val crossY = crossHairImageView.y.toInt() + (crossHairImageView.height / 2)
                val color = bitmap.getPixel(crossX, crossY)
                Log.d("dog Raw Green OUT", Color.green(color).toString())
                tempListGreen.add(Color.green(color))
                tempListBlue.add(Color.blue(color))
                tempListRed.add(Color.red(color))
            }
        }
        Log.d("dog temp average green", tempListGreen.average().toString())
        return tempListGreen.average()
    }

    fun whichRadio(view: View) {
        // This is called from xml, do not delete or remove view
        crossHairImageView.isVisible = true
        //val individualRadio = rGroup.checkedRadioButtonId
        if (rGroup.checkedRadioButtonId.toString() == R.id.patientSamples.toString()) {
            radioType = "patient"
            crossHairImageView.setColorFilter(Color.MAGENTA)
        } else if (rGroup.checkedRadioButtonId.toString() == R.id.negativeControls.toString()) {
            radioType = "negative"
            crossHairImageView.setColorFilter(Color.RED)
        } else if (rGroup.checkedRadioButtonId.toString() == R.id.positiveControls.toString()) {
            radioType = "positive"
            crossHairImageView.setColorFilter(Color.GREEN)
        } else {
            radioType = "none"
        }
        Log.d("dog", view.height.toString())
    }
}






