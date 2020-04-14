package com.fsh.common.base


abstract class BaseLazyFragment : BaseFragment(){
    private var isLoaded:Boolean = false

    override fun onResume() {
        super.onResume()
        if(!isLoaded && !isHidden){
            lazyLoading()
            isLoaded = true
        }
        if(!isHidden){
            onVisible()
        }else{
            onInVisible()
        }
    }

    //界面可见
    open fun onVisible(){

    }
    //界面不可见
    open fun onInVisible(){

    }

    override fun onDetach() {
        super.onDetach()
        isLoaded = false
    }

    abstract fun lazyLoading()
}