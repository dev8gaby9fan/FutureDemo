package com.future.trade.repository.tradeapi

import com.fsh.common.repository.BaseRepository
import com.future.trade.bean.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

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
    private val currentUser: AtomicReference<RspUserLoginField> = AtomicReference()
    private val orderRef:AtomicInteger = AtomicInteger(0)
    private val orderReqId:AtomicInteger = AtomicInteger(0)
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

    fun reqOrderInsert(insertField: IOrderInsertField){
        tradeApiSource.reqOrderInsert(insertField)
        //这里手动创建一条报单记录，处理一下冻结手数
        val localRtnOrderEvent = insertField.toRtnOrderEvent()
        tradeEventPublish.onNext(localRtnOrderEvent)
    }

    fun reqOrderAction(actionField:CTPInputOrderActionField){
        tradeApiSource.reqOrderAction(actionField)
    }

    fun reqQryPositionDetail(){
        tradeApiSource.reqQryPositionDetail()
    }

    fun reqQryTradingAccount(){
        tradeApiSource.reqQryTradingAccount()
    }

    fun getTradeEventObserver():Observable<TradeEvent> = tradeEventPublish

    fun onUserLoginSuccess(session:RspUserLoginField){
        currentUser.compareAndSet(currentUser.get(),session)
        orderRef.set(session.maxOrderRef.toInt())
        orderReqId.set(0)
    }

    fun isUserLogined():Boolean{
        return currentUser.get() != null
    }

    fun onUserLogout(){
        currentUser.compareAndSet(currentUser.get(),null)
    }

    fun getOrderRefId():Int = orderRef.getAndIncrement()

    fun getOrderReqId():Int = orderReqId.getAndIncrement()

    fun getCurrentUser():RspUserLoginField?{
        return currentUser.get()
    }
}