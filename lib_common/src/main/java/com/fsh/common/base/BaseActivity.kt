package com.fsh.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 基类
 *
 */

abstract class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.push(this)
        setContentView(layoutRes())
    }

    @LayoutRes
    abstract fun layoutRes():Int

    override fun onDestroy() {
        ActivityManager.remove(this)
        super.onDestroy()
    }
}