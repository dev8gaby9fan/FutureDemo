package com.future.trade.repository.transaction

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.InstrumentPosition
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPCombOffsetFlag

interface IPositionDataHandler : BaseDataHandler<Position> {
    /**
     * 处理委托查询响应
     */
    fun handleRspQryOrder(rsp: RspQryOrder)

    /**
     * 处理委托回报
     */
    fun handleRtnOrder(rtn: RtnOrder)

    /**
     * 报单响应
     */
    fun handleRspOrderInsert(rsp:RspOrderInsert)

    /**
     * 撤单响应
     */
    fun handleRspOrderAction(rsp:RspOrderAction)

    /**
     * 处理成交回报
     */
    fun handleRtnTrade(rtn: RtnTrade)

    /**
     * 处理持仓明细响应
     */
    fun handleRspQryPositionDetail(rsp: RspQryPositionDetail)
}

class PositionDataHandler : IPositionDataHandler {
    //网外发的数据按合约+持仓方向显示
    private val posLiveDat: MutableLiveData<List<Position>> = MutableLiveData()
    //初始化容器大小为20,key为合约ID-持仓方向
    private val positionCollection:ArrayMap<String,InstrumentPosition> = ArrayMap(20)
    override fun getLiveData(): LiveData<List<Position>> = posLiveDat

    override fun handleRspQryOrder(rsp: RspQryOrder) {
        //查询成功了，直接处理
//        if(rsp.rspField != null && rsp.rspInfoField.errorID == 0){
//            handleRspOrderField(rsp.rspField!!)
//        }
//        if(rsp.bIsLast){
//            posLiveDat.postValue(ArrayList(positionCollection.values))
//        }
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
//        handleRspOrderField(rtn.rspField)
//        posLiveDat.postValue(ArrayList(positionCollection.values))
    }

    override fun handleRspOrderInsert(rsp: RspOrderInsert) {

    }

    override fun handleRspOrderAction(rsp: RspOrderAction) {

    }


    private fun handleRspOrderField(field:RspOrderField){
//        //开仓的委托不管，不用计算
//        if(CTPCombOffsetFlag.from(field.combOffsetFlag[0]) == CTPCombOffsetFlag.Open){
//            return
//        }
//        //没有持仓去处理委托就不管了，可能仓位被平掉了
//        val position: InstrumentPosition? = positionCollection[field.instrumentID] ?: return
//        position?.handleRspOrderField(field)
    }

    override fun handleRtnTrade(rtn: RtnTrade) {
//        var position = positionCollection[rtn.rspField.instrumentID]
//        if(position == null){
//            position = InstrumentPosition.createPositionPoJoByExchangeId(rtn.rspField.exchangeID)
//            positionCollection[rtn.rspField.instrumentID] = position
//        }
//        position!!.handleRspTradeField(rtn.rspField)
//        posLiveDat.postValue(ArrayList(positionCollection.values))
    }

    override fun handleRspQryPositionDetail(rsp: RspQryPositionDetail) {
        if(rsp.rspField != null && !Omits.isOmit(rsp.rspField?.tradeID)){
            val positionKey = "${rsp.rspField?.instrumentID}"
            var position = positionCollection[positionKey]
            if(position == null){
                position = InstrumentPosition()
                positionCollection[positionKey] = position
            }
            position.onRspPositionDetail(rsp.rspField!!)
        }
        //处理到最后一条数据了，将处理好的数据发到界面上显示
        if(rsp.bIsLast){
            val result:MutableList<Position> = ArrayList()
            positionCollection.values.map {
                val list = ArrayList<Position>()
                if (it.longPosition.getPosition() > 0) {
                    list.add(it.longPosition)
                }
                if (it.shortPosition.getPosition() > 0) {
                    list.add(it.shortPosition)
                }
                list
            }.forEach {
                result.addAll(it)
            }
            posLiveDat.postValue(ArrayList(result))
        }
    }

}