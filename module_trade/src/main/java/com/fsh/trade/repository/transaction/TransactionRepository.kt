package com.fsh.trade.repository.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.trade.bean.InstrumentPosition
import com.fsh.trade.bean.RspOrderField
import com.fsh.trade.bean.RspTradeField
import com.fsh.trade.repository.tradeapi.*

/**
 * 交易业务处理的Repository
 */
class TransactionRepository : ITransactionRepository {
    override val orderLiveData: LiveData<List<RspOrderField>> = MutableLiveData()
    override val withDrawLiveData: LiveData<List<RspOrderField>> = MutableLiveData()
    override val tradeLiveData: LiveData<List<RspTradeField>> = MutableLiveData()
    override val positionLiveData: LiveData<List<InstrumentPosition>> = MutableLiveData()

    override fun handleRspQryOrderEvent(event: RspQryOrderEvent) {

    }

    override fun handleRtnOrderEvent(event: RtnOrderEvent) {

    }

    override fun handleRspQryTradeEvent(event: RspQryTradeEvent) {

    }

    override fun handleRtnTradeEvent(event: RtnTradeEvent) {

    }

    override fun handleRspQryPositionDetailEvent(event: RspQryPositionDetailEvent) {

    }

    override fun handleRspQryTradingAccountEvent(event: RspQryTradingAccountEvent) {

    }
}