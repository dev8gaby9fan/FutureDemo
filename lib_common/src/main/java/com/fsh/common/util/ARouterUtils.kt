package com.fsh.common.util

import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.model.ArouterPath
import com.fsh.common.provider.QuoteService
import com.fsh.common.provider.TradeService

object ARouterUtils {

    private fun getARouter():ARouter = ARouter.getInstance()

    fun <T> getARouteComponent(path:String):T = getARouter().build(ArouterPath.SERVICE_QUOTE).navigation() as T

    fun getQuoteService():QuoteService? = getARouteComponent(ArouterPath.SERVICE_QUOTE)

    fun getTradeService():TradeService = getARouteComponent(ArouterPath.SERVICE_TRADE)
}