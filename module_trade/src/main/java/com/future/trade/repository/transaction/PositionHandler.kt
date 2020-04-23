package com.future.trade.repository.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.bean.position.InstrumentPosition
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import java.util.concurrent.ConcurrentHashMap

interface IPositionHandler : BaseDataHandler<Position> {
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

    /**
     * 获取持仓数据
     */
    fun getPositionByInstrumentId(instrumentId:String,direction: CTPDirection? = null):Position?
}

class PositionHandler : IPositionHandler {
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
                sendPositionDataToView()
            }
        }
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
        val insPosition = positionCollection[rtn.rspField.instrumentID]
        val handleResult = insPosition?.onRtnOrder(rtn)
        //这里持仓有变化，才发数据出去
        if(handleResult != null && handleResult.second){
            sendPositionDataToView()
        }
    }

    override fun handleRspOrderInsert(rsp: RspOrderInsert) {
        val insPos = positionCollection[rsp.rspField?.instrumentID]
        val result = insPos?.onRspOrderInsert(rsp)
        //这里是有处理才把数据发送给界面
        if(result != null && result.second){
            sendPositionDataToView()
        }
    }

    override fun handleRspOrderAction(rsp: RspOrderAction) {

    }

    override fun handleRtnTrade(rtn: RtnTrade) {
        Log.d("PositionHandler","handleRtnTrade ${rtn.rspField.instrumentID} ${CTPDirection.from(rtn.rspField.direction)?.text} ${CTPCombOffsetFlag.from(rtn.rspField.offsetFlag)} ${rtn.rspField.volume}")
        var position = positionCollection[rtn.rspField.instrumentID]
        //开仓,需要新建仓位
        if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.Open.offset){
            if(position == null){
                position = InstrumentPosition()
                positionCollection[rtn.rspField.instrumentID] = position
            }
            val result = position.onRtnTrade(rtn)
            if(result.second){
                sendPositionDataToView()
            }
            Log.d("PositionHandler","handleRtnTrade open position result ${result.second}" )
        }else if(position != null && rtn.rspField.offsetFlag != CTPCombOffsetFlag.Open.offset){
            //平仓
            val result = position.onRtnTrade(rtn)
            if(position.getPosition() == 0){
                positionCollection.remove(rtn.rspField.instrumentID)
            }
            //需要通知界面刷新
            if(result.second){
                sendPositionDataToView()
            }
            Log.d("PositionHandler","handleRtnTrade close position result ${result.second}" )
        }
    }

    override fun handleRspQryPositionDetail(rsp: RspQryPositionDetail) {
        Log.d("PositionHandler","handleRspQryPositionDetail --> ${rsp.rspField?.tradeID}")
        if (rsp.rspField != null && !Omits.isOmit(rsp.rspField?.tradeID)) {
            val positionKey = "${rsp.rspField?.instrumentID}"
            var position = positionCollection[positionKey]
            if (position == null) {
                position = InstrumentPosition()
                positionCollection[positionKey] = position
            }
            position.onRspPositionDetail(rsp.rspField!!)
            //没有仓位就不保存起来了
            if(position.getPosition() == 0){
                positionCollection.remove(positionKey)
            }
        }
        //处理到最后一条数据了，将处理好的数据发到界面上显示
        if (rsp.bIsLast) {
            sendPositionDataToView()
        }
    }

    private fun sendPositionDataToView(){
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
        Log.d("PositionHandler","result length --> ${result.size}")
        posLiveDat.postValue(ArrayList(result))
    }

    override fun getPositionByInstrumentId(
        instrumentId: String,
        direction: CTPDirection?
    ): Position? {
        val pos = positionCollection[instrumentId] ?: return null
        if(direction == null){
            return pos
        }
        if(direction == CTPDirection.Buy && pos.longPosition.getPosition() > 0){
            return pos.longPosition
        }
        if(direction == CTPDirection.Sell && pos.shortPosition.getPosition() > 0){
            return pos.shortPosition
        }
        return null
    }
}