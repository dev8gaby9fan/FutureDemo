package com.future.trade.bean.position.exchange

import com.future.trade.bean.RspQryOrder
import com.future.trade.bean.RtnOrder
import com.future.trade.bean.position.ExchangePosition
import com.future.trade.bean.position.PositionDetailTable
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPHedgeType

/**
 * 上期能源的持仓数据处理
 * 特点是平仓区分今昨仓，严格区分投机套保仓位
 */
class SHFEINEPosition : ExchangePosition(){
    /**
     * 处理委托查询响应
     */
    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        return if(rsp.rspField!!.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text){
            //投机仓位 今投机
            handleRspQryOrderByHedge(tdSpecPos,tdHedgePos,rsp)
        }else{
            handleRspQryOrderByHedge(ydSpecPos,ydHedgePos,rsp)
        }
    }

    private fun handleRspQryOrderByHedge(specTable:PositionDetailTable,hedgeTable:PositionDetailTable,rspOder:RspQryOrder): Pair<RspQryOrder, Boolean>{
        return if(rspOder.rspField!!.combHedgeFlag == CTPHedgeType.Speculation.text){
            specTable.onRspQryOrder(rspOder)
        }else{
            hedgeTable.onRspQryOrder(rspOder)
        }
    }

    /**
     * 处理委托回报数据
     */
    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        return if(rtn.rspField.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text){
            //投机仓位 今投机
            handleRtnOrderByHedge(tdSpecPos,tdHedgePos,rtn)
        }else{
            handleRtnOrderByHedge(ydSpecPos,ydHedgePos,rtn)
        }
    }

    private fun handleRtnOrderByHedge(specTable:PositionDetailTable,hedgeTable:PositionDetailTable,rtn:RtnOrder): Pair<RtnOrder, Boolean>{
        return if(rtn.rspField.combHedgeFlag == CTPHedgeType.Speculation.text){
            specTable.onRtnOrder(rtn)
        }else{
            hedgeTable.onRtnOrder(rtn)
        }
    }
}