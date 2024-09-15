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
import com.shashank.unitconverter.databinding.FragmentSpeedBinding

class Speed : Fragment() {
    private var previousFragmentName: String? = null
    private lateinit var binding: FragmentSpeedBinding
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
        binding = FragmentSpeedBinding.inflate(inflater, container, false)
        themePreferences = ThemePreferences(requireContext()) // Initialize ThemePreferences

        val unitOfSpeed = resources.getStringArray(R.array.unit_of_speed)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, unitOfSpeed)
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
            val result = convertSpeed(fromValue, fromUnit, toUnit)
            binding.spinnerTo.text = result.toString()

            // Save values to SharedPreferences
            saveValues(fromValue, fromUnit, toUnit, result)
        }
    }

    private val speedConversionFactors: Map<String, Double> = mapOf(
        "Meter per Second (m/s)" to 1.0,
        "Kilometer per Hour (km/h)" to 0.277778,
        "Foot per Second (ft/s)" to 0.3048,
        "Mile per Hour (mph)" to 0.44704,
        "Knot (kn)" to 0.514444,
        "Mach (at sea level)" to 343.0 // Mach 1 at sea level is approximately 343 m/s
    )

    private fun convertSpeed(fromValue: Double, fromUnit: String, toUnit: String): Double {
        val fromFactor = speedConversionFactors[fromUnit] ?: 1.0
        val toFactor = speedConversionFactors[toUnit] ?: 1.0

        return (fromValue * fromFactor) / toFactor
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
            val adapter = binding.spinnerFromDrop.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(it)
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

    fun clearData(itemId : Int) {
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
