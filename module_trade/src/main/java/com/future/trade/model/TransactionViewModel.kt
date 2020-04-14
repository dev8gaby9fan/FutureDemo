package com.future.trade.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.ARouterUtils
import com.future.trade.bean.*
import com.future.trade.repository.TradeApiProvider
import com.future.trade.repository.tradeapi.*
import com.future.trade.repository.transaction.ITransactionRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class TransactionViewModel : BaseViewModel<TradeApiRepository>(){
    private var isQueriedOrder:Boolean = false
    private val disposable:CompositeDisposable = CompositeDisposable()
    private val transactionRepository:ITransactionRepository = TradeApiProvider.providerTransactionRepository()
    val orderLiveData:LiveData<List<RspOrderField>> = transactionRepository.orderLiveData
    val withDrawOrderLiveData:LiveData<List<RspOrderField>> = transactionRepository.withDrawLiveData
    val positionLiveData:LiveData<List<InstrumentPosition>> = transactionRepository.positionLiveData
    val tradeLiveData:LiveData<List<RspTradeField>> = transactionRepository.tradeLiveData
    val tradingAccountLiveData:LiveData<RspTradingAccountField> = transactionRepository.tradingAccountLiveData
    val quoteData: Observable<QuoteEntity> = ARouterUtils.getQuoteService().getSubscribeQuoteObservable()
    init {
        repository = TradeApiProvider.providerCTPTradeApi().apply {
            disposable.add(getTradeEventObserver().subscribe {
                when(it){
                    is RspQryOrderEvent -> transactionRepository.handleRspQryOrderEvent(it)
                    is RspOrderInsertEvent -> transactionRepository.handleRspOrderInsertEvent(it)
                    is RspOrderActionEvent -> transactionRepository.handleRspOrderActionEvent(it)
                    is RtnOrderEvent -> transactionRepository.handleRtnOrderEvent(it)
                    is RspQryTradeEvent -> transactionRepository.handleRspQryTradeEvent(it)
                    is RtnTradeEvent -> transactionRepository.handleRtnTradeEvent(it)
                    is RspQryPositionDetailEvent -> transactionRepository.handleRspQryPositionDetailEvent(it)
                    is RspQryTradingAccountEvent -> transactionRepository.handleRspQryTradingAccountEvent(it)
                    is RspUserLogoutEvent -> handleRspUserLogout(it.rsp)
                }
            })
        }
    }

    private fun handleRspUserLogout(rsp: RspUserLogout){
        //TODO 这里需要将对应的列表数据清空
        repository?.onUserLogout()
    }

    /**
     *  查询委托
     */
    fun reqQryOrder(){
        repository?.reqQryOrder()
        isQueriedOrder = true
        Log.d("TransactionViewModel","reqQryOrder")
    }

    /**
     * 查询成交
     */
    fun reqQryTrade(){
        repository?.reqQryTrade()
        Log.d("TransactionViewModel","reqQryTrade")
    }

    /**
     * 查询持仓明细
     */
    fun reqQryPositionDetail(){
        repository?.reqQryPositionDetail()
        Log.d("TransactionViewModel","reqQryPositionDetail")
    }

    fun reqQryTradingAccount(){
        repository?.reqQryTradingAccount()
    }

    /**
     * 报单录入
     */
    fun reqOrderInsert(filed:IOrderInsertField){
        repository?.reqOrderInsert(filed)
    }

    /**
     * 撤单
     */
    fun reqOrderAction(field:CTPInputOrderActionField){
        repository?.reqOrderAction(field)
    }

    /**
     * 订阅单独的行情
     */
    fun subscribeQuote(insId:String){
        val quoteService = ARouterUtils.getQuoteService()
        quoteService.subscribeQuote(insId,true)
    }
}