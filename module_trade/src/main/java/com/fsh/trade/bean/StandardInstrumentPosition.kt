package com.fsh.trade.bean

import android.util.ArrayMap
import com.fsh.trade.enums.CTPCombOffsetFlag
import com.fsh.trade.enums.CTPHedgeType

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
        val positionDetailKey = "${rsp.tradeID.trim()}${rsp.openDate}${rsp.tradeType}"

        //今仓
        if(rsp.openDate == rsp.tradingDay){
            //投机仓
            if(rsp.hedgeFlag == CTPHedgeType.Speculation.code){

            }else if(rsp.hedgeFlag == CTPHedgeType.Hedge.code){

            }
        }
    }

    fun handleCacheData(key:String,positionMap:ArrayMap<String,RspPositionDetailField>){
        if(positionMap.contains(key)){
            val position = positionMap[key]

        }
    }
}