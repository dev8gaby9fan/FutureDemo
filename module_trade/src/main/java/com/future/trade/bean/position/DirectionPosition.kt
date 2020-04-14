package com.future.trade.bean.position

import com.future.trade.bean.*
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.ExchangeType

/**
 * 根据方向的持仓
 * 里面包括有今仓和昨仓
 */
class DirectionPosition(private var todayPos: Position?, private var yesterdayPos: Position?) :
    SimplePosition() {

    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        //今仓
        if (rsp.openDate == rsp.tradingDay) {
            if (todayPos == null) {
                todayPos = DatePosition(null, null)
            }
            todayPos!!.onRspPositionDetail(rsp)
        } else {
            if (yesterdayPos == null) {
                yesterdayPos = DatePosition(null, null)
            }
            yesterdayPos!!.onRspPositionDetail(rsp)
        }
    }

    //委托查询响应，外部已经处理过了开平判断，这里面就不再判断了
    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        val exchangeType = ExchangeType.from(rsp.rspField?.exchangeID) ?: return Pair(rsp, false)
        //区分今昨仓处理的仓位
        return if (exchangeType.isYdPos) {
            //平仓或者平今操作都只能平今仓【上期和能源所】
            if (rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.Close.text || rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text) {
                todayPos?.onRspQryOrder(rsp) ?: Pair(rsp, false)
            } else {//平昨仓操作,直接先平昨
                yesterdayPos?.onRspQryOrder(rsp) ?: Pair(rsp, false)
            }
        } else {
            var pair = yesterdayPos?.onRspQryOrder(rsp) ?: Pair(rsp, false)
            pair = todayPos?.onRspQryOrder(rsp) ?: Pair(pair.first, false)
            pair
        }
    }

    //委托回报处理
    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        val exchangeType = ExchangeType.from(rtn.rspField.exchangeID) ?: return Pair(rtn, false)
        //区分今昨仓处理的仓位
        return if (exchangeType.isYdPos) {
            //平仓或者平今操作都只能平今仓【上期和能源所】
            if (rtn.rspField.combOffsetFlag == CTPCombOffsetFlag.Close.text || rtn.rspField.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text) {
                todayPos?.onRtnOrder(rtn) ?: Pair(rtn, false)
            } else {//平昨仓操作,直接先平昨
                yesterdayPos?.onRtnOrder(rtn) ?: Pair(rtn, false)
            }
        } else {
            var pair = yesterdayPos?.onRtnOrder(rtn) ?: Pair(rtn, false)
            pair = todayPos?.onRtnOrder(rtn) ?: Pair(pair.first, false)
            pair
        }
    }

    //报单响应处理
    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        val exchangeType = ExchangeType.from(rsp.rspField?.exchangeID) ?: return Pair(rsp, false)
        //区分今昨仓处理的仓位
        return if (exchangeType.isYdPos) {
            //平仓或者平今操作都只能平今仓【上期和能源所】
            if (rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.Close.text || rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.CloseToday.text) {
                todayPos?.onRspOrderInsert(rsp) ?: Pair(rsp, false)
            } else {//平昨仓操作,直接先平昨
                yesterdayPos?.onRspOrderInsert(rsp) ?: Pair(rsp, false)
            }
        } else {
            var pair = yesterdayPos?.onRspOrderInsert(rsp) ?: Pair(rsp, false)
            pair = todayPos?.onRspOrderInsert(rsp) ?: Pair(pair.first, false)
            pair
        }
    }

    override fun onRspOrderAction(rsp: RspOrderAction): Pair<RspOrderAction, Boolean> {
        val exchangeType = ExchangeType.from(rsp.rspField?.exchangeID) ?: return Pair(rsp, false)
        //TODO 需要外部传入报单记录信息，才能处理了,将OrderDataHandler的处理结果传入进来吧
        return super.onRspOrderAction(rsp)
    }

    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //开仓操作，收到开仓回报，开的仓位只可能是今仓
        return if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.Open.offset){
            if(todayPos == null){
                todayPos = DatePosition(null,null)
            }
            return todayPos!!.onRtnTrade(rtn)
        }else{
            val exchangeType = ExchangeType.from(rtn.rspField.exchangeID) ?: return Pair(rtn,false)
            //平仓区分今昨仓
            if(exchangeType.isYdPos){
                if (rtn.rspField.offsetFlag == CTPCombOffsetFlag.Close.text[0] || rtn.rspField.offsetFlag == CTPCombOffsetFlag.CloseToday.text[0]) {
                    todayPos?.onRtnTrade(rtn) ?: Pair(rtn, false)
                } else {//平昨仓操作,直接先平昨
                    yesterdayPos?.onRtnTrade(rtn) ?: Pair(rtn, false)
                }
            }else{
                //平仓的时候，可能是平投机或者套保，这里将投机套保的区分放到DatePosition里面判断了
                var pair = yesterdayPos?.onRtnTrade(rtn) ?: Pair(rtn, false)
                pair = todayPos?.onRtnTrade(rtn) ?: Pair(pair.first, false)
                pair
            }
        }
    }

}