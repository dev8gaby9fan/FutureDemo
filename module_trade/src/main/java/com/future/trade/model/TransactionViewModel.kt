package com.future.trade.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.ARouterUtils
import com.future.trade.bean.*
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.Position
import com.future.trade.repository.TradeApiProvider
import com.future.trade.repository.tradeapi.*
import com.future.trade.repository.transaction.ITransactionRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class TransactionViewModel : BaseViewModel<TradeApiRepository>(){
    private var isQueriedOrder:Boolean = false
    private val disposable:CompositeDisposable = CompositeDisposable()
    val orderLiveData:LiveData<List<RspOrderField>>
    val withDrawOrderLiveData:LiveData<List<RspOrderField>>
    val positionLiveData:LiveData<List<Position>>
    val tradeLiveData:LiveData<List<RspTradeField>>
    val tradingAccountLiveData:LiveData<RspTradingAccountField>
    val quoteData: Observable<QuoteEntity> = ARouterUtils.getQuoteService().getSubscribeQuoteObservable()
    init {
        repository = TradeApiProvider.providerCTPTradeApi().apply {
            disposable.add(getTradeEventObserver().subscribe {
                when(it){
                    is RspUserLogoutEvent -> handleRspUserLogout(it.rsp)
                }
            })
        }
        orderLiveData = repository!!.transactionRepository.orderLiveData
        withDrawOrderLiveData = repository!!.transactionRepository.withDrawLiveData
        positionLiveData = repository!!.transactionRepository.positionLiveData
        tradeLiveData = repository!!.transactionRepository.tradeLiveData
        tradingAccountLiveData = repository!!.transactionRepository.tradingAccountLiveData
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
    fun reqOrderAction(field:IInputOrderActionField){
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