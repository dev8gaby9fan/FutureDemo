package com.fsh.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.fsh.common.widget.LoadingDialog

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: Fragment基类
 *
 */

abstract class BaseFragment : Fragment(){
    private val loadingDialog:LoadingDialog = LoadingDialog()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes(),container,false)
    }

    @LayoutRes
    abstract fun layoutRes():Int


    fun showLoadingDialog(){
        loadingDialog.show(childFragmentManager,TAG_LOADING)
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