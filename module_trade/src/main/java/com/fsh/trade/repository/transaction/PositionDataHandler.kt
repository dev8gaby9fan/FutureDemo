package com.fsh.trade.repository.transaction

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.trade.bean.*
import com.fsh.trade.enums.CTPCombOffsetFlag

interface IPositionDataHandler : BaseDataHandler<InstrumentPosition> {
    fun handleRspQryOrder(rsp: RspQryOrder)
    fun handleRtnOrder(rtn: RtnOrder)
    fun handleRtnTrade(rtn: RtnTrade)
    fun handleRspQryPositionDetail(rsp: RspQryPositionDetail)
}

class PositionDataHandler : IPositionDataHandler {
    private val posLiveDat: MutableLiveData<List<InstrumentPosition>> = MutableLiveData()
    //初始化容器大小为20
    private val positionCollection:ArrayMap<String,InstrumentPosition> = ArrayMap(20)
    override fun getLiveData(): LiveData<List<InstrumentPosition>> = posLiveDat

    override fun handleRspQryOrder(rsp: RspQryOrder) {
        //查询成功了，直接处理
        if(rsp.rspField != null && rsp.rspInfoField.errorID == 0){
            handleRspOrderField(rsp.rspField!!)
        }
        if(rsp.bIsLast){
            posLiveDat.postValue(ArrayList(positionCollection.values))
        }
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
        handleRspOrderField(rtn.rspField)
        posLiveDat.postValue(ArrayList(positionCollection.values))
    }

    private fun handleRspOrderField(field:RspOrderField){
        //开仓的委托不管，不用计算
        if(CTPCombOffsetFlag.from(field.combOffsetFlag[0]) == CTPCombOffsetFlag.Open){
            return
        }
        //没有持仓去处理委托就不管了，可能仓位被平掉了
        var position: InstrumentPosition? = positionCollection[field.instrumentID] ?: return
        position?.handleRspOrderField(field)
    }

    override fun handleRtnTrade(rtn: RtnTrade) {
        var position = positionCollection[rtn.rspField.instrumentID]
        if(position == null){
            position = InstrumentPosition.createPositionPoJoByExchangeId(rtn.rspField.exchangeID)
            positionCollection[rtn.rspField.instrumentID] = position
        }
        position.handleRspTradeField(rtn.rspField)
        posLiveDat.postValue(ArrayList(positionCollection.values))
    }

    override fun handleRspQryPositionDetail(rsp: RspQryPositionDetail) {
        if(rsp.rspField != null && rsp.rspInfoField.errorID ==0){
            var position = positionCollection[rsp.rspField?.instrumentID]
            if(position == null){
                position = InstrumentPosition.createPositionPoJoByExchangeId(rsp.rspField!!.exchangeID)
                positionCollection[rsp.rspField?.instrumentID] = position
            }
            position.handleRspQryPositionDetail(rsp.rspField!!)
        }
        if(rsp.bIsLast){
            posLiveDat.postValue(ArrayList(positionCollection.values))
        }
    }

}