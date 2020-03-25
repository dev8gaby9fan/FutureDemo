package com.fsh.common.base

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.DateUtils
import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.io.PrintWriter

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: Application基类
 *
 */

open class BaseApplication : Application(),Thread.UncaughtExceptionHandler{
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.d("BaseApplication","crash occur ${filesDir.absolutePath}")
//        val crashLogFile = File(filesDir, DateUtils.formatNow1())
//        crashLogFile.delete()
//        crashLogFile.createNewFile()
//        val fileWriter = FileWriter(crashLogFile)
//        e.printStackTrace(PrintWriter(fileWriter,true))
        e.printStackTrace()
    }

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
        Thread.setDefaultUncaughtExceptionHandler(this)
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