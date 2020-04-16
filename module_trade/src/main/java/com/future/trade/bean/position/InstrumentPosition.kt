package com.future.trade.bean.position

import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType

/**
 * 合约的持仓汇总
 * 包括多仓和空仓两种仓位
 */
class InstrumentPosition : SimplePosition(){
    val longPosition:DirectionPosition = DirectionPosition()
    val shortPosition:DirectionPosition = DirectionPosition()

    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        if(rsp.direction == CTPDirection.Buy.direction){
            longPosition.onRspPositionDetail(rsp)
        }else{
            shortPosition.onRspPositionDetail(rsp)
        }
    }

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        //没有查询到数据 不处理
        if(rsp.rspField == null || Omits.isOmit(rsp.rspField!!.orderRef)){
            return Pair(rsp,false)
        }
        //开仓数据不处理
        if(rsp.rspField!!.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rsp,false)
        }
        //卖平仓的委托，需要多仓处理
        return if(rsp.rspField!!.direction == CTPDirection.Sell.direction){
            longPosition.onRspQryOrder(rsp)
        }else{
            shortPosition.onRspQryOrder(rsp)
        }
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        //开仓行为不处理
        if(rtn.rspField.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rtn,false)
        }
        //卖平仓的委托，需要多仓处理
        return if(rtn.rspField.direction == CTPDirection.Sell.direction){
            longPosition.onRtnOrder(rtn)
        }else{
            shortPosition.onRtnOrder(rtn)
        }
    }

    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        //数据内容没有，不处理
        if(rsp.rspField == null || Omits.isOmit(rsp.rspField?.orderRef)){
            return Pair(rsp,false)
        }
        //开仓委托响应 不处理
        if(rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rsp,false)
        }
        return if(rsp.rspField?.direction == CTPDirection.Buy.direction){
            shortPosition.onRspOrderInsert(rsp)
        }else{
            longPosition.onRspOrderInsert(rsp)
        }
    }

    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //开仓操作，需要新建仓位
        return if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.Open.offset){
            if(rtn.rspField.direction == CTPDirection.Buy.direction){
                longPosition.onRtnTrade(rtn)
            }else{
                shortPosition.onRtnTrade(rtn)
            }
        }else{
            //平仓
            if(rtn.rspField.direction == CTPDirection.Buy.direction){
                shortPosition.onRtnTrade(rtn)
            }else{
                longPosition.onRtnTrade(rtn)
            }
        }
    }

    override fun getPosition(): Int {
        return shortPosition.getPosition() + longPosition.getPosition()
    }

    override fun getAvailable(): Int {
        return shortPosition.getAvailable() + longPosition.getAvailable()
    }

    override fun getSpecPosition(): Int {
        return longPosition.getSpecPosition() + shortPosition.getSpecPosition()
    }

    override fun getHedgePosition(): Int {
        return longPosition.getHedgePosition() + shortPosition.getHedgePosition()
    }

    override fun getOpenCost(): Double {
        return longPosition.getOpenCost() + shortPosition.getOpenCost()
    }

    override fun getPositionCost(): Double {
        return longPosition.getPositionCost() + shortPosition.getPositionCost()
    }

    override fun getPositionProfit(): Double {
        return longPosition.getPositionProfit() + shortPosition.getPositionProfit()
    }

    override fun getOpenPositionProfit(): Double {
        return longPosition.getOpenPositionProfit() + shortPosition.getOpenPositionProfit()
    }
}