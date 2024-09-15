package com.shashank.unitconverter.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.shashank.unitconverter.R
import com.shashank.unitconverter.ThemePreferences
import com.shashank.unitconverter.databinding.FragmentTemperatureBinding

class Temperature : Fragment() {
    private var previousFragmentName: String? = null
    private lateinit var binding: FragmentTemperatureBinding
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            previousFragmentName = it.getString("previous_fragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTemperatureBinding.inflate(inflater, container, false)
        themePreferences = ThemePreferences(requireContext()) // Initialize ThemePreferences

        val temperatureUnits = resources.getStringArray(R.array.unit_temperature)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, temperatureUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFromDrop.adapter = adapter
        binding.spinnerToDrop.adapter = adapter

        // Restore saved values
        restoreValues()

        binding.spinnerFromDrop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                performConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected if needed
            }
        }

        binding.spinnerToDrop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                performConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected if needed
            }
        }

        // Use an EditText for temperature input
        binding.spinnerFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performConversion()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    private fun performConversion() {
        val fromUnit = binding.spinnerFromDrop.selectedItem.toString()
        val toUnit = binding.spinnerToDrop.selectedItem.toString()
        val fromValue = binding.spinnerFrom.text.toString().toDoubleOrNull()

        if (fromValue != null) {
            val result = convertTemperature(fromValue, fromUnit, toUnit)
            binding.spinnerTo.text = result.toString()

            // Save values to SharedPreferences
            saveValues(fromValue, fromUnit, toUnit, result)
        }
    }

    private val temperatureConversions: Map<Pair<String, String>, (Double) -> Double> = mapOf(
        Pair("Celsius (°C)", "Fahrenheit (°F)") to { celsius -> (celsius * 9.0/5.0) + 32 },
        Pair("Celsius (°C)", "Kelvin (K)") to { celsius -> celsius + 273.15 },
        Pair("Celsius (°C)", "Rankine (°R)") to { celsius -> (celsius + 273.15) * 9.0/5.0 },

        Pair("Fahrenheit (°F)", "Celsius (°C)") to { fahrenheit -> (fahrenheit - 32) * 5.0/9.0 },
        Pair("Fahrenheit (°F)", "Kelvin (K)") to { fahrenheit -> (fahrenheit - 32) * 5.0/9.0 + 273.15 },
        Pair("Fahrenheit (°F)", "Rankine (°R)") to { fahrenheit -> fahrenheit + 459.67 },

        Pair("Kelvin (K)", "Celsius (°C)") to { kelvin -> kelvin - 273.15 },
        Pair("Kelvin (K)", "Fahrenheit (°F)") to { kelvin -> (kelvin - 273.15) * 9.0/5.0 + 32 },
        Pair("Kelvin (K)", "Rankine (°R)") to { kelvin -> kelvin * 9.0/5.0 },

        Pair("Rankine (°R)", "Celsius (°C)") to { rankine -> (rankine - 491.67) * 5.0/9.0 },
        Pair("Rankine (°R)", "Fahrenheit (°F)") to { rankine -> rankine - 459.67 },
        Pair("Rankine (°R)", "Kelvin (K)") to { rankine -> rankine * 5.0/9.0 }
    )

    private fun convertTemperature(fromValue: Double, fromUnit: String, toUnit: String): Double {
        val conversionFunction = temperatureConversions[Pair(fromUnit, toUnit)]
        return conversionFunction?.invoke(fromValue) ?: fromValue
    }




    private fun saveValues(fromValue: Double, fromUnit: String, toUnit: String, result: Double) {
        themePreferences.apply {
            saveEditTextValue("fromValue", fromValue.toString())
            saveTextViewValue("fromUnit", fromUnit)
            saveTextViewValue("toUnit", toUnit)
            saveTextViewValue("result", result.toString())
        }
    }

    private fun restoreValues() {
        val fromValue = themePreferences.getEditTextValue("fromValue")
        val fromUnit = themePreferences.getTextViewValue("fromUnit")
        val toUnit = themePreferences.getTextViewValue("toUnit")
        val result = themePreferences.getTextViewValue("result")

        fromValue?.let {
            binding.spinnerFrom.setText(it)
        }

        fromUnit?.let {
            val position = (binding.spinnerFromDrop.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerFromDrop.setSelection(position, false)
        }

        toUnit?.let {
            val position = (binding.spinnerToDrop.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerToDrop.setSelection(position, false)
        }

        result?.let {
            val formattedResult = String.format("%.2f", it.toDoubleOrNull() ?: 0.0)
            binding.spinnerTo.text = formattedResult
        }
    }

    fun clearData(itemId: Int) {
        // Reset spinners and clear EditText
        val defaultPosition = (itemId % 10) - 1
        binding.spinnerFromDrop.setSelection(defaultPosition, false)
        binding.spinnerToDrop.setSelection(defaultPosition, false)
        binding.spinnerFrom.text.clear()
        binding.spinnerTo.text = ""

        // Save empty values to SharedPreferences
        themePreferences.apply {
            saveEditTextValue("fromValue", "")
            saveTextViewValue("fromUnit", "")
            saveTextViewValue("toUnit", "")
            saveTextViewValue("result", "")
        }
    }
}
