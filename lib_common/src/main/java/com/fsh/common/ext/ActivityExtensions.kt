package com.fsh.common.ext

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.ViewModelFactory
/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: Activity的扩展函数
 *
 */

inline fun <reified VM : ViewModel> BaseActivity.viewModelOf(): Lazy<VM> {
    return viewModels { ViewModelFactory(this, lifecycle) }
}

fun FragmentActivity.addFragment(frameId:Int,fragment:Fragment){
    supportFragmentManager.beginTransaction()
        .add(frameId,fragment)
        .commit()
}