package com.fsh.common.base

import androidx.lifecycle.ViewModel
import com.fsh.common.repository.BaseRepository

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: ViewModel基类,暂时还未想好里面要封装哪些功能
 *
 */

abstract class BaseViewModel<RE: BaseRepository> : ViewModel() , CommonLifeCycleObserver{
    var repository:RE? = null

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }
}