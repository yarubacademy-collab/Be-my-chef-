package com.bemychef.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.net.URI

/**
 * The Android client.
 *
 * The app itself is a thin shell: the real product is the web app at
 * [ApiConfig.baseUrl]. Navigation is confined to that origin, so a link
 * elsewhere cannot turn the app into a general-purpose browser.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BeMyChef)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            // No file or content access: the app never needs to read local
            // files, and denying it removes a whole class of exfiltration.
            allowFileAccess = false
            allowContentAccess = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                val target = request.url.toString()
                return if (isBackendOrigin(target)) {
                    false
                } else {
                    // Anything off-origin is not ours to render in-app.
                    true
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                findViewById<View>(R.id.splash)?.visibility = View.GONE
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })

        if (savedInstanceState == null) {
            webView.loadUrl(ApiConfig.baseUrl)
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    private fun isBackendOrigin(url: String): Boolean = runCatching {
        val target = URI(url)
        val base = URI(ApiConfig.baseUrl)
        target.host != null && target.host.equals(base.host, ignoreCase = true)
    }.getOrDefault(false)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }
}
