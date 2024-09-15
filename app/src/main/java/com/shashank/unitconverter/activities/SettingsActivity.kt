package com.shashank.unitconverter.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shashank.unitconverter.BaseActivity
import com.shashank.unitconverter.R
import com.shashank.unitconverter.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the theme based on saved preferences
        val currentMode = if (themePreference.isDarkMode()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(currentMode)

        // Apply the theme before setting content view
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the switch state based on saved preferences
        binding.modeChanger.isChecked = themePreference.isDarkMode()

        binding.modeChanger.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
            themePreference.setDarkMode(isChecked)

            // Optionally update the theme
            updateTheme()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun updateTheme() {
        // Update UI elements based on the current theme
        val isDarkMode = themePreference.isDarkMode()

        val rootView = findViewById<View>(R.id.main)
        if (isDarkMode) {
            rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        } else {
            rootView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        }

        // Update any other UI elements that rely on the theme
        binding.toolbar.setBackgroundColor(
            ContextCompat.getColor(this, if (isDarkMode) R.color.black else android.R.color.white)
        )
    }

}
