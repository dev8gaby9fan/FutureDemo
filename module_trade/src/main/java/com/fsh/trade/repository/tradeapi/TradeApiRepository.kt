package com.fsh.trade.repository.tradeapi

import com.fsh.common.repository.BaseRepository
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 交易Repository
 *
 */

abstract class TradeApiRepository(var tradeApiSource: TradeApiSource) : BaseRepository{
    private val tradeEventPublish:Subject<TradeEvent> = PublishSubject.create()

    init {
        tradeApiSource.registerSubject(tradeEventPublish)
    }
    fun reqAuthenticate(){
        tradeApiSource.reqAuthenticate()
    }

    fun reqUserLogin(brokerConfig: BrokerConfig,account:TradeAccountConfig){
        tradeApiSource.reqUserLogin(brokerConfig,account)
    }

    fun initTradeApi(){
        tradeApiSource.initTradeApi()
    }

    fun reqUserLogout(){
        tradeApiSource.reqUserLogout()
    }
    /** 请求确认结算单*/
    fun reqConfirmSettlement(){
        tradeApiSource.reqConfirmSettlement()
    }

    fun reqQryConfirmSettlement(){
        tradeApiSource.reqQryConfirmSettlement()
    }

    fun reqQryOrder(){
        tradeApiSource.reqQryOrder()
    }

    fun reqQryTrade(){
        tradeApiSource.reqQryTrade()
    }

    fun reqOrderInsert(){
        tradeApiSource.reqOrderInsert()
    }

    fun reqOrderAction(){
        tradeApiSource.reqOrderAction()
    }

    fun reqQryPositionDetail(){
        tradeApiSource.reqQryPositionDetail()
    }

    fun getTradeEventObserver():Observable<TradeEvent> = tradeEventPublish

}