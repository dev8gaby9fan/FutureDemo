package com.future.trade.bean.position.exchange

import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.bean.position.ExchangePosition
import com.future.trade.bean.position.PositionDetailTable
import com.future.trade.enums.*
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.repository.TradeApiProvider

/**
 * 上期能源的持仓数据处理
 * 特点是平仓区分今昨仓，严格区分投机套保仓位
 */
class SHFEINEPosition : ExchangePosition(){
    override fun getCloseOrderFields(
        volume: Int,
        priceType: SupportTransactionOrderPrice,
        limitPrice: Double
    ): List<IOrderInsertField> {
        //这里既得区分今昨 也得区分投机套保
        val fieldList = ArrayList<IOrderInsertField>()
        val user = TradeApiProvider.providerCTPTradeApi().getCurrentUser()!!
        val orderDir = if(getDirection() == CTPDirection.Buy) CTPDirection.Sell else CTPDirection.Buy
        if(tdSpecPos.posVolume > tdSpecPos.frozenVolume){
            fieldList.add(getCloseOrderFieldFromPositionDetailTable(tdSpecPos,limitPrice,volume,user,orderDir,CTPCombOffsetFlag.CloseToday,CTPHedgeType.Speculation))
        }
        if(tdHedgePos.posVolume > tdHedgePos.frozenVolume){
            fieldList.add(getCloseOrderFieldFromPositionDetailTable(tdHedgePos,limitPrice,volume,user,orderDir,CTPCombOffsetFlag.CloseToday,CTPHedgeType.Hedge))
        }
        if(ydSpecPos.posVolume > ydSpecPos.frozenVolume){
            fieldList.add(getCloseOrderFieldFromPositionDetailTable(ydSpecPos,limitPrice,volume,user,orderDir,CTPCombOffsetFlag.CloseYesterday,CTPHedgeType.Speculation))
        }
        if(ydHedgePos.posVolume > ydHedgePos.frozenVolume){
            fieldList.add(getCloseOrderFieldFromPositionDetailTable(ydHedgePos,limitPrice,volume,user,orderDir,CTPCombOffsetFlag.CloseYesterday,CTPHedgeType.Hedge))
        }
        return fieldList
    }

    private fun getCloseOrderFieldFromPositionDetailTable(posTable:PositionDetailTable,limitPrice: Double,volume: Int,user:RspUserLoginField,direction:CTPDirection,offset: CTPCombOffsetFlag,hedge:CTPHedgeType):IOrderInsertField{
        val vol = if(volume > (posTable.posVolume - posTable.frozenVolume)) (posTable.posVolume - posTable.frozenVolume) else volume
        return CTPOrderInsertField(user.brokerID,user.userID,getInstrumentId(),Omits.OmitString,user.userID,CTPOrderPriceType.LimitPrice,direction,offset,hedge,limitPrice,vol,
            CTPTimeConditionType.GFD,DateUtils.formatNow1(),CTPVolumeConditionType.AV,1,CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,
            1,null,0,0,0,getExchangeId(),user.userID,user.userID,null,null,null,null)
    }

    /**
     * 处理委托查询响应
     */
    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        return if(rsp.rspField!!.combOffsetFlag[0] == CTPCombOffsetFlag.CloseToday.offset){
            //投机仓位 今投机
            handleRspQryOrderByHedge(tdSpecPos,tdHedgePos,rsp)
        }else{
            handleRspQryOrderByHedge(ydSpecPos,ydHedgePos,rsp)
        }
    }

    private fun handleRspQryOrderByHedge(specTable:PositionDetailTable,hedgeTable:PositionDetailTable,rspOder:RspQryOrder): Pair<RspQryOrder, Boolean>{
        return if(rspOder.rspField!!.combHedgeFlag[0] == CTPHedgeType.Speculation.code){
            specTable.onRspQryOrder(rspOder)
        }else{
            hedgeTable.onRspQryOrder(rspOder)
        }
    }

    /**
     * 处理委托回报数据
     */
    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        return if(rtn.rspField.combOffsetFlag[0] == CTPCombOffsetFlag.CloseToday.offset){
            //投机仓位 今投机
            handleRtnOrderByHedge(tdSpecPos,tdHedgePos,rtn)
        }else{
            handleRtnOrderByHedge(ydSpecPos,ydHedgePos,rtn)
        }
    }

    private fun handleRtnOrderByHedge(specTable:PositionDetailTable,hedgeTable:PositionDetailTable,rtn:RtnOrder): Pair<RtnOrder, Boolean>{
        return if(rtn.rspField.combHedgeFlag[0] == CTPHedgeType.Speculation.code){
            specTable.onRtnOrder(rtn)
        }else{
            hedgeTable.onRtnOrder(rtn)
        }
    }

    /**
     * 处理报单响应
     */
    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        return if(rsp.rspField!!.combOffsetFlag[0] == CTPCombOffsetFlag.CloseToday.offset){
            handleRspOrderInsertByHedge(tdSpecPos,tdHedgePos,rsp)
        }else{
            handleRspOrderInsertByHedge(ydSpecPos,ydHedgePos,rsp)
        }
    }

    private fun handleRspOrderInsertByHedge(specTable: PositionDetailTable,hedgeTable: PositionDetailTable,rsp:RspOrderInsert):Pair<RspOrderInsert,Boolean>{
        return if(rsp.rspField!!.combHedgeFlag[0] == CTPHedgeType.Speculation.code){
            specTable.onRspOrderInsert(rsp)
        }else{
            hedgeTable.onRspOrderInsert(rsp)
        }
    }

    override fun onRtnTradeClosePosition(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //平今
        return if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.CloseToday.offset){
            closePositionByHedge(tdSpecPos,tdHedgePos,rtn)
        }else{
            //平昨
            closePositionByHedge(ydSpecPos,ydHedgePos,rtn)
        }
    }

    private fun closePositionByHedge(specTable: PositionDetailTable,hedgeTable: PositionDetailTable,rtn:RtnTrade): Pair<RtnTrade, Boolean>{
        return if(rtn.rspField.hedgeFlag == CTPHedgeType.Speculation.code){
            specTable.closePositionByRtnTrade(rtn)
        }else{
            hedgeTable.closePositionByRtnTrade(rtn)
        }
    }
}