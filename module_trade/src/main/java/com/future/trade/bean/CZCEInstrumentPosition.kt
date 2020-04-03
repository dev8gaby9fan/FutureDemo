package com.future.trade.bean

/**
 * 郑商所的持仓
 * 郑商所的持仓不区分今昨仓，但是平仓是先平投机再平套保仓
 */
class CZCEInstrumentPosition : InstrumentPosition(){
    override fun handleRspOrderField(rsp: RspOrderField) {

    }

    override fun handleRspTradeField(rsp: RspTradeField) {
    }

    override fun handleRspQryPositionDetail(rsp: RspPositionDetailField) {
    }

}