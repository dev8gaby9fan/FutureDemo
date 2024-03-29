package com.future.trade.repository.tradeapi

import android.util.Log
import com.fsh.common.repository.BaseRepository
import com.future.trade.bean.*
import com.future.trade.repository.TradeApiProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    val transactionRepository = TradeApiProvider.providerTransactionRepository()
    private val tradeEventPublish:Subject<TradeEvent> = PublishSubject.create()
    private val currentUser: AtomicReference<RspUserLoginField> = AtomicReference()
    private val orderRef:AtomicInteger = AtomicInteger(0)
    private val orderReqId:AtomicInteger = AtomicInteger(0)
    private val disposables = CompositeDisposable()
    init {
        tradeApiSource.registerSubject(tradeEventPublish)
        disposables.add(tradeEventPublish.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                Log.d("TradeApiRepository","received ${it.eventType.name}")
                when(it){
                    is RspQryOrderEvent -> transactionRepository.handleRspQryOrderEvent(it)
                    is RspOrderInsertEvent -> transactionRepository.handleRspOrderInsertEvent(it)
                    is RspOrderActionEvent -> transactionRepository.handleRspOrderActionEvent(it)
                    is RtnOrderEvent -> transactionRepository.handleRtnOrderEvent(it)
                    is RspQryTradeEvent -> transactionRepository.handleRspQryTradeEvent(it)
                    is RtnTradeEvent -> transactionRepository.handleRtnTradeEvent(it)
                    is RspQryPositionDetailEvent -> {
                        transactionRepository.handleRspQryPositionDetailEvent(it)
                        if(it.rsp.bIsLast){
                            //查委托记录，计算可用仓位
                            reqQryOrder()
                        }
                    }
                    is RspQryTradingAccountEvent -> transactionRepository.handleRspQryTradingAccountEvent(it)
                    is RspUserLogoutEvent -> onUserLogout()
                }
            })
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

    /** 请求结算单数据*/
    fun reqQrySettlementInfo() {
        tradeApiSource.reqQrySettlementInfo()
    }

    fun reqQryConfirmSettlement(){
        tradeApiSource.reqQryConfirmSettlement()
    }

    private fun reqQryOrder(){
        tradeApiSource.reqQryOrder()
    }

    fun reqQryTrade(){
        tradeApiSource.reqQryTrade()
    }

    fun reqOrderInsert(insertField: IOrderInsertField){
        insertField.setOref(orderRef.getAndIncrement().toString())
        insertField.setReqId(orderReqId.getAndIncrement())
        tradeApiSource.reqOrderInsert(insertField)
        //这里手动创建一条报单记录，处理一下冻结手数
        val localRtnOrderEvent = insertField.toRtnOrderEvent()
        tradeEventPublish.onNext(localRtnOrderEvent)
    }

    fun reqOrderAction(actionField:IInputOrderActionField){
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
        transactionRepository.onUserLogout()
        orderRef.set(0)
        orderReqId.set(0)
    }

    fun getOrderRefId():Int = orderRef.getAndIncrement()

    fun getOrderReqId():Int = orderReqId.getAndIncrement()

    fun getCurrentUser():RspUserLoginField?{
        return currentUser.get()
    }
}