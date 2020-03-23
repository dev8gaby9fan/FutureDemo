package com.fsh.trade.repository

import com.fsh.trade.repository.config.BrokerConfigRepository
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
    //经纪公司配置
    private val configRepository:BrokerConfigRepository by lazy { BrokerConfigRepository() }
    //CTP 交易api
    fun providerCTPTradeApi():TradeApiRepository = ctpTradeApiRepository
    //经纪公司配置项
    fun providerConfigRepository() = configRepository
}