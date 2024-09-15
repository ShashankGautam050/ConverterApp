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
import com.shashank.unitconverter.databinding.FragmentDataBinding


class Data : Fragment() {
    private var previousFragmentName: String? = null
    private lateinit var binding: FragmentDataBinding
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
        binding = FragmentDataBinding.inflate(inflater, container, false)
        themePreferences = ThemePreferences(requireContext()) // Initialize ThemePreferences

        // Inflate the layout for this fragment
        val dataUnits = resources.getStringArray(R.array.unit_data_storage)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, dataUnits)
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
            val result = convertData(fromValue, fromUnit, toUnit)
            binding.spinnerTo.text = result.toString()

            // Save values to SharedPreferences
            saveValues(fromValue, fromUnit, toUnit, result)
        }
    }

    private fun convertData(fromValue: Double, fromUnit: String, toUnit: String): Double {
        // Define conversion factors to square meters
        val conversionFactors = mapOf(
            "Bit (b)" to 0.125,
            "Nibble" to 0.5,
            "Byte (B)" to 1.0,
            "Kilobyte (KB)" to 1024.0,
            "Megabyte (MB)" to 1.048576e6,  // 1024 * 1024
            "Gigabyte (GB)" to 1.073741824e9,  // 1024 * 1024 * 1024
            "Terabyte (TB)" to 1.099511627776e12,  // 1024^4
            "Petabyte (PB)" to 1.125899906842624e15,  // 1024^5
            "Exabyte (EB)" to 1.15292150460684698E18,  // 1024^6
            "Zettabyte (ZB)" to 1.1805916207174113E21,  // 1024^7
            "Yottabyte (YB)" to 1.2089258196146292E24  // 1024^8
        )


        val fromFactor = conversionFactors[fromUnit] ?: 1.0
        val toFactor = conversionFactors[toUnit] ?: 1.0

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

        fromUnit?.let { it ->
            val adapter = binding.spinnerFromDrop.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(it)
            binding.spinnerFromDrop.setSelection(position, false)
        }

        toUnit?.let {
            val position = (binding.spinnerToDrop.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerToDrop.setSelection(position, false) // false to avoid triggering item selected listener
        }

        result?.let {
            // Format the result to two decimal places
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
        }
    }

}