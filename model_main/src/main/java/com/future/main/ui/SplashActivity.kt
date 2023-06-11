package com.future.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.fsh.common.base.BaseActivity
import com.future.main.R

class SplashActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_splash

    override fun getStatusBarColorRes(): Int = android.R.color.transparent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.postDelayed({
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            finish()
        },1000)
    }

    override fun setWindowFlags() {
        super.setWindowFlags()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN)
        window.addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window.addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

}
