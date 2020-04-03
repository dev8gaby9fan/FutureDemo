package com.future.trade.repository

import com.future.trade.bean.RspUserLoginField
import com.future.trade.repository.tradeapi.CTPTradeApi
import com.future.trade.repository.tradeapi.TradeApiRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 交易登录Repository
 */

class CTPTradeApiRepository(api:CTPTradeApi) : TradeApiRepository(api){

}