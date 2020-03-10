package com.fsh.common.base

import java.util.*
import kotlin.system.exitProcess

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description:Activity堆栈管理
 *
 */

object ActivityManager {
    private var stack:LinkedList<BaseActivity> = LinkedList()

    @JvmStatic
    fun push(act:BaseActivity){
        stack.push(act)
    }
    @JvmStatic
    fun remove(act:BaseActivity){
        stack.remove(act)
    }
    @JvmStatic
    fun current():BaseActivity =
        stack.first()
    @JvmStatic
    fun finishCurrent(){
        stack.peekFirst()?.finish()
    }
    @JvmStatic
    fun quitApp(){
        stack.forEach {
            it.finish()
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }
}