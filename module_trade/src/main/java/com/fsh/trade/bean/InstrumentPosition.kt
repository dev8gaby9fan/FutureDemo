package com.fsh.trade.bean

import com.fsh.common.util.Omits
import com.fsh.trade.enums.ExchangeType
import com.fsh.trade.util.DiffComparable

/**
 * 持仓数据
 */
abstract class InstrumentPosition : DiffComparable<InstrumentPosition>{
    var volume:Int =0 //总手数
    var longVolume:Int = 0 //多长手数
    var shortVolume:Int = 0 //空仓手数
    var longFrozenVolume:Int = 0 //多仓冻结手数
    var shortFrozenVolume:Int = 0 //空仓冻结
    var todayVolume:Int = 0 //今仓手数
    var yesterdayVolume:Int =0 //昨仓手数
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

    override fun compare(obj: InstrumentPosition): Boolean {
        return obj.instrumentID == instrumentID && obj.exchangeID == exchangeID && obj.volume == volume
                && obj.longVolume == longVolume && obj.longFrozenVolume == longFrozenVolume
                && obj.shortVolume == obj.shortVolume && obj.shortFrozenVolume == shortFrozenVolume
                && obj.todayVolume == obj.todayVolume && obj.yesterdayVolume == yesterdayVolume;
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false
        if(other === this)
            return  true
        if(other !is InstrumentPosition)
            return false
        return other.instrumentID == instrumentID && other.exchangeID == exchangeID
    }

    override fun hashCode(): Int {
        var result = volume
        result = 31 * result + longVolume
        result = 31 * result + shortVolume
        result = 31 * result + longFrozenVolume
        result = 31 * result + shortFrozenVolume
        result = 31 * result + todayVolume
        result = 31 * result + yesterdayVolume
        result = 31 * result + instrumentID.hashCode()
        result = 31 * result + exchangeID.hashCode()
        return result
    }

    companion object{

        //根据交易所ID，创建持仓类型
        fun createPositionPoJoByExchangeId(exchangeId:String):InstrumentPosition? =
            ExchangeType.from(exchangeId)?.getInstrumentInstance()
    }
}