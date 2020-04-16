package com.future.trade.bean.position

import com.fsh.common.util.Omits
import com.future.trade.bean.RspOrderInsert
import com.future.trade.bean.RspPositionDetailField
import com.future.trade.bean.RspQryOrder
import com.future.trade.bean.RtnOrder
import com.future.trade.enums.CTPDirection

/**
 * 按方向处理的持仓
 */
class DirectionPosition : SimplePosition() {
    private var dir:CTPDirection? = null
    private var exchangePosition:ExchangePosition? = null

    override fun getPosition(): Int {
        return exchangePosition?.getPosition() ?: 0
    }

    override fun getAvailable(): Int {
        return exchangePosition?.getAvailable() ?: 0
    }

    override fun getSpecPosition(): Int {
        return exchangePosition?.getSpecPosition()?:0
    }

    override fun getHedgePosition(): Int {
        return exchangePosition?.getHedgePosition() ?: 0
    }

    override fun getOpenCost(): Double {
        return exchangePosition?.getOpenCost() ?: 0.0
    }

    override fun getPositionCost(): Double {
        return exchangePosition?.getPositionCost() ?: 0.0
    }

    override fun getPositionProfit(): Double {
        return exchangePosition?.getPositionProfit() ?: 0.0
    }

    override fun getOpenPositionProfit(): Double {
        return exchangePosition?.getOpenPositionProfit() ?: 0.0
    }

    override fun getExchangeId(): String {
        return exchangePosition?.getExchangeId() ?: Omits.OmitPrice
    }

    override fun getInstrumentId(): String {
        return exchangePosition?.getInstrumentId() ?: Omits.OmitPrice
    }

    override fun getDirection(): CTPDirection {
        return dir ?: CTPDirection.Buy
    }

    /**
     * ==================================数据处理方法===============================================
     */
    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        if(dir == null){
            dir = CTPDirection.from(rsp.direction)
        }
        if(exchangePosition == null){
            exchangePosition = ExchangePosition.newInstance(rsp.exchangeID)
        }
        exchangePosition?.onRspPositionDetail(rsp)
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        if(dir == null){
            dir = CTPDirection.from(rtn.rspField.direction)
        }
        if(exchangePosition == null){
            return Pair(rtn,false)
        }
        return exchangePosition!!.onRtnOrder(rtn)
    }

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        return exchangePosition?.onRspQryOrder(rsp) ?: Pair(rsp,false)
    }

    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        if(exchangePosition == null){
            return Pair(rsp,false)
        }
        return exchangePosition!!.onRspOrderInsert(rsp)
    }


}