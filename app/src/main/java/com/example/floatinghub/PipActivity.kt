package com.example.floatinghub

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout

class PipActivity : Activity() {
 companion object { const val EXTRA_URL = "start_url" }
 private lateinit var browser: WebView
 private lateinit var controls: LinearLayout

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val startUrl = intent.getStringExtra(EXTRA_URL) ?: "https://chatgpt.com"
  val root = LinearLayout(this).apply {
   orientation = LinearLayout.VERTICAL
   setBackgroundColor(0xfffafafa.toInt())
  }
  controls = LinearLayout(this).apply {
   orientation = LinearLayout.HORIZONTAL
   setBackgroundColor(0xffb3261e.toInt())
  }
  browser = WebView(this).apply {
   settings.javaScriptEnabled = true
   settings.domStorageEnabled = true
   settings.javaScriptCanOpenWindowsAutomatically = true
   webViewClient = WebViewClient()
   webChromeClient = WebChromeClient()
   loadUrl(startUrl)
  }
  controls.addView(Button(this).apply {
   text = "ChatGPT"
   setOnClickListener { browser.loadUrl("https://chatgpt.com") }
  }, LinearLayout.LayoutParams(0, -2, 1f))
  controls.addView(Button(this).apply {
   text = "Gemini"
   setOnClickListener { browser.loadUrl("https://gemini.google.com") }
  }, LinearLayout.LayoutParams(0, -2, 1f))
  controls.addView(Button(this).apply {
   text = "Mini window"
   setOnClickListener { enterMiniWindow() }
  }, LinearLayout.LayoutParams(0, -2, 1f))
  controls.addView(Button(this).apply {
   text = "Close"
   setOnClickListener { finish() }
  }, LinearLayout.LayoutParams(0, -2, 1f))
  root.addView(controls)
  root.addView(browser, LinearLayout.LayoutParams(-1, 0, 1f))
  setContentView(root)
  updatePipParams()
 }

 private fun pipParams(): PictureInPictureParams =
  PictureInPictureParams.Builder()
   .setAspectRatio(Rational(16, 9))
   .build()

 private fun updatePipParams() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
   val builder = PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    builder.setAutoEnterEnabled(true)
    builder.setSeamlessResizeEnabled(true)
   }
   setPictureInPictureParams(builder.build())
  }
 }

 private fun enterMiniWindow() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
   enterPictureInPictureMode(pipParams())
  }
 }

 override fun onUserLeaveHint() {
  super.onUserLeaveHint()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
   enterMiniWindow()
  }
 }

 override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
  super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
  controls.visibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
 }

 override fun onDestroy() {
  if (::browser.isInitialized) {
   browser.stopLoading()
   browser.destroy()
  }
  super.onDestroy()
 }
}