package com.fsh.common.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.fsh.common.widget.LoadingDialog

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 基类
 *
 */

abstract class BaseActivity : AppCompatActivity(){
    private val loadingDialog: LoadingDialog = LoadingDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        ActivityManager.push(this)
        setContentView(layoutRes())
    }

    @LayoutRes
    abstract fun layoutRes():Int

    override fun onDestroy() {
        ActivityManager.remove(this)
        super.onDestroy()
    }

    fun showLoadingDialog(){
        loadingDialog.show(supportFragmentManager, TAG_LOADING)
    }
    fun setLoadingDialogMessage(@StringRes msgRes:Int){
        setLoadingDialogMessage(resources.getString(msgRes))
    }
    fun setLoadingDialogMessage(message:String){
        loadingDialog.setMessage(message)
    }

    fun dismissLoading(){
        loadingDialog.dismiss()
    }

    fun setLoadingCancelable(enableCancel:Boolean){
        loadingDialog.isCancelable = enableCancel
    }

    companion object{
        const val TAG_LOADING = "LOADING"
    }
}