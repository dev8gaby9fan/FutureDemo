package com.fsh.trade.bean

import com.fsh.common.util.Omits
import com.fsh.trade.util.DiffComparable

/**
 * 持仓数据
 */
abstract class InstrumentPosition : DiffComparable<InstrumentPosition>{
    var volume:Int =0 //总手数
    var longVolume:Int = 0 //多长手数
    var shortVolume:Int = 0 //空仓手数
    var longFrezonVolume:Int = 0 //多仓冻结手数
    var shortFrezonVolume:Int = 0 //空仓冻结
    var todayVolume:Int = 0 //今仓手数
    var yestodayVolume:Int =0 //昨仓手数
    var instrumentID:String = Omits.OmitString
    var exchangeID:String = Omits.OmitString

    /**
     * 处理委托响应和委托回报
     */
    abstract fun handleRspOrderField(rsp:RspOrderField)

    /**
     * 处理成交回报
     */
    abstract fun handleRspTradeField(rsp:RspTradeField)

    /**
     * 处理持仓查询响应
     */
    abstract fun handleRspQryPositionDetail(rsp:RspPositionDetailField)
}