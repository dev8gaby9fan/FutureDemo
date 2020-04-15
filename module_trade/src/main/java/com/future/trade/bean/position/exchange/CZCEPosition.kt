package com.future.trade.bean.position.exchange

import com.future.trade.bean.RspQryOrder
import com.future.trade.bean.RtnOrder
import com.future.trade.bean.position.ExchangePosition

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
}