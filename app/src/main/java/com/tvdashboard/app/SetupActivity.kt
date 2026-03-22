package com.tvdashboard.app

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SetupActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        urlInput = findViewById(R.id.setupUrlInput)

        // Preset buttons
        findViewById<Button>(R.id.setupBtnLocal).setOnClickListener {
            urlInput.setText("http://192.168.1.100:8080/tv-dashboard.html")
            urlInput.requestFocus()
            urlInput.setSelection(urlInput.text.length)
        }

        findViewById<Button>(R.id.setupBtnGithub).setOnClickListener {
            urlInput.setText("https://yourusername.github.io/tv-dashboard/tv-dashboard.html")
            urlInput.requestFocus()
            urlInput.setSelection(urlInput.text.length)
        }

        // Launch button
        findViewById<Button>(R.id.setupBtnLaunch).setOnClickListener {
            saveAndLaunch()
        }

        urlInput.requestFocus()

        // Allow Enter key to submit
        urlInput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                saveAndLaunch()
                true
            } else false
        }
    }

    private fun saveAndLaunch() {
        val url = urlInput.text.toString().trim()

        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            return
        }

        // Basic validation
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
            Toast.makeText(this, "URL must start with http://, https://, or file://", Toast.LENGTH_LONG).show()
            return
        }

        // Save URL and mark setup as done
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
            .putString(MainActivity.PREF_URL, url)
            .putBoolean(MainActivity.PREF_SETUP_DONE, true)
            .apply()

        // Launch the main WebView activity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
