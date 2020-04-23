package com.future.trade.repository.transaction

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.util.Omits
import com.future.trade.bean.RspQryTrade
import com.future.trade.bean.RspTradeField
import com.future.trade.bean.RtnTrade

interface ITradeDataHandler : BaseDataHandler<RspTradeField> {
    /**
     * 处理查询响应
     */
    fun handleRspQryTrade(rsp: RspQryTrade)

    /**
     * 处理成交回报
     */
    fun handleRtnQryTrade(rtn: RtnTrade)

    fun handleUserLogout()
}

class TradeDataHandler : ITradeDataHandler {
    private val liveData: MutableLiveData<List<RspTradeField>> = MutableLiveData()
    private val tradeDataCollection: ArrayMap<String, RspTradeField> = ArrayMap(200)

    override fun getLiveData(): LiveData<List<RspTradeField>> = liveData

    override fun handleRtnQryTrade(rtn: RtnTrade) {
        handleRspTradeField(rtn.rspField)
        //新增的成交明细需要返回给界面上
        liveData.postValue(tradeDataCollection.values.sortedBy { o -> o.tradeDate+o.tradeTime })
    }

    override fun handleRspQryTrade(rsp: RspQryTrade) {
        //错误数据，不显示
        if (rsp.rspField == null || (rsp.rspInfoField.errorID != 0 && !Omits.isOmit(rsp.rspInfoField.errorID))) {

        } else {
            handleRspTradeField(rsp.rspField!!)
        }
        if (rsp.bIsLast) {
            liveData.postValue(tradeDataCollection.values.sortedBy { o -> o.tradeDate+o.tradeTime })
        }
    }

    private fun handleRspTradeField(field: RspTradeField) {
        val dateKey = "${field.tradeID}${field.orderSysID}${field.investorID}"
        tradeDataCollection[dateKey] = field
    }

    override fun handleUserLogout() {
        tradeDataCollection.clear()
        liveData.postValue(emptyList())
    }

}