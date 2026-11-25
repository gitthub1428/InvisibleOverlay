package com.example.floatinghub
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity: Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(36,48,36,24) }
  layout.addView(TextView(this).apply { text="Floating Hub\n\nLaunch a movable overlay with a mini web panel. It is not designed to hide from recording or monitoring."; textSize=18f })
  layout.addView(Button(this).apply { text="Grant overlay permission"; setOnClickListener { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) } })
  layout.addView(Button(this).apply { text="Start floating window"; setOnClickListener {
   if (Settings.canDrawOverlays(this@MainActivity)) startService(Intent(this@MainActivity,OverlayService::class.java))
   else startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$packageName")))
  } })
  layout.addView(Button(this).apply { text="Stop floating window"; setOnClickListener { stopService(Intent(this@MainActivity,OverlayService::class.java)) } })
  setContentView(layout)
 }
}