package com.example.calc_android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var textViewMain: TextView
    private lateinit var opString: String
    private lateinit var buttonClear: Button
    private val numberButtonIds = arrayOf(
        R.id.button0,
        R.id.button1,
        R.id.button2,
        R.id.button3,
        R.id.button4,
        R.id.button5,
        R.id.button6,
        R.id.button7,
        R.id.button8,
        R.id.button9,
    )
    private val functionButtonIds = arrayOf(
        R.id.buttonDivide,
        R.id.buttonMultiply,
        R.id.buttonSubtract,
        R.id.buttonAdd,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Instantiate the main text view and set the string value
        textViewMain = findViewById(R.id.textViewMain)
        opString = ""
        textViewMain.text = opString

        // Instantiating number buttons
        for (i in 0..<numberButtonIds.size) {

            val numButton: Button = findViewById(numberButtonIds[i])
            numButton.setOnClickListener {
                opString = "$opString$i"
                textViewMain.text = opString
            }

        }

        // Instantiate clear button
        buttonClear = findViewById(R.id.buttonClear)
        buttonClear.setOnClickListener {
            opString = ""
            textViewMain.text = opString
        }

        // Instantiate function buttons
        for (id in functionButtonIds) {

            val functionButton: Button = findViewById(id)
            val buttonResourceName = resources.getResourceEntryName(functionButton.id)

            functionButton.setOnClickListener {
                when (buttonResourceName) {
                    "buttonDivide" -> opString = "${opString}÷"
                    "buttonMultiply" -> opString = "${opString}x"
                    "buttonSubtract" -> opString = "${opString}-"
                    "buttonAdd" -> opString = "${opString}+"
                }
                textViewMain.text = opString
            }

        }


    } // End of onCreate




}