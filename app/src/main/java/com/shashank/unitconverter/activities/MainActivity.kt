package com.shashank.unitconverter.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.shashank.unitconverter.BaseActivity
import com.shashank.unitconverter.R
import com.shashank.unitconverter.databinding.ActivityMainBinding
import com.shashank.unitconverter.fragments.Area
import com.shashank.unitconverter.fragments.Data
import com.shashank.unitconverter.fragments.Length
import com.shashank.unitconverter.fragments.Mass
import com.shashank.unitconverter.fragments.Speed
import com.shashank.unitconverter.fragments.Temperature
import com.shashank.unitconverter.fragments.Time
import com.shashank.unitconverter.fragments.Tip
import com.shashank.unitconverter.fragments.Volume

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var lastClickedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = getString(R.string.app_name)
        // Apply insets to handle the status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }

        // Get the last clicked fragment tag from ThemePreferences
        val lastFragmentTag = themePreference.getLastClickedFragment() ?: "Area"

        // Show the last clicked fragment
        showFragment(getFragmentByTag(lastFragmentTag), lastFragmentTag)

        // Set click listeners for TextViews
        setTextViewClickListeners()

        // Restore the state of the last clicked TextView
        lastClickedTextView = getTextViewByTag(lastFragmentTag)
        setTextViewState(lastClickedTextView, true)
    }

    private fun setTextViewClickListeners() {
        binding.area.setOnClickListener { onClicked(binding.area, "Area") }
        binding.length.setOnClickListener { onClicked(binding.length, "Length") }
        binding.mass.setOnClickListener { onClicked(binding.mass, "Mass") }
        binding.volume.setOnClickListener { onClicked(binding.volume, "Volume") }
        binding.time.setOnClickListener { onClicked(binding.time, "Time") }
        binding.temperature.setOnClickListener { onClicked(binding.temperature, "Temperature") }
        binding.speed.setOnClickListener { onClicked(binding.speed, "Speed") }
        binding.data.setOnClickListener { onClicked(binding.data, "Data") }
        binding.tip.setOnClickListener { onClicked(binding.tip, "Tip") }
    }

    private fun setTextViewState(textView: TextView, isClicked: Boolean) {
        if (isClicked) {
            textView.background = ContextCompat.getDrawable(this, R.drawable.tablayout_tv_bg)
            textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray))
        }
    }

    private fun onClicked(clicked: TextView, fragmentTag: String) {
        // Update UI states
        setTextViewState(lastClickedTextView, false)
        setTextViewState(clicked, true)
        lastClickedTextView = clicked

        // Save the last clicked fragment tag
        themePreference.saveLastClickedFragment(fragmentTag)

        // Show the selected fragment
        showFragment(getFragmentByTag(fragmentTag), fragmentTag)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.conatiner, fragment, tag)
            .commit()
    }

    private fun getFragmentByTag(tag: String): Fragment {
        return when (tag) {
            "Area" -> Area()
            "Length" -> Length()
            "Mass" -> Mass()
            "Volume" -> Volume()
            "Time" -> Time()
            "Temperature" -> Temperature()
            "Speed" -> Speed()
            "Data" -> Data()
            "Tip" -> Tip()
            else -> {
                // Handle unknown tags: default to "Area" and update preferences
                themePreference.saveLastClickedFragment("Area")
                lastClickedTextView = getTextViewByTag("Area")
                setTextViewState(lastClickedTextView, true)
                Area()
            }
        }
    }

    private fun getTextViewByTag(tag: String): TextView {
        return when (tag) {
            "Area" -> binding.area
            "Length" -> binding.length
            "Mass" -> binding.mass
            "Volume" -> binding.volume
            "Time" -> binding.time
            "Temperature" -> binding.temperature
            "Speed" -> binding.speed
            "Data" -> binding.data
            "Tip" -> binding.tip
            else -> binding.area // Default to "Area" if no match found
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.conatiner)
                if (currentFragment != null) {
                    when (currentFragment) {
                        is Area -> currentFragment.clearData(item.itemId)
                        is Length -> currentFragment.clearData(item.itemId)
                        is Mass -> currentFragment.clearData(item.itemId)
                        is Volume -> currentFragment.clearData(item.itemId)
                        is Time -> currentFragment.clearData(item.itemId)
                        is Temperature -> currentFragment.clearData(item.itemId)
                        is Speed -> currentFragment.clearData(item.itemId)
                        is Data -> currentFragment.clearData(item.itemId)
                        is Tip -> currentFragment.clearData()
                    }
                } else {
                    // Handle the case where there is no current fragment
                    // You might want to show an error message or handle it gracefully
                    // For now, let's just log a message
                    Log.e("MainActivity", "No fragment found to clear data")
                }
                true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
    private fun rotateView(view: Int) {
        // Create an ObjectAnimator for rotationY with 0 to 180 degrees
        val rotationAnimator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f)

        // Set animation duration in milliseconds
        rotationAnimator.duration = 900

        // Start the animation
        rotationAnimator.start()
    }
}
