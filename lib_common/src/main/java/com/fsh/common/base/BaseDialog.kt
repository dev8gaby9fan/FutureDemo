package com.fsh.common.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseDialog : DialogFragment(){
    var isLoading:Boolean = false
    override fun onStart() {
        super.onStart()
        if(dialog != null){
            dialog!!.window!!.setLayout(getLayoutWidth(),ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutRes(),container,false)
    }

    @LayoutRes
    abstract fun getLayoutRes():Int

    open fun getLayoutWidth():Int{
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return (displayMetrics.widthPixels*0.75).toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLoading = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoading = false
    }

    override fun dismiss() {
        if(isLoading){
            super.dismiss()
        }
    }
}