package com.fsh.trade.repository

import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import com.fsh.trade.repository.tradeapi.CTPTradeApi
import com.fsh.trade.repository.tradeapi.TradeApiRepository

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 交易登录Repository
 */

class CTPTradeApiRepository(var api:CTPTradeApi) : TradeApiRepository(api){
    override fun reqUserLogin(brokerConfig: BrokerConfig, account: TradeAccountConfig) {
        api.reqUserLogin(brokerConfig,account)
    }

    override fun initTradeApi() {
        api.initTradeApi()
    }

    override fun reqUserLogout() {

    }

    override fun reqAuthenticate() {

    }

}