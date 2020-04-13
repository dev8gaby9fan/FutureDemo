package com.future.main.ui.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.model.ARouterPath
import com.fsh.common.provider.MainService

@Route(path = ARouterPath.Service.SERVICE_MAIN)
class MainServiceImpl : MainService{
    private val pageLiveData:MutableLiveData<Int> = MutableLiveData()
    override fun switchTabPage(index: Int) {
        pageLiveData.postValue(index)
    }

    override fun getSwitchPageLiveData(): LiveData<Int> = pageLiveData

    override fun init(context: Context?) {

    }

}