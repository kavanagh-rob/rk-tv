package com.tvdashboard.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    companion object {
        const val PREF_URL = "dashboard_url"
        const val PREF_FULLSCREEN = "fullscreen_mode"
        const val DEFAULT_URL = "file:///android_asset/default-dashboard.html"
        const val SETTINGS_REQUEST = 1001
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen immersive
        applyFullscreen()

        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        webView = findViewById(R.id.webView)

        setupWebView()
        loadDashboardUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            databaseEnabled = true

            // Optimize for TV rendering
            setSupportZoom(false)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                // Inject D-pad focus helper if needed
                view?.evaluateJavascript("""
                    (function() {
                        // Ensure first focusable element gets focus
                        var first = document.querySelector('[tabindex]');
                        if (first) first.focus();
                    })();
                """.trimIndent(), null)
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    view?.loadData(
                        getErrorHtml(error?.description?.toString() ?: "Unknown error"),
                        "text/html", "UTF-8"
                    )
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadDashboardUrl() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val url = prefs.getString(PREF_URL, DEFAULT_URL) ?: DEFAULT_URL

        progressBar.visibility = View.VISIBLE

        if (url.isBlank()) {
            webView.loadUrl(DEFAULT_URL)
        } else {
            webView.loadUrl(url)
        }
    }

    private fun applyFullscreen() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fullscreen = prefs.getBoolean(PREF_FULLSCREEN, true)

        if (fullscreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // MENU button opens settings
            KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_BOOKMARK -> {
                openSettings()
                return true
            }
            // Long press BACK or dedicated button for settings
            KeyEvent.KEYCODE_SETTINGS -> {
                openSettings()
                return true
            }
            // BACK navigates within WebView, or exits
            KeyEvent.KEYCODE_BACK -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                    return true
                }
            }
            // R key to reload
            KeyEvent.KEYCODE_R -> {
                webView.reload()
                Toast.makeText(this, "Reloading…", Toast.LENGTH_SHORT).show()
                return true
            }
            // S key for settings (convenience)
            KeyEvent.KEYCODE_S -> {
                openSettings()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        @Suppress("DEPRECATION")
        startActivityForResult(intent, SETTINGS_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST) {
            // Reload with new URL
            applyFullscreen()
            loadDashboardUrl()
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    private fun getErrorHtml(message: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    background: #0a0a0f;
                    color: #e8e8f0;
                    font-family: sans-serif;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                    margin: 0;
                }
                h1 { color: #f87171; font-size: 36px; }
                p { color: #6e6e82; font-size: 20px; max-width: 600px; text-align: center; }
                .hint {
                    margin-top: 30px;
                    padding: 16px 32px;
                    border: 2px solid #2a2a3a;
                    border-radius: 12px;
                    color: #4d8eff;
                    font-size: 18px;
                }
            </style>
        </head>
        <body>
            <h1>Connection Error</h1>
            <p>$message</p>
            <div class="hint">Press MENU or S to open Settings and update your URL</div>
        </body>
        </html>
        """.trimIndent()
    }
}
