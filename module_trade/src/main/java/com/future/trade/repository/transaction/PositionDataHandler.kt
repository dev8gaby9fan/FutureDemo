package com.future.trade.repository.transaction

import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.InstrumentPosition
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPCombOffsetFlag
import java.util.concurrent.ConcurrentHashMap

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
    fun handleRspOrderInsert(rsp: RspOrderInsert)

    /**
     * 撤单响应
     */
    fun handleRspOrderAction(rsp: RspOrderAction)

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
    private val positionCollection: ConcurrentHashMap<String, InstrumentPosition> =
        ConcurrentHashMap(20)

    override fun getLiveData(): LiveData<List<Position>> = posLiveDat

    override fun handleRspQryOrder(rsp: RspQryOrder) {
        //查询成功了，直接处理
        if (rsp.rspField != null && !Omits.isOmit(rsp.rspField?.orderRef)) {
            val insPosition = positionCollection[rsp.rspField!!.instrumentID]
            insPosition?.onRspQryOrder(rsp)
            //这里如果是有委托数据，就返回持仓数据
            if (rsp.bIsLast) {
                posLiveDat.postValue(ArrayList(positionCollection.values))
            }
        }
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
        val insPosition = positionCollection[rtn.rspField.instrumentID]
        val handleResult = insPosition?.onRtnOrder(rtn)
        //这里持仓有变化，才发数据出去
        if(handleResult != null && handleResult.second){
            posLiveDat.postValue(ArrayList(positionCollection.values))
        }
    }

    override fun handleRspOrderInsert(rsp: RspOrderInsert) {
        val insPos = positionCollection[rsp.rspField?.instrumentID]
        val result = insPos?.onRspOrderInsert(rsp)
        //这里是有处理才把数据发送给界面
        if(result != null && result.second){
            posLiveDat.postValue(ArrayList(positionCollection.values))
        }
    }

    override fun handleRspOrderAction(rsp: RspOrderAction) {

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
        Log.d("PositionDataHandler","handleRspQryPositionDetail --> ${rsp.rspField?.tradeID}")
        if (rsp.rspField != null && !Omits.isOmit(rsp.rspField?.tradeID)) {
            val positionKey = "${rsp.rspField?.instrumentID}"
            var position = positionCollection[positionKey]
            if (position == null) {
                position = InstrumentPosition()
                positionCollection[positionKey] = position
            }
            position.onRspPositionDetail(rsp.rspField!!)
        }
        //处理到最后一条数据了，将处理好的数据发到界面上显示
        if (rsp.bIsLast) {
            val result: MutableList<Position> = ArrayList()
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
            Log.d("PositionDataHandler","result length --> ${result.size}")
            posLiveDat.postValue(ArrayList(result))
        }
    }

}