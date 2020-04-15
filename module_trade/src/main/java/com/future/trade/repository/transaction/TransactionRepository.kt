package com.future.trade.repository.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.future.trade.bean.RspOrderField
import com.future.trade.bean.RspTradeField
import com.future.trade.bean.RspTradingAccountField
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.Position
import com.future.trade.repository.tradeapi.*

/**
 * 交易业务处理的Repository
 */
class TransactionRepository : ITransactionRepository {
    private val orderDataHandler:IOrderHandler = OrderDataHandler()
    private val tradeDataHandler:ITradeDataHandler = TradeDataHandler()
    private val positionDataHandler:IPositionDataHandler = PositionDataHandler()

    override val orderLiveData: LiveData<List<RspOrderField>> = orderDataHandler.getLiveData()
    override val withDrawLiveData: LiveData<List<RspOrderField>> = orderDataHandler.getWithDrawLiveData()
    override val tradeLiveData: LiveData<List<RspTradeField>> = tradeDataHandler.getLiveData()
    override val positionLiveData: LiveData<List<Position>> = positionDataHandler.getLiveData()
    override val tradingAccountLiveData:MutableLiveData<RspTradingAccountField> = MutableLiveData()

    override fun handleRspQryOrderEvent(event: RspQryOrderEvent) {
        Log.d("TransactionRepository","handleRspQryOrderEvent ${event.rsp.rspInfoField.errorMsg}  ${event.rsp.rspInfoField.errorID}")
        orderDataHandler.handleRspQryOrder(event.rsp)
        //持仓也需要处理委托响应，计算仓位冻结手数
        positionDataHandler.handleRspQryOrder(event.rsp)
    }

    override fun handleRtnOrderEvent(event: RtnOrderEvent) {
        orderDataHandler.handleRtnOrder(event.rtn)
        //持仓也需要处理委托响应，计算仓位冻结手数
        positionDataHandler.handleRtnOrder(event.rtn)
    }
    override fun handleRspOrderInsertEvent(event: RspOrderInsertEvent) {
        orderDataHandler.handleRspOrderInsert(event.rsp)
        positionDataHandler.handleRspOrderInsert(event.rsp)
    }

    override fun handleRspOrderActionEvent(event: RspOrderActionEvent) {
        orderDataHandler.handleRspOrderAction(event.rsp)
        positionDataHandler.handleRspOrderAction(event.rsp)
    }

    override fun handleRspQryTradeEvent(event: RspQryTradeEvent) {
        tradeDataHandler.handleRspQryTrade(event.rsp)
    }

    override fun handleRtnTradeEvent(event: RtnTradeEvent) {
        tradeDataHandler.handleRtnQryTrade(event.rtn)
        //持仓也需要处理成交回报，计算仓位
        positionDataHandler.handleRtnTrade(event.rtn)
    }

    override fun handleRspQryPositionDetailEvent(event: RspQryPositionDetailEvent) {
        positionDataHandler.handleRspQryPositionDetail(event.rsp)
    }

    override fun handleRspQryTradingAccountEvent(event: RspQryTradingAccountEvent) {
        //资金数据就直接往丢，不用处理
        tradingAccountLiveData.postValue(event.rsp.rspField)
    }
}