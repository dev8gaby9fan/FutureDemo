package com.fsh.common.base

import android.util.Log


abstract class BaseLazyFragment : BaseFragment(){
    private var isLoaded:Boolean = false

    override fun onResume() {
        super.onResume()
        Log.d("BaseLazyFragment","${javaClass.simpleName} onResume")
        if(!isLoaded && !isHidden){
            lazyLoading()
            isLoaded = true
        }
        if(!isHidden){
            onVisible()
        }
    }

    //界面可见
    open fun onVisible(){

    }

    override fun onDetach() {
        super.onDetach()
        isLoaded = false
    }

    abstract fun lazyLoading()
}