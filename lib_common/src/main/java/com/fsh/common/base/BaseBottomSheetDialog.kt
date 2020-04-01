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
    var isShowing:Boolean = false
    private lateinit var mBehavior:BottomSheetBehavior<View>
    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING && !enableBottomSheetBehavior()) {
                mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float){}
    }
    var dismissListener:DialogInterface.OnDismissListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWindowHeight()
        isShowing = true
    }

    private fun setWindowHeight(){
        if(dialog != null){
            val bottomSheet = dialog!!.findViewById<View>(R.id.design_bottom_sheet)
            if(bottomSheet != null && bottomSheet.layoutParams != null){
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }else{
                Log.w("BaseBottomSheetDialog","layout params null")
            }
            dialog!!.setCancelable(dialogCancelable())
        }

        view!!.post {
            val layoutParams: CoordinatorLayout.LayoutParams = (view!!.parent as View).layoutParams as CoordinatorLayout.LayoutParams
            mBehavior = layoutParams.behavior as BottomSheetBehavior<View>
            mBehavior.setBottomSheetCallback(mBottomSheetCallback)
            val display = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(display)
            val displayHeight = (display.heightPixels*getBehaviorPeekHeight()).toInt()
            mBehavior.peekHeight = displayHeight
        }
    }

    open fun getBehaviorPeekHeight():Float = 0.4f

    open fun enableBottomSheetBehavior():Boolean = true

    open fun dialogCancelable():Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutRes(),container,false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShowing = false
        dismissListener?.onDismiss(dialog)
    }

    override fun dismiss() {
        if(isShowing){
            super.dismiss()
        }
    }

    @LayoutRes
    abstract fun getLayoutRes():Int
}