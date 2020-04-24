package com.fsh.common.util

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.fsh.common.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun show(activity:Activity,message:String,@BaseTransientBottomBar.Duration duration:Int){
        val content = activity.window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(content, message, duration)
        snackbar.show()
    }
}