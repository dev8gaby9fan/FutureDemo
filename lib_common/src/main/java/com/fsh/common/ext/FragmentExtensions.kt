package com.fsh.common.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.fsh.common.base.ViewModelFactory

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: Fragment扩展函数
 *
 */

inline fun <reified VM : ViewModel> Fragment.viewModelOf(useActivity: Boolean = false): Lazy<VM> {
    return if (useActivity) {
        activityViewModels { ViewModelFactory(requireActivity(), lifecycle) }
    } else {
        viewModels { ViewModelFactory(context!!, lifecycle) }
    }
}

inline fun Fragment.addChildFragment(frameId:Int,fragment:Fragment){
    childFragmentManager.beginTransaction()
        .add(frameId,fragment)
        .commit()
}

inline fun Fragment.findFragmentById(frameId:Int):Fragment?{
    return childFragmentManager.findFragmentById(frameId)
}

inline fun Fragment.findFragmentByTag(tag:String?):Fragment?{
    return childFragmentManager.findFragmentByTag(tag)
}