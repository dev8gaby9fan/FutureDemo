package com.future.trade.bean.position

import com.future.trade.bean.*
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType
import com.future.trade.enums.ExchangeType

/**
 * 今昨仓位，包括有投机和套保仓
 */
class DatePosition(var specPos:Position?,var hedgePos:Position?) : SimplePosition(){

    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        //投机仓
        if(rsp.hedgeFlag == CTPHedgeType.Speculation.code){
            if(specPos == null){
                specPos = HedgePosition()
            }
            specPos?.onRspPositionDetail(rsp)
        }else if(rsp.hedgeFlag == CTPHedgeType.Hedge.code){
            if(hedgePos == null){
                hedgePos = HedgePosition()
            }
            hedgePos?.onRspPositionDetail(rsp)
        }
    }

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        val exchangeType = ExchangeType.from(rsp.rspField?.exchangeID) ?: return Pair(rsp,false)
        //平仓区分投机套保，那么就按投机仓和套保仓来处理
        if(exchangeType.isHedgePos){
            if(rsp.rspField?.combHedgeFlag == CTPHedgeType.Speculation.text){
                return specPos?.onRspQryOrder(rsp) ?: Pair(rsp,false)
            }else if(rsp.rspField?.combHedgeFlag == CTPHedgeType.Hedge.text){
                return hedgePos?.onRspQryOrder(rsp) ?: Pair(rsp,false)
            }
            return Pair(rsp,false)
        }else{
            //郑商所，平仓不区分投机和套保，优先平投机仓,
            //eg:今投机5手，套保10手；昨投机10手，昨套保5手
            //平仓应该是先平昨投机--》今投机--》昨套保---》今套保
            var pair = specPos?.onRspQryOrder(rsp) ?: Pair(rsp,false)
//            return hedgePos?.onRspQryOrder()
        }
        return super.onRspQryOrder(rsp)
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        return super.onRtnOrder(rtn)
    }

    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        return super.onRspOrderInsert(rsp)
    }

    override fun onRspOrderAction(rsp: RspOrderAction): Pair<RspOrderAction, Boolean> {
        return super.onRspOrderAction(rsp)
    }

    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        return super.onRtnTrade(rtn)
    }
}