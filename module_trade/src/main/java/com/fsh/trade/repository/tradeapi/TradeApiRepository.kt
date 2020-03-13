package com.fsh.trade.repository.tradeapi

import com.fsh.common.repository.BaseRepository
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 交易Repository
 *
 */

abstract class TradeApiRepository(tradeApiSource: TradeApiSource) : BaseRepository{

    abstract fun reqAuthenticate()

    abstract fun reqUserLogin(brokerConfig: BrokerConfig,account:TradeAccountConfig)

    abstract fun initTradeApi()

    abstract fun reqUserLogout()
}