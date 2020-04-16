package com.future.trade.bean.position.exchange

import com.future.trade.bean.RspOrderInsert
import com.future.trade.bean.RspQryOrder
import com.future.trade.bean.RtnOrder
import com.future.trade.bean.RtnTrade
import com.future.trade.bean.position.ExchangePosition
import com.future.trade.bean.position.PositionDetailTable
import com.future.trade.enums.CTPHedgeType

/**
 * 大商所、中金所持仓数据
 * 特点，平仓区分投机套保，但不区分今昨仓，按照先开先平的原则平仓
 */
class StandardPosition : ExchangePosition(){

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        //投机仓
        return if (rsp.rspField!!.combHedgeFlag == CTPHedgeType.Speculation.text) {
            handleRspQryOrderByDate(tdSpecPos, ydSpecPos, rsp)
        } else {
            handleRspQryOrderByDate(tdHedgePos, ydHedgePos, rsp)
        }
    }

    private fun handleRspQryOrderByDate(tdTable:PositionDetailTable,ydTable:PositionDetailTable,rsp: RspQryOrder):Pair<RspQryOrder,Boolean>{
        var result = ydTable.onRspQryOrder(rsp)
        if(!result.second){
            result = tdTable.onRspQryOrder(result.first)
        }
        return result
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        return if (rtn.rspField.combHedgeFlag == CTPHedgeType.Speculation.text) {
            handleRtnOrderByDate(tdSpecPos, ydSpecPos, rtn)
        } else {
            handleRtnOrderByDate(tdHedgePos, ydHedgePos, rtn)
        }
    }

    private fun handleRtnOrderByDate(tdTable: PositionDetailTable, ydTable: PositionDetailTable, rtn:RtnOrder):Pair<RtnOrder,Boolean>{
        var result = ydTable.onRtnOrder(rtn)
        if(!result.second){
            result = tdTable.onRtnOrder(result.first)
        }
        return result
    }

    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        //投机仓
        return if(rsp.rspField?.combHedgeFlag == CTPHedgeType.Speculation.text){
            handleRspOrderInsertByDate(tdSpecPos,ydSpecPos,rsp)
        }else{
            handleRspOrderInsertByDate(tdHedgePos,ydHedgePos,rsp)
        }
    }

    private fun handleRspOrderInsertByDate(tdTable:PositionDetailTable,ydTable: PositionDetailTable,rsp:RspOrderInsert):Pair<RspOrderInsert,Boolean>{
        //这里释放冻结仓位需要先释放今仓，然后再释放昨仓
        var result = tdTable.onRspOrderInsert(rsp)
        if(!result.second){
            result = ydTable.onRspOrderInsert(result.first)
        }
        return result
    }

    /**
     * 平仓的成交回报处理
     */
    override fun onRtnTradeClosePosition(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //平仓，先开先平，优先平昨仓
        return if(rtn.rspField.hedgeFlag == CTPHedgeType.Speculation.code){
            closePositionByRtnTrade(rtn,ydSpecPos,tdSpecPos)
        }else{
            closePositionByRtnTrade(rtn,ydHedgePos,tdHedgePos)
        }
    }

    private fun closePositionByRtnTrade(rtn:RtnTrade,ydTable: PositionDetailTable,tdTable: PositionDetailTable): Pair<RtnTrade, Boolean>{
        //昨仓没有平完的 ,今仓继续平
        var result = ydTable.closePositionByRtnTrade(rtn)
        if(!result.second){
            result = tdTable.closePositionByRtnTrade(result.first)
        }
        return result
    }


}