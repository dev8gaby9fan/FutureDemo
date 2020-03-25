package com.fsh.common.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.fsh.common.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 从底部弹出的对话框
 */
abstract class BaseBottomSheetDialog : BottomSheetDialogFragment(){
//    private lateinit var mBehavior:View
    var dismissListener:DialogInterface.OnDismissListener? = null

    override fun onStart() {
        super.onStart()
        setWindowHeight()
    }
    private fun setWindowHeight(){
        if(dialog != null){
            val bottomSheet = dialog!!.findViewById<View>(R.id.design_bottom_sheet)
            if(bottomSheet != null && bottomSheet.layoutParams != null){
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }else{
                Log.w("BaseBottomSheetDialog","layout params null")
            }
        }

        view!!.post {
            val layoutParams: CoordinatorLayout.LayoutParams = (view!!.parent as View).layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as BottomSheetBehavior
            val display = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(display)
            val displayHeight = (display.heightPixels*0.4).toInt()
            behavior.peekHeight = displayHeight
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutRes(),container,false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

    @LayoutRes
    abstract fun getLayoutRes():Int
}