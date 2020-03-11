package com.fsh.common.base

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: ViewModel创建工厂
 *
 */

class ViewModelFactory(private var context: Context,private var lifeCycle: Lifecycle) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val instance = modelClass.newInstance()
        if(instance is BaseViewModel<*>){
            lifeCycle.addObserver(instance as BaseViewModel<*>)
        }
        return instance
    }

}