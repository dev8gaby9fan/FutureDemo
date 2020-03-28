package com.fsh.trade.repository.transaction

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.trade.bean.RspQryTrade
import com.fsh.trade.bean.RspTradeField
import com.fsh.trade.bean.RtnTrade

interface ITradeDataHandler : BaseDataHandler<RspTradeField> {
    /**
     * 处理查询响应
     */
    fun handleRspQryTrade(rsp:RspQryTrade)

    /**
     * 处理成交回报
     */
    fun handleRtnQryTrade(rtn:RtnTrade)

    /**
     * 获取所有的成交数据
     */
    fun getTradeDataList():List<RspTradeField>
}

class TradeDataHandler : ITradeDataHandler{
    private val liveData:MutableLiveData<List<RspTradeField>> = MutableLiveData()
    private val tradeDataCollection:ArrayMap<String,RspTradeField> = ArrayMap(200)

    override fun getLiveData(): LiveData<List<RspTradeField>> = liveData

    override fun getTradeDataList(): List<RspTradeField> = ArrayList(tradeDataCollection.values)

    override fun handleRtnQryTrade(rtn: RtnTrade) {
            handleRspTradeField(rtn.rspField)
    }

    override fun handleRspQryTrade(rsp: RspQryTrade) {
        //错误数据，不显示
        if(rsp.rspInfoField.errorID != 0){

        }else{
            handleRspTradeField(rsp.rspField!!)
        }
        if(rsp.bIsLast){
            liveData.postValue(getTradeDataList())
        }
    }

    private fun handleRspTradeField(field:RspTradeField){
        val dateKey = "${field.tradeID}${field.orderSysID}${field.investorID}"
        tradeDataCollection[dateKey] = field
    }

}