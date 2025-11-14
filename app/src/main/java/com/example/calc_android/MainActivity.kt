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
    private lateinit var buttonEquals: Button
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

    private var errorDisplaying = false

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

                if (errorDisplaying) {
                    opString = ""
                    errorDisplaying = false
                }

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

                if (errorDisplaying) {
                    opString = ""
                    errorDisplaying = false
                }

                when (buttonResourceName) {
                    "buttonDivide" -> opString = "${opString}÷"
                    "buttonMultiply" -> opString = "${opString}x"
                    "buttonSubtract" -> opString = "${opString}-"
                    "buttonAdd" -> opString = "${opString}+"
                }
                textViewMain.text = opString
            }

        }

        // Instantiate equals button
        buttonEquals = findViewById(R.id.buttonEquals)
        buttonEquals.setOnClickListener {

            val expression = textViewMain.text
            val mathSymbolsRegex = Regex("((?=[+\\-\\ \\\\x÷()])|(?<=[+\\-\\ \\\\x÷()]))")

            val expList: MutableList<String> = expression.split(mathSymbolsRegex).toMutableList()

            if (isValidExpression(expList)) {

                // Calculate expression

                val result = getAnswer(expList)
                opString = result[0]
                textViewMain.text = opString

            } else {
                textViewMain.text = "Error"
            }

        }


    } // End of onCreate

    fun isValidExpression(expression: MutableList<String>): Boolean {

        var symbolCount = 0
        var numCount = 0

        for (ch in expression) {
            if (ch == "÷" ||
                ch == "x" ||
                ch == "-" ||
                ch == "+") {
                symbolCount++
            } else if (ch.toIntOrNull() != null){
                numCount++
            }
        }

        if (symbolCount + 1 == numCount) {
            return true
        } else {
            return false
        }

    }


    fun getAnswer(expression: MutableList<String>): MutableList<String> {

        // 10-2*4+1
        var exp = expression

        // index value
        var i = 0

        // Perform multiplication and division first
        while (exp.contains("x") || exp.contains("÷")) {
            if (exp[i] == "x") {
                exp = simplify(exp, i)
                i = 0
            } else if (exp[i] == "÷") {
                exp = simplify(exp, i)
                i = 0
            } else {
                i++
            }
        }

        // Perform addition and subtraction next
        while (exp.contains("+") || exp.contains("-")) {
            if (exp[i] == "+") {
                exp = simplify(exp, i)
                i = 0
            } else if (exp[i] == "-") {
                exp = simplify(exp, i)
                i = 0
            } else {
                i++
            }
        }

        return exp

    }

    fun simplify(exp: MutableList<String>, i: Int): MutableList<String> {

        // Get the specified expression from the original expression
        val expSlice = listOf(exp[i - 1], exp[i], exp[i + 1])

        // Calculate the sliced expression
        val sliceResult = calculate(expSlice)

        // Handle division by zero
        if (sliceResult == "null") {
            errorDisplaying = true
            return mutableListOf("Error")
        }

        // Create new list to hold remaining characters from the original expression
        val expTemp = mutableListOf<String>()
        for (j in 0..< exp.size) {
            if (j != i - 1 && j != i && j != i + 1) { // Add characters minus the ones in expSlice
                expTemp.add(exp[j])
            }
        }

        // Add result from the expression slice back into the original expression
        expTemp.add(i - 1, sliceResult)

        return  expTemp

    }

    fun calculate(expSlice: List<String>): String {

        val operator = expSlice[1]
        val x = expSlice[0].toInt()
        val y = expSlice[2].toInt()
        var result = 0

        when (operator) {
            "x" -> result = x * y
            "÷" -> {
                if (y == 0) {
                    return "null"
                } else {
                    result = x / y
                }
            }
            "-" -> result = x - y
            "+" -> result = x + y
        }

        return result.toString()

    }



}