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
 * 大商所、中金所持仓数据
 * 特点，平仓区分投机套保，但不区分今昨仓，按照先开先平的原则平仓
 */
class StandardPosition : ExchangePosition(){

    override fun getCloseOrderFields(volume: Int,priceType: SupportTransactionOrderPrice,limitPrice:Double): List<IOrderInsertField> {
        val fieldList = ArrayList<IOrderInsertField>(2)
        val user = TradeApiProvider.providerCTPTradeApi().getCurrentUser()!!
        val orderDir = if(getDirection() == CTPDirection.Buy) CTPDirection.Sell else CTPDirection.Buy
        val orderPriceType = if(priceType == SupportTransactionOrderPrice.Market) CTPOrderPriceType.AnyPrice else CTPOrderPriceType.LimitPrice
        val timeCondition = if(priceType == SupportTransactionOrderPrice.Market) CTPTimeConditionType.IOC else CTPTimeConditionType.GFD
        val volumeCondition = CTPVolumeConditionType.AV
        val price = if(priceType == SupportTransactionOrderPrice.Market) 0.0 else limitPrice
        //有投机仓位
        if(tdSpecPos.posVolume + ydSpecPos.posVolume > tdSpecPos.frozenVolume + ydSpecPos.frozenVolume){
            val specAvailableCount = tdSpecPos.posVolume + ydSpecPos.posVolume - (tdSpecPos.frozenVolume + ydSpecPos.frozenVolume)
            val closeVolume = if(volume > specAvailableCount)  specAvailableCount else volume
            fieldList.add(CTPOrderInsertField(user.brokerID,user.userID,getInstrumentId(),Omits.OmitString,user.userID,orderPriceType,orderDir,CTPCombOffsetFlag.Close,
                CTPHedgeType.Speculation,price,closeVolume,timeCondition,DateUtils.formatNow1(),volumeCondition,1,CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,
                0,null,0,0,0,getExchangeId(),null,user.userID,null,null,null,null))
        }
        //有套保仓位
        if(tdHedgePos.posVolume + ydHedgePos.posVolume > tdHedgePos.frozenVolume + ydHedgePos.frozenVolume){
            val specAvailableCount = tdHedgePos.posVolume + ydHedgePos.posVolume - (tdHedgePos.frozenVolume + ydHedgePos.frozenVolume)
            val closeVolume = if(volume > specAvailableCount)  specAvailableCount else volume
            fieldList.add(CTPOrderInsertField(user.brokerID,user.userID,getInstrumentId(),Omits.OmitString,user.userID,orderPriceType,orderDir,CTPCombOffsetFlag.Close,
                CTPHedgeType.Hedge,price,closeVolume,timeCondition,DateUtils.formatNow1(),volumeCondition,1,CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,
                0,null,0,0,0,getExchangeId(),null,user.userID,null,null,null,null))
        }
        return fieldList
    }

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