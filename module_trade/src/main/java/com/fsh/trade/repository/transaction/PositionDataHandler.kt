package com.fsh.trade.repository.transaction

import com.fsh.trade.bean.RspQryOrder
import com.fsh.trade.bean.RspQryPositionDetail
import com.fsh.trade.bean.RtnOrder
import com.fsh.trade.bean.RtnTrade

interface IPositionDataHandler{
    fun handleRspQryOrder(rsp: RspQryOrder)
    fun handleRtnOrder(rtn:RtnOrder)
    fun handleRtnTrade(rtn:RtnTrade)
    fun handleRspQryPositionDetail(rsp:RspQryPositionDetail)
}

class PositionDataHandler : IPositionDataHandler{

    override fun handleRspQryOrder(rsp: RspQryOrder) {

    }

    override fun handleRtnOrder(rtn: RtnOrder) {

    }

    override fun handleRtnTrade(rtn: RtnTrade) {

    }

    override fun handleRspQryPositionDetail(rsp: RspQryPositionDetail) {

    }

}