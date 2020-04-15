package com.future.trade.bean.position.exchange

import com.future.trade.bean.RspQryOrder
import com.future.trade.bean.position.ExchangePosition
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPHedgeType

/**
 * 上期能源的持仓数据处理
 * 特点是平仓区分今昨仓，严格区分投机套保仓位
 */
class SHFEINEPosition : ExchangePosition(){

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        return if(rsp.rspField!!.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text){
            //投机仓位 今投机
            if(rsp.rspField!!.combHedgeFlag == CTPHedgeType.Speculation.text){
                tdSpecPos.onRspQryOrder(rsp)
            }else{
                tdHedgePos.onRspQryOrder(rsp)
            }
        }else{
            if(rsp.rspField!!.combHedgeFlag == CTPHedgeType.Speculation.text){
                ydSpecPos.onRspQryOrder(rsp)
            }else{
                ydHedgePos.onRspQryOrder(rsp)
            }
        }
    }
}