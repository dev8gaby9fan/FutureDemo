package com.fsh.common.util

import com.fsh.common.base.BaseActivity
import com.fsh.common.base.BaseApplication

object SizeUtils {

    fun px2dp(px:Int):Int{
        return (px/ CommonUtil.application?.resources?.displayMetrics?.density!! +0.5).toInt()
    }

    fun dp2px(dp:Int):Int{
        return (dp * CommonUtil.application?.resources?.displayMetrics?.density!! +0.5).toInt()
    }
}