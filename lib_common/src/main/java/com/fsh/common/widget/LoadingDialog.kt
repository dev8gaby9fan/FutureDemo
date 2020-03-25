package com.fsh.common.widget

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import com.fsh.common.R
import com.fsh.common.base.BaseDialog
import com.fsh.common.util.Omits
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog : BaseDialog(){
    private var message:String = Omits.OmitString
    override fun getLayoutRes(): Int = R.layout.dialog_loading
    override fun getLayoutWidth(): Int {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return (displayMetrics.widthPixels*0.4).toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_message.text = message
    }

    fun setMessage(msg:String){
        message = msg
        if(isLoading){
            tv_message.text = message
        }
    }


}