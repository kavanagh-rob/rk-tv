package com.tvdashboard.app

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var fullscreenCheck: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        urlInput = findViewById(R.id.urlInput)
        fullscreenCheck = findViewById(R.id.fullscreenCheck)

        val currentUrl = prefs.getString(MainActivity.PREF_URL, MainActivity.DEFAULT_URL) ?: ""
        urlInput.setText(currentUrl)
        fullscreenCheck.isChecked = prefs.getBoolean(MainActivity.PREF_FULLSCREEN, true)

        // Preset buttons
        findViewById<Button>(R.id.btnPresetLocal).setOnClickListener {
            urlInput.setText("http://192.168.1.100:8080/tv-dashboard.html")
            urlInput.requestFocus()
        }

        findViewById<Button>(R.id.btnPresetGithub).setOnClickListener {
            urlInput.setText("https://yourusername.github.io/tv-dashboard/tv-dashboard.html")
            urlInput.requestFocus()
        }

        findViewById<Button>(R.id.btnPresetBuiltin).setOnClickListener {
            urlInput.setText(MainActivity.DEFAULT_URL)
        }

        // Save button
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveAndClose()
        }

        // Cancel button
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        // Focus the URL input initially
        urlInput.requestFocus()
    }

    private fun saveAndClose() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
            .putString(MainActivity.PREF_URL, urlInput.text.toString().trim())
            .putBoolean(MainActivity.PREF_FULLSCREEN, fullscreenCheck.isChecked)
            .apply()

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
