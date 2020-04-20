package com.future.trade.bean.position.exchange

import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.bean.position.ExchangePosition
import com.future.trade.enums.*
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.repository.TradeApiProvider

/**
 * 郑商所持仓数据
 * 平仓特点：不分区今昨仓，也不区分投机套保，优先平投机，先开先平
 * 如：今仓5手投机5手套保，昨仓5手投机，5手套保
 * 平 5手  还剩 今仓5手投机，今仓5手套保，昨仓5手套保
 * 平 5手  还剩 今仓5手套保，昨仓5手套保
 * 平 5手  还剩 今仓5手套保
 * 平 5手  还剩 0手
 */
class CZCEPosition :ExchangePosition(){
    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        var result = ydSpecPos.onRspQryOrder(rsp)
        if(!result.second){
            result = tdSpecPos.onRspQryOrder(rsp)
        }
        if(!result.second){
            result = ydHedgePos.onRspQryOrder(rsp)
        }
        if(!result.second){
            result = tdHedgePos.onRspQryOrder(rsp)
        }
        return result
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        var result = ydSpecPos.onRtnOrder(rtn)
        if(!result.second){
            result = tdSpecPos.onRtnOrder(rtn)
        }
        if(!result.second){
            result = ydHedgePos.onRtnOrder(rtn)
        }
        if(!result.second){
            result = tdHedgePos.onRtnOrder(rtn)
        }
        return result
    }

    /**
     * 处理报单响应
     */
    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        var result = tdHedgePos.onRspOrderInsert(rsp)
        if(!result.second){
            result = ydSpecPos.onRspOrderInsert(rsp)
        }
        if(!result.second){
            result = tdSpecPos.onRspOrderInsert(rsp)
        }
        if(!result.second){
            result = ydSpecPos.onRspOrderInsert(rsp)
        }
        return result
    }

    override fun onRtnTradeClosePosition(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //平仓的时候，直接先平投机再平套保,先昨仓，后今仓
        var result = ydSpecPos.closePositionByRtnTrade(rtn)
        if(!result.second){
            result = tdSpecPos.closePositionByRtnTrade(rtn)
        }
        if(!result.second){
            result = ydHedgePos.closePositionByRtnTrade(rtn)
        }
        if(!result.second){
            result = tdHedgePos.closePositionByRtnTrade(rtn)
        }
        return result
    }
    override fun getCloseOrderFields(
        volume: Int,
        priceType: SupportTransactionOrderPrice,
        limitPrice: Double
    ): List<IOrderInsertField> {
        val list = ArrayList<IOrderInsertField>(1)
        val user = TradeApiProvider.providerCTPTradeApi().getCurrentUser()!!
        val orderDir = if(getDirection() == CTPDirection.Buy) CTPDirection.Sell else CTPDirection.Buy
        val orderPriceType = if(priceType == SupportTransactionOrderPrice.Market) CTPOrderPriceType.AnyPrice else CTPOrderPriceType.LimitPrice
        val timeCondition = if(priceType == SupportTransactionOrderPrice.Market) CTPTimeConditionType.IOC else CTPTimeConditionType.GFD
        val volumeCondition = CTPVolumeConditionType.AV
        val price = if(priceType == SupportTransactionOrderPrice.Market) 0.0 else limitPrice
        list.add(CTPOrderInsertField(user.brokerID,user.userID,getInstrumentId(),Omits.OmitString,user.userID,orderPriceType,orderDir,CTPCombOffsetFlag.Close,CTPHedgeType.Speculation,
            price,volume,timeCondition,DateUtils.formatNow1(),volumeCondition,1,CTPContingentConditionType.Immediately,null,CTPForceCloseReasonType.NotForceClose,
            1,null,0,0,0,getExchangeId(),null,user.userID,null,null,null,null))
        return list
    }
}