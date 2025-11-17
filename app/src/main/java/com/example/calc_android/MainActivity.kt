package com.example.calc_android

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "mooo"

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
        R.id.buttonNegation
    )

    private var errorDisplaying = false
    private var digitCount = 0
    private var digitToast: Toast? = null

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
        opString = "0"
        textViewMain.text = opString

        // Instantiating number buttons
        for (i in 0..<numberButtonIds.size) {

            val numButton: Button = findViewById(numberButtonIds[i])
            numButton.setOnClickListener {

                if (digitCount == 8) {

                    digitToast?.cancel()

                    digitToast = Toast.makeText(this, "Can't enter more than 8 digits.", Toast.LENGTH_SHORT)
                    digitToast?.show()

                } else {

                    if (errorDisplaying) {
                        opString = ""
                        errorDisplaying = false
                    }

                    if (opString == "0") {
                        opString = i.toString()
                    } else {
                        digitCount++
                        opString = "$opString$i"
                    }

                    textViewMain.text = opString

                    // Resize text if necessary
                    resizeText()

                }


            }

        }

        // Instantiate clear button
        buttonClear = findViewById(R.id.buttonClear)
        buttonClear.setOnClickListener {
            opString = "0"
            textViewMain.text = opString
            digitCount = 0

            // Reset font size
            textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65f)
            Log.d(TAG, "resizeText: 65sp")
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
                    "buttonMultiply" -> opString = "${opString}×"
                    "buttonSubtract" -> opString = "${opString}-"
                    "buttonAdd" -> opString = "${opString}+"
                    "buttonNegation" -> opString = "${opString}-"
                }
                textViewMain.text = opString
                digitCount = 0
                // Resize text if necessary
                resizeText()
            }

        }

        // Instantiate equals button
        buttonEquals = findViewById(R.id.buttonEquals)
        buttonEquals.setOnClickListener {

            val expression = textViewMain.text
            val mathSymbolsRegex = Regex("((?=[+\\-\\ \\\\×÷()])|(?<=[+\\-\\ \\\\×÷()]))")

            var expList: MutableList<String> = expression.split(mathSymbolsRegex).toMutableList()
            expList = simplifyNegatives(expList)

            if (isValidExpression(expList)) {

                // Calculate expression
                val result = getAnswer(expList)
                opString = result[0]
                textViewMain.text = opString

                // Resize text accordingly
                if (opString.length > 9 && opString.length < 16) {
                    textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
                } else if (opString.length > 16) {
                    textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
                } else {
                    textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65f)
                }

            } else {
                textViewMain.text = "Error"
            }

        }


    } // End of onCreate

    fun resizeText() {

        if (textViewMain.text.length == 9) {
            textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            Log.d(TAG, "resizeText: 40sp")
        } else if (textViewMain.text.length == 16) {
            textViewMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
            Log.d(TAG, "resizeText: 20sp")
        }

    }

    fun isOperator(token: String): Boolean {
        return token == "×" ||
            token == "÷" ||
            token == "+" ||
            token == "-"
    }

    fun isValidExpression(expression: MutableList<String>): Boolean {

        var symbolCount = 0
        var numCount = 0

        for (token in expression) {
            if (isOperator(token)) {
                symbolCount++
            } else if (token.toIntOrNull() != null) {
                numCount++
            }
        }

        return symbolCount + 1 == numCount

    }

    fun simplifyNegatives(exp: MutableList<String>): MutableList<String> {

        val expFinal = mutableListOf<String>()

        var j = 0 // Loop count

        while (true) {
            // Check if token is subtraction symbol
            if (exp[j] == "-" && (exp[j + 1].toIntOrNull() != null)) {

                if (j == 0) {
                    expFinal.add("${exp[j]}${exp[j + 1]}")
                    j++
                } else if (exp[j - 1] == "-") {
                    expFinal[expFinal.size - 1] = "+"
                    expFinal.add(exp[j + 1])
                    j++
                } else if ( exp[j - 1].toIntOrNull() != null ) {
                    expFinal.add(exp[j])
                } else {
                    expFinal.add("${exp[j]}${exp[j + 1]}")
                    j++
                }

            } else if ( isOperator(exp[j]) || (exp[j].toIntOrNull() != null) ) {
                expFinal.add(exp[j])
            }

            j++

            println("\nParsing negatives: $expFinal")

            if (j == exp.size) {
                break
            }

        }

        return expFinal

    }

    fun getAnswer(expression: MutableList<String>): MutableList<String> {

        // 10-2*4+1
        var exp = expression

        // index value
        var i = 0

        // Perform multiplication and division first
        while (exp.contains("×") || exp.contains("÷")) {
            if (exp[i] == "×") {
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
        val x = expSlice[0].toLong()
        val y = expSlice[2].toLong()
        var result: Long = 0

        when (operator) {
            "×" -> result = x * y
            "÷" -> {
                if (y.toInt() == 0) {
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