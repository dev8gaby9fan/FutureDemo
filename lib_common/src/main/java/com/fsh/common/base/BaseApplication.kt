package com.fsh.common.base

import android.app.Application
import android.content.Context
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.util.CommonUtil

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: Application基类
 *
 */

open class BaseApplication : Application(){
    val application:BaseApplication by lazy {
        this
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initArouter()
        CommonUtil.application = this
    }

    private fun initArouter(){
        if(BuildConfig.DEBUG){
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ActivityManager.quitApp()
    }
}