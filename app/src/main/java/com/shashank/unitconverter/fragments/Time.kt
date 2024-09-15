package com.shashank.unitconverter.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.shashank.unitconverter.R
import com.shashank.unitconverter.ThemePreferences
import com.shashank.unitconverter.databinding.FragmentTimeBinding

class Time : Fragment() {

    private lateinit var binding: FragmentTimeBinding
    private var previousFragmentName: String? = null
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
        binding = FragmentTimeBinding.inflate(inflater, container, false)
        themePreferences = ThemePreferences(requireContext()) // Initialize ThemePreferences

        // Inflate the layout for this fragment
        val timeUnits = resources.getStringArray(R.array.unit_time)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeUnits)
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
            val result = convertTime(fromValue, fromUnit, toUnit)
            binding.spinnerTo.text = String.format("%.2f", result)

            // Save values to SharedPreferences
            saveValues(fromValue, fromUnit, toUnit, result)
        } else {
            binding.spinnerTo.text = ""
        }
    }

    private fun convertTime(value: Double, fromUnit: String, toUnit: String): Double {
        // Define conversion factors to seconds
        val conversionFactors = mapOf(
            "Second (s)" to 1.0,
            "Minute (m)" to 60.0,
            "Hour (h)" to 3600.0,
            "Day" to 86400.0,
            "Week" to 604800.0,
            "Month" to 2.628e6, // Average month length in seconds
            "Year" to 3.154e7, // Average year length in seconds
            "Millisecond (ms)" to 1e-3,
            "Microsecond (µs)" to 1e-6,
            "Nanosecond (ns)" to 1e-9
        )

        // Retrieve the conversion factors for the from and to units
        val fromFactor = conversionFactors[fromUnit] ?: 1.0
        val toFactor = conversionFactors[toUnit] ?: 1.0

        // Convert the value to the target unit
        return (value * fromFactor) / toFactor
    }

    private fun saveValues(fromValue: Double, fromUnit: String, toUnit: String, result: Double) {
        themePreferences.apply {
            saveEditTextValue("fromValue", fromValue.toString())
            saveTextViewValue("fromUnit", fromUnit)
            saveTextViewValue("toUnit", toUnit)
            saveTextViewValue("result", result.toString())

            // Save spinner positions
            saveSpinnerFromPosition(binding.spinnerFromDrop.selectedItemPosition)
            saveSpinnerToPosition(binding.spinnerToDrop.selectedItemPosition)
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
            binding.spinnerToDrop.setSelection(position, false) // false to avoid triggering item selected listener
        }

        result?.let {
            val formattedResult = String.format("%.2f", it.toDoubleOrNull() ?: 0.0)
            binding.spinnerTo.text = formattedResult
        }
    }

    fun clearData(itemId: Int) {
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

            // Save default spinner positions
            saveSpinnerFromPosition(defaultPosition)
            saveSpinnerToPosition(defaultPosition)
        }
    }
}
