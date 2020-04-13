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