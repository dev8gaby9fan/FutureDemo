package com.fsh.trade.bean

/**
 * 其他类型的持仓，标准模式
 *  不区分今昨仓，投机套包严格区分
 */
class StandardInstrumentPosition : InstrumentPosition(){
    override fun handleRspOrderField(rsp: RspOrderField) {
        
    }

    override fun handleRspTradeField(rsp: RspTradeField) {
        
    }

    override fun handleRspQryPositionDetail(rsp: RspPositionDetailField) {
        
    }

}