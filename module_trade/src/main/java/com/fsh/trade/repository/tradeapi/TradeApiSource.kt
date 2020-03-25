package com.fsh.trade.repository.tradeapi

import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import io.reactivex.subjects.Subject

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 交易API
 *
 */

interface TradeApiSource {
    /**
     * 初始化Api
     */
    fun initTradeApi()

    /**
     * 请求认证
     */
    fun reqAuthenticate()

    /**
     * 请求登录
     */
    fun reqUserLogin(brokerConfig: BrokerConfig, account: TradeAccountConfig)

    /**
     * 请求退出
     */
    fun reqUserLogout()

    fun registerSubject(publish:Subject<TradeEvent>)
}