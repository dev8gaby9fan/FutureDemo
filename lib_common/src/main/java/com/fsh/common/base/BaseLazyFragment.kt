package com.fsh.common.base

abstract class BaseLazyFragment : BaseFragment(){
    private var isLoaded:Boolean = false


    override fun onResume() {
        super.onResume()
        if(!isLoaded && !isHidden){
            lazyLoading()
            isLoaded = true
        }
    }

    override fun onDetach() {
        super.onDetach()
        isLoaded = false
    }

    abstract fun lazyLoading()
}