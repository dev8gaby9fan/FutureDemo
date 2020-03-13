package com.fsh.trade.ui.login

import com.fsh.common.base.BaseViewModel
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig

import com.fsh.trade.repository.TradeApiProvider
import com.fsh.trade.repository.tradeapi.TradeApiRepository

class LoginViewModel : BaseViewModel<TradeApiRepository>(){
    init {
        repository = TradeApiProvider.providerCTPTradeApi()
    }

    fun reqUserLogin(tradeAccount:TradeAccountConfig,broker:BrokerConfig){
        repository!!.reqUserLogin(broker,tradeAccount)
    }
}
