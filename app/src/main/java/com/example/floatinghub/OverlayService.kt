package com.example.floatinghub
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*

class OverlayService: Service() {
 private lateinit var wm: WindowManager
 private var root: LinearLayout?=null
 private lateinit var web:WebView
 override fun onCreate() { super.onCreate(); wm=getSystemService(WINDOW_SERVICE) as WindowManager; showBubble() }
 private fun params(w:Int,h:Int)=WindowManager.LayoutParams(w,h,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT)
 private fun showBubble(){
  val b=TextView(this).apply { text="FH"; textSize=18f; setTextColor(0xffffffff.toInt()); setBackgroundColor(0xffb3261e.toInt()); gravity=Gravity.CENTER; setPadding(20,14,20,14) }
  val p=params(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT).apply { gravity=Gravity.TOP or Gravity.END; x=32; y=220 }
  wm.addView(b,p)
  b.setOnTouchListener(object: View.OnTouchListener {
   var sx=0f; var sy=0f; var ox=0; var oy=0
   override fun onTouch(v:View,e:MotionEvent):Boolean { when(e.action){
    MotionEvent.ACTION_DOWN->{sx=e.rawX;sy=e.rawY;ox=p.x;oy=p.y;return true}
    MotionEvent.ACTION_MOVE->{p.x=ox-(e.rawX-sx).toInt();p.y=oy+(e.rawY-sy).toInt();wm.updateViewLayout(b,p);return true}
    MotionEvent.ACTION_UP->{ if (kotlin.math.abs(e.rawX-sx)<12 && kotlin.math.abs(e.rawY-sy)<12) showPanel();return true}
   };return true }
  })
 }
 private fun showPanel(){
  root?.let { try { wm.removeView(it) } catch(_:Exception){} }
  val panel=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(0xfffafafa.toInt()) }
  val bar=LinearLayout(this).apply { setBackgroundColor(0xffb3261e.toInt()) }
  bar.addView(Button(this).apply { text="ChatGPT";setOnClickListener { web.loadUrl("https://chatgpt.com") } })
  bar.addView(Button(this).apply { text="Gemini";setOnClickListener { web.loadUrl("https://gemini.google.com") } })
  bar.addView(Button(this).apply { text="Close";setOnClickListener { root?.let{wm.removeView(it)};root=null } })
  panel.addView(bar)
  web=WebView(this).apply { settings.javaScriptEnabled=true;settings.domStorageEnabled=true;webViewClient=WebViewClient();loadUrl("https://chatgpt.com") }
  panel.addView(web,LinearLayout.LayoutParams(-1,0,1f))
  root=panel;wm.addView(panel,params(900,1200).apply { gravity=Gravity.CENTER })
 }
 override fun onDestroy(){root?.let{try{wm.removeView(it)}catch(_:Exception){}};root=null;super.onDestroy()}
 override fun onBind(intent:Intent?):IBinder?=null
}