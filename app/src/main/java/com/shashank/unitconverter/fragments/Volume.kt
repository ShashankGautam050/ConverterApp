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
import com.shashank.unitconverter.databinding.FragmentVolumeBinding

class Volume : Fragment() {

    private var previousFragmentName: String? = null
    private lateinit var binding: FragmentVolumeBinding
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
        binding = FragmentVolumeBinding.inflate(inflater, container, false)
        themePreferences = ThemePreferences(requireContext()) // Initialize ThemePreferences

        val volumeUnits = resources.getStringArray(R.array.unit_volume)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, volumeUnits)
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
            val result = convertVolume(fromValue, fromUnit, toUnit)
            binding.spinnerTo.text = result.toString()

            // Save values to SharedPreferences
            saveValues(fromValue, fromUnit, toUnit, result)
        }
    }

    private fun convertVolume(fromValue: Double, fromUnit: String, toUnit: String): Double {
        val conversionFactorsToLiters = mapOf(
            "Liter (L)" to 1.0,
            "Milliliter (mL)" to 1e-3,
            "Cubic Meter (m³)" to 1e3,
            "Cubic Centimeter (cm³)" to 1e-3,
            "Cubic Inch (in³)" to 0.0163871,
            "Cubic Foot (ft³)" to 28.3168,
            "Gallon (US)" to 3.78541,
            "Gallon (UK)" to 4.54609,
            "Quart (US)" to 0.946353,
            "Quart (UK)" to 1.13652,
            "Pint (US)" to 0.473176,
            "Pint (UK)" to 0.568261,
            "Fluid Ounce (US)" to 0.0295735,
            "Fluid Ounce (UK)" to 0.0284131,
            "Tablespoon (US)" to 0.0147868,
            "Teaspoon (US)" to 0.00492892
        )

        val fromFactor = conversionFactorsToLiters[fromUnit] ?: return Double.NaN
        val toFactor = conversionFactorsToLiters[toUnit] ?: return Double.NaN

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
