package com.shashank.unitconverter.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.shashank.unitconverter.ThemePreferences
import com.shashank.unitconverter.databinding.FragmentTipBinding

class Tip : Fragment() {
    private lateinit var themePreferences: ThemePreferences
    private lateinit var binding: FragmentTipBinding

    companion object {
        private const val KEY_BASE_AMOUNT = "base_amount"
        private const val KEY_TIP_PERCENTAGE = "tip_percentage"
        private const val KEY_TIP_AMOUNT = "tip_amount"
        private const val KEY_TOTAL_PAYABLE_AMOUNT = "total_payable_amount"
        private const val KEY_SEEK_BAR_PROGRESS = "seek_bar_progress"
        private const val KEY_SPINNER_POSITION = "spinner_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipBinding.inflate(inflater, container, false)
        setupSpinner()
        setupSeekBar()
        setupBaseAmountEditText()
        restoreValues()
        return binding.root
    }

    private fun setupSpinner() {
        val countList = (1..100).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countSpinner.adapter = adapter
        binding.countSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateAmounts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.emojiTv.text = when {
                    progress < 15 -> "😐"
                    progress == 15 -> "😊"
                    else -> "😄"
                }
                binding.tipPercentTv.text = "$progress%"
                updateAmounts()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupBaseAmountEditText() {
        binding.etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateAmounts()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateAmounts() {
        val baseAmount = binding.etBaseAmount.text.toString().toDoubleOrNull() ?: 0.0
        val tipPercentage = binding.seekBar.progress
        val peopleCount = binding.countSpinner.selectedItem.toString().toIntOrNull() ?: 1

        // Calculate tip amount and total
        val tipAmount = (tipPercentage / 100.0) * baseAmount
        val totalAmount = baseAmount + tipAmount
        val eachAmount = if (peopleCount > 0) totalAmount / peopleCount else 0.0

        // Format values to one decimal place
        val formattedTipAmount = String.format("%.1f", tipAmount)
        val formattedTotalAmount = String.format("%.1f", eachAmount)

        // Update UI
        binding.tipAmountVal.text = "₹ $formattedTipAmount"
        binding.totalPayableTv.text = "₹ $formattedTotalAmount"

        // Save values
        saveValues(baseAmount, tipPercentage, tipAmount, totalAmount, peopleCount)
    }

    private fun saveValues(baseAmount: Double, tipPercentage: Int, tipAmount: Double, totalAmount: Double, peopleCount: Int) {
        themePreferences.apply {
            saveBaseAmount(baseAmount.toString())
            saveTipPercentage("$tipPercentage%")
            saveTipAmount("₹ $tipAmount")
            saveTotalPayableAmount("₹ $totalAmount")
            saveSeekBarProgress(KEY_SEEK_BAR_PROGRESS, tipPercentage)
            saveSpinnerPosition(KEY_SPINNER_POSITION, binding.countSpinner.selectedItemPosition)
        }
    }

    private fun restoreValues() {
        val baseAmount = themePreferences.getBaseAmount()?.toDoubleOrNull() ?: 0.0
        val tipPercentage = themePreferences.getSeekBarProgress(KEY_SEEK_BAR_PROGRESS)
        val spinnerPosition = themePreferences.getSpinnerPosition(KEY_SPINNER_POSITION)

        binding.etBaseAmount.setText(baseAmount.toString())
        binding.seekBar.progress = tipPercentage
        binding.countSpinner.setSelection(spinnerPosition, false)

        updateAmounts() // Recalculate amounts with the restored values
    }

    fun clearData() {
        binding.etBaseAmount.text.clear()
        binding.seekBar.progress = 0
        binding.countSpinner.setSelection(0, false)
        binding.emojiTv.text = ""
        binding.tipPercentTv.text = "0%"

        // Format values to one decimal place
        val formattedZero = String.format("%.1f", 0.0)

        binding.tipAmountVal.text = "₹ $formattedZero"
        binding.totalPayableTv.text = "₹ $formattedZero"

        themePreferences.apply {
            saveBaseAmount("")
            saveTipPercentage("")
            saveTipAmount("")
            saveTotalPayableAmount("")
            saveSeekBarProgress(KEY_SEEK_BAR_PROGRESS, 0)
            saveSpinnerPosition(KEY_SPINNER_POSITION, 0)
        }
    }
}
