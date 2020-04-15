package com.future.trade.bean.position

import com.fsh.common.util.Omits
import com.future.trade.bean.RspPositionDetailField
import com.future.trade.bean.RspQryOrder
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

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        return exchangePosition?.onRspQryOrder(rsp) ?: Pair(rsp,false)
    }


}