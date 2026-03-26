package com.example.travelcompanionapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    private val categories = arrayOf("Currency", "Fuel", "Temperature")

    private val currencyUnits = arrayOf("USD", "AUD", "EUR", "JPY", "GBP")
    private val fuelUnits = arrayOf("mpg", "km/L", "Gallon", "Liters", "Nautical Mile", "Kilometer")
    private val temperatureUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextValue = findViewById(R.id.editTextValue)
        buttonConvert = findViewById(R.id.buttonConvert)
        textViewResult = findViewById(R.id.textViewResult)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        updateUnitSpinners("Currency")

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                updateUnitSpinners(selectedCategory)
                textViewResult.text = "Converted value will appear here"
                editTextValue.text.clear()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonConvert.setOnClickListener {
            performConversion()
        }
    }

    private fun updateUnitSpinners(category: String) {
        val units = when (category) {
            "Currency" -> currencyUnits
            "Fuel" -> fuelUnits
            "Temperature" -> temperatureUnits
            else -> currencyUnits
        }

        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = unitAdapter
        spinnerTo.adapter = unitAdapter
    }

    private fun performConversion() {
        val category = spinnerCategory.selectedItem.toString()
        val fromUnit = spinnerFrom.selectedItem.toString()
        val toUnit = spinnerTo.selectedItem.toString()
        val inputText = editTextValue.text.toString().trim()

        if (inputText.isEmpty()) {
            textViewResult.text = "Converted value will appear here"
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            return
        }

        val inputValue = inputText.toDoubleOrNull()
        if (inputValue == null) {
            textViewResult.text = "Converted value will appear here"
            Toast.makeText(this, "Please enter a valid numeric value", Toast.LENGTH_SHORT).show()
            return
        }

        // Block all negative values
        if (inputValue < 0) {
            textViewResult.text = "Converted value will appear here"
            Toast.makeText(this, "Negative values are not allowed", Toast.LENGTH_SHORT).show()
            return
        }

        if (fromUnit == toUnit) {
            textViewResult.text = "Result: %.2f %s".format(inputValue, toUnit)
            Toast.makeText(this, "Source and destination are the same", Toast.LENGTH_SHORT).show()
            return
        }

        val result = when (category) {
            "Currency" -> convertCurrency(fromUnit, toUnit, inputValue)
            "Fuel" -> convertFuel(fromUnit, toUnit, inputValue)
            "Temperature" -> convertTemperature(fromUnit, toUnit, inputValue)
            else -> null
        }

        if (result != null) {
            textViewResult.text = "Result: %.2f %s".format(result, toUnit)
        } else {
            textViewResult.text = "Converted value will appear here"
            Toast.makeText(this, "Conversion not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertCurrency(from: String, to: String, value: Double): Double? {
        val usdValue = when (from) {
            "USD" -> value
            "AUD" -> value / 1.55
            "EUR" -> value / 0.92
            "JPY" -> value / 148.50
            "GBP" -> value / 0.78
            else -> return null
        }

        return when (to) {
            "USD" -> usdValue
            "AUD" -> usdValue * 1.55
            "EUR" -> usdValue * 0.92
            "JPY" -> usdValue * 148.50
            "GBP" -> usdValue * 0.78
            else -> null
        }
    }

    private fun convertFuel(from: String, to: String, value: Double): Double? {
        return when {
            from == "mpg" && to == "km/L" -> value * 0.425
            from == "km/L" && to == "mpg" -> value / 0.425

            from == "Gallon" && to == "Liters" -> value * 3.785
            from == "Liters" && to == "Gallon" -> value / 3.785

            from == "Nautical Mile" && to == "Kilometer" -> value * 1.852
            from == "Kilometer" && to == "Nautical Mile" -> value / 1.852

            else -> null
        }
    }

    private fun convertTemperature(from: String, to: String, value: Double): Double? {
        return when {
            from == "Celsius" && to == "Fahrenheit" -> (value * 1.8) + 32
            from == "Fahrenheit" && to == "Celsius" -> (value - 32) / 1.8
            from == "Celsius" && to == "Kelvin" -> value + 273.15
            from == "Kelvin" && to == "Celsius" -> value - 273.15
            from == "Fahrenheit" && to == "Kelvin" -> ((value - 32) / 1.8) + 273.15
            from == "Kelvin" && to == "Fahrenheit" -> ((value - 273.15) * 1.8) + 32
            else -> null
        }
    }
}