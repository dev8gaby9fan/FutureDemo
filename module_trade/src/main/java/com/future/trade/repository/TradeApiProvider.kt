package com.future.trade.repository

import com.future.trade.repository.config.BrokerConfigRepository
import com.future.trade.repository.tradeapi.CTPTradeApi
import com.future.trade.repository.tradeapi.TradeApiRepository
import com.future.trade.repository.transaction.ITransactionRepository
import com.future.trade.repository.transaction.TransactionRepository

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
    private val transactionRepository: ITransactionRepository by lazy { TransactionRepository() }
    //CTP 交易api
    fun providerCTPTradeApi():TradeApiRepository = ctpTradeApiRepository
    //经纪公司配置项
    fun providerConfigRepository():BrokerConfigRepository = configRepository

    fun providerTransactionRepository():ITransactionRepository = transactionRepository
}