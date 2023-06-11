package com.fsh.common.base

import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.BuildConfig
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

open class BaseApplication : Application(), Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.d("BaseApplication", "crash occur ${filesDir.absolutePath}", e)
//        val crashLogFile = File(filesDir, DateUtils.formatNow1())
//        crashLogFile.delete()
//        crashLogFile.createNewFile()
//        val fileWriter = FileWriter(crashLogFile)
//        e.printStackTrace(PrintWriter(fileWriter,true))
        System.exit(0)
        Process.killProcess(Process.myPid())
    }

    val application: BaseApplication by lazy {
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
        Thread.setDefaultUncaughtExceptionHandler(this)
        if (BuildConfig.DEBUG) {
//            DoraemonKit.install(this, null, "c6062411b36abf47c54c365e4f7e5a11")
//            DoraemonKit.setDebug(false)
        }
    }

    private fun initArouter() {
        ARouter.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ActivityManager.quitApp()
    }
}