package com.future.trade.bean

/**
 * 需要区分今昨仓的仓位
 * 目前有：上期所和能源所
 */
class YTDInstrumentPosition : InstrumentPosition(){
    override fun handleRspOrderField(rsp: RspOrderField) {

    }

    override fun handleRspTradeField(rsp: RspTradeField) {

    }

    override fun handleRspQryPositionDetail(rsp: RspPositionDetailField) {

    }
}