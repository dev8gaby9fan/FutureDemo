package com.fsh.trade.repository.tradeapi

import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.IInputOrderActionField
import com.fsh.trade.bean.IOrderInsertField
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

    /**
     * 查询请求确认结算单记录
     */
    fun reqQryConfirmSettlement()
    /**
     * 确认结算单
     */
    fun reqConfirmSettlement()

    /**
     * 查询委托
     */
    fun reqQryOrder()

    /**
     * 查询成交
     */
    fun reqQryTrade()

    /**
     * 查询持仓明细
     */
    fun reqQryPositionDetail()

    /**
     * 请求报单
     */
    fun reqOrderInsert(order:IOrderInsertField)

    /**
     * 请求撤单
     */
    fun reqOrderAction(action:IInputOrderActionField)

    /**
     * 查询资金
     */
    fun reqQryTradingAccount()

    fun registerSubject(publish:Subject<TradeEvent>)
}