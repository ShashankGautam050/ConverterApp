package com.shashank.unitconverter

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun setDarkMode(isDarkMode: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun getSwitchState(): Boolean {
        return prefs.getBoolean("switch_state", false)
    }

    fun setSwitchState(isChecked: Boolean) {
        prefs.edit().putBoolean("switch_state", isChecked).apply()
    }

    fun saveEditTextValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getEditTextValue(key: String): String? {
        return prefs.getString(key, "")
    }

    fun saveTextViewValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getTextViewValue(key: String): String? {
        return prefs.getString(key, "")
    }

    fun saveLastClickedFragment(tag: String) {
        prefs.edit().putString("last_clicked_fragment", tag).apply()
    }

    fun getLastClickedFragment(): String? {
        return prefs.getString("last_clicked_fragment", "")
    }

    // Methods to save and retrieve values specific to the Tip fragment
    fun saveBaseAmount(value: String) {
        prefs.edit().putString("base_amount", value).apply()
    }

    fun getBaseAmount(): String? {
        return prefs.getString("base_amount", "")
    }

    fun saveTipPercentage(value: String) {
        prefs.edit().putString("tip_percentage", value).apply()
    }

    fun getTipPercentage(): String? {
        return prefs.getString("tip_percentage", "")
    }

    fun saveTipAmount(value: String) {
        prefs.edit().putString("tip_amount", value).apply()
    }

    fun getTipAmount(): String? {
        return prefs.getString("tip_amount", "")
    }

    fun saveTotalPayableAmount(value: String) {
        prefs.edit().putString("total_payable_amount", value).apply()
    }

    fun getTotalPayableAmount(): String? {
        return prefs.getString("total_payable_amount", "")
    }
    fun saveSeekBarProgress(key: String, progress: Int) {
        prefs.edit().putInt(key, progress).apply()
    }

    // Method to retrieve SeekBar progress
    fun getSeekBarProgress(key: String): Int {
        return prefs.getInt(key, 0) // Default value is 0
    }
    fun saveSpinnerPosition(key: String, position: Int) {
        prefs.edit().putInt(key, position).apply()
    }

    fun getSpinnerPosition(key: String): Int {
        return prefs.getInt(key, 0)
    }
    fun saveSpinnerFromPosition(position: Int) {
        prefs.edit().putInt("spinner_from_position", position).apply()
    }

    fun getSpinnerFromPosition(): Int {
        return prefs.getInt("spinner_from_position", 0)
    }

    fun saveSpinnerToPosition(position: Int) {
        prefs.edit().putInt("spinner_to_position", position).apply()
    }

    fun getSpinnerToPosition(): Int {
        return prefs.getInt("spinner_to_position", 0)
    }
}
