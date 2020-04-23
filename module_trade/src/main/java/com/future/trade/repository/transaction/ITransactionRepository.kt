package com.future.trade.repository.transaction

import androidx.lifecycle.LiveData
import com.fsh.common.repository.BaseRepository
import com.future.trade.bean.*
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPDirection
import com.future.trade.repository.tradeapi.*

interface ITransactionRepository : BaseRepository {
    val orderLiveData:LiveData<List<RspOrderField>>
    val withDrawLiveData:LiveData<List<RspOrderField>>
    val tradeLiveData:LiveData<List<RspTradeField>>
    val positionLiveData:LiveData<List<Position>>
    val tradingAccountLiveData:LiveData<RspTradingAccountField>
    /**
     * 根据合约ID和方向获取持仓
     */
    fun getPositionByInstrumentId(instrumentId:String,direction:CTPDirection? = null) : Position?

    /**
     * 查询委托响应
     */
    fun handleRspQryOrderEvent(event: RspQryOrderEvent)

    /**
     * 委托回报
     */
    fun handleRtnOrderEvent(event: RtnOrderEvent)

    /**
     * 报单响应
     */
    fun handleRspOrderInsertEvent(event: RspOrderInsertEvent)

    /**
     * 撤单响应
     */
    fun handleRspOrderActionEvent(event: RspOrderActionEvent)

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
    //交易账号退出登录
    fun onUserLogout()
}