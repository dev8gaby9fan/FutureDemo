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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPosition(): Int {
        return shortPosition.getPosition() + longPosition.getPosition()
    }

    override fun getAvailable(): Int {
        return shortPosition.getAvailable() + longPosition.getAvailable()
    }

    override fun getSpecPosition(): Int = 0

    override fun getHedgePosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDirection(): CTPDirection {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHedgeType(): CTPHedgeType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}