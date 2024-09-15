package com.shashank.unitconverter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.shashank.unitconverter.ThemePreferences

open class BaseActivity : AppCompatActivity() {

    lateinit var themePreference: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the class-level themePreference
        themePreference = ThemePreferences(this)

        // Apply the theme based on saved preferences
        val currentMode = if (themePreference.isDarkMode()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(currentMode)
    }
}
