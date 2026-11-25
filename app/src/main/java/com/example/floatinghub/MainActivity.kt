package com.example.floatinghub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity: Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout=LinearLayout(this).apply {
   orientation=LinearLayout.VERTICAL
   setPadding(36,48,36,24)
  }
  layout.addView(TextView(this).apply {
   text="Floating Hub\n\nOpen ChatGPT or Gemini in a resizable Picture-in-Picture window. Start the mini browser, then press Home to keep it in a small window. This uses Android's supported PiP mode; other apps may still know a PiP window is present."
   textSize=18f
  })
  layout.addView(Button(this).apply {
   text="Open ChatGPT mini window"
   setOnClickListener {
    startActivity(Intent(this@MainActivity, PipActivity::class.java).putExtra(PipActivity.EXTRA_URL,"https://chatgpt.com"))
   }
  })
  layout.addView(Button(this).apply {
   text="Open Gemini mini window"
   setOnClickListener {
    startActivity(Intent(this@MainActivity, PipActivity::class.java).putExtra(PipActivity.EXTRA_URL,"https://gemini.google.com"))
   }
  })
  layout.addView(Button(this).apply {
   text="Stop old floating overlay"
   setOnClickListener { stopService(Intent(this@MainActivity,OverlayService::class.java)) }
  })
  setContentView(layout)
 }
}