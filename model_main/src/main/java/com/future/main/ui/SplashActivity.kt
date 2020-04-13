package com.future.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        },3000)
    }
}
