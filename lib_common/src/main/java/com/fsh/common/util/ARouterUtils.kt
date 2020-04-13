@file:Suppress("UNCHECKED_CAST")

package com.fsh.common.util

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.model.ARouterPath
import com.fsh.common.provider.QuoteService
import com.fsh.common.provider.TradeService

object ARouterUtils {

    private fun getARouter():ARouter = ARouter.getInstance()

    fun <T> getARouteComponent(path:String):T = getARouter().build(path).navigation() as T

    fun getQuoteService():QuoteService = getARouteComponent(ARouterPath.Service.SERVICE_QUOTE)

    fun getTradeService():TradeService = getARouteComponent(ARouterPath.Service.SERVICE_TRADE)
}