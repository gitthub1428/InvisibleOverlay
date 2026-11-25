package com.example.floatinghub

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var root: LinearLayout? = null
    private var web: WebView? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun params(width: Int, height: Int, flags: Int): WindowManager.LayoutParams =
        WindowManager.LayoutParams(
            width,
            height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            flags,
            PixelFormat.TRANSLUCENT
        )

    private fun showBubble() {
        val b = TextView(this).apply {
            text = "FH"
            textSize = 18f
            setTextColor(0xffffffff.toInt())
            setBackgroundColor(0xffb3261e.toInt())
            gravity = Gravity.CENTER
            setPadding(18, 12, 18, 12)
            contentDescription = "Open Floating Hub"
        }
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        val p = params(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            flags
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 180
        }
        bubble = b
        wm.addView(b, p)

        b.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0f
            private var startY = 0f
            private var startWindowX = 0
            private var startWindowY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.rawX
                        startY = event.rawY
                        startWindowX = p.x
                        startWindowY = p.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        p.x = startWindowX - (event.rawX - startX).toInt()
                        p.y = startWindowY + (event.rawY - startY).toInt()
                        wm.updateViewLayout(b, p)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (abs(event.rawX - startX) < 12 && abs(event.rawY - startY) < 12) {
                            showPanel()
                        }
                        return true
                    }
                }
                return true
            }
        })
    }

    private fun closePanel() {
        root?.let { panel ->
            try { wm.removeView(panel) } catch (_: Exception) { }
        }
        web?.let { view ->
            try {
                view.stopLoading()
                view.loadUrl("about:blank")
                view.removeAllViews()
                view.destroy()
            } catch (_: Exception) { }
        }
        web = null
        root = null
    }

    private fun showPanel() {
        closePanel()

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xfffafafa.toInt())
        }
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xffb3261e.toInt())
        }

        val browser = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            webViewClient = WebViewClient()
            loadUrl("https://chatgpt.com")
        }
        web = browser

        bar.addView(Button(this).apply {
            text = "ChatGPT"
            setOnClickListener { browser.loadUrl("https://chatgpt.com") }
        }, LinearLayout.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT, 1f))
        bar.addView(Button(this).apply {
            text = "Gemini"
            setOnClickListener { browser.loadUrl("https://gemini.google.com") }
        }, LinearLayout.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT, 1f))
        bar.addView(Button(this).apply {
            text = "Close"
            setOnClickListener { closePanel() }
        }, LinearLayout.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT, 1f))

        panel.addView(bar)
        panel.addView(
            browser,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.88f).toInt()
        val height = (metrics.heightPixels * 0.72f).toInt()
        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        root = panel
        wm.addView(panel, params(width, height, flags).apply {
            gravity = Gravity.CENTER
        })
    }

    override fun onDestroy() {
        closePanel()
        bubble?.let { view ->
            try { wm.removeView(view) } catch (_: Exception) { }
        }
        bubble = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}