package com.future.quote.event

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 行情模块的事件
 *
 */

data class BaseEvent(var action:Int){
    companion object{
        const val ACTION_LOAD_INS_OK = 10001
        const val ACTION_LOAD_INS_FAIL = 10002
    }
}

