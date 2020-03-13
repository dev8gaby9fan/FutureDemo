package com.fsh.trade.repository

import com.fsh.trade.repository.tradeapi.CTPTradeApi
import com.fsh.trade.repository.tradeapi.TradeApiRepository

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 创建Repository
 */

object TradeApiProvider{
    //CTP 交易api
    private val ctpTradeApiRepository:TradeApiRepository by lazy { CTPTradeApiRepository(CTPTradeApi()) }
    //CTP 交易api
    fun providerCTPTradeApi():TradeApiRepository = ctpTradeApiRepository
}