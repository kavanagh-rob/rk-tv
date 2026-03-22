package com.tvdashboard.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        urlInput = findViewById(R.id.urlInput)
        urlInput.setText(prefs.getString(MainActivity.PREF_URL, "") ?: "")

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                prefs.edit().putString(MainActivity.PREF_URL, url).apply()
                Toast.makeText(this, "URL updated!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            prefs.edit()
                .remove(MainActivity.PREF_URL)
                .putBoolean(MainActivity.PREF_SETUP_DONE, false)
                .apply()
            Toast.makeText(this, "Reset! Next launch will show setup.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

        urlInput.requestFocus()
    }
}
