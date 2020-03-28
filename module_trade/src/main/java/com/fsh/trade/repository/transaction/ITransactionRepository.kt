package com.fsh.trade.repository.transaction

import androidx.lifecycle.LiveData
import com.fsh.common.repository.BaseRepository
import com.fsh.trade.bean.InstrumentPosition
import com.fsh.trade.bean.RspOrderField
import com.fsh.trade.bean.RspTradeField
import com.fsh.trade.bean.RspTradingAccountField
import com.fsh.trade.repository.tradeapi.*

interface ITransactionRepository : BaseRepository {
    val orderLiveData:LiveData<List<RspOrderField>>
    val withDrawLiveData:LiveData<List<RspOrderField>>
    val tradeLiveData:LiveData<List<RspTradeField>>
    val positionLiveData:LiveData<List<InstrumentPosition>>
    val tradingAccountLiveData:LiveData<RspTradingAccountField>
    /**
     * 查询委托响应
     */
    fun handleRspQryOrderEvent(event: RspQryOrderEvent)

    /**
     * 委托回报
     */
    fun handleRtnOrderEvent(event: RtnOrderEvent)

    /**
     * 查询成交响应
     */
    fun handleRspQryTradeEvent(event: RspQryTradeEvent)

    /**
     * 成交回报
     */
    fun handleRtnTradeEvent(event: RtnTradeEvent)

    /**
     * 查询持仓明细响应
     */
    fun handleRspQryPositionDetailEvent(event: RspQryPositionDetailEvent)

    /**
     * 查询资金响应
     */
    fun handleRspQryTradingAccountEvent(event: RspQryTradingAccountEvent)
}