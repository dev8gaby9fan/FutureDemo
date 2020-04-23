package com.future.trade.repository.transaction

import android.util.Log
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.enums.CTPOrderStatusType
import com.future.trade.repository.TradeApiProvider
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * 处理委托响应、回报数据
 */
interface IOrderHandler : BaseDataHandler<RspOrderField> {
    /**
     * 处理查询委托响应
     */
    fun handleRspQryOrder(rsp: RspQryOrder)

    /**
     * 处理委托回报
     */
    fun handleRtnOrder(rtn: RtnOrder)

    /**
     * 处理报单响应
     */
    fun handleRspOrderInsert(rsp: RspOrderInsert)

    /**
     * 处理报单录入
     */
    fun handleRspOrderAction(rsp: RspOrderAction)

    /**
     * 挂单数据的LiveData
     */
    fun getWithDrawLiveData(): LiveData<List<RspOrderField>>

    fun handleUserLogout()
}

class OrderDataHandler : IOrderHandler {
    private val orderLiveData: MutableLiveData<List<RspOrderField>> = MutableLiveData()
    private val withDrawLiveData: MutableLiveData<List<RspOrderField>> = MutableLiveData()

    //初始化容器大小为100，委托数据多于挂单数据
    private val orderDataContainer: ArrayMap<String, RspOrderField> = ArrayMap(100)
    //初始化20条数据,挂单数据会比较少
    private val withDrawOrderDataContainer: ArrayMap<String, RspOrderField> = ArrayMap(20)

    override fun handleRspQryOrder(rsp: RspQryOrder) {
        dealRspOrderFieldData(rsp.rspField)
        if (rsp.bIsLast) {
            postValues()
        }
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
        dealRspOrderFieldData(rtn.rspField)
        postValues()
    }
    //只有报单失败才有报单响应
    override fun handleRspOrderInsert(rsp: RspOrderInsert) {
        val user = TradeApiProvider.providerCTPTradeApi().getCurrentUser()!!
        val key = "${user.frontID}${user.sessionID}${rsp.rspField?.orderRef}${rsp.rspField?.instrumentID}${rsp.rspField?.userID}"
        val order = orderDataContainer[key]
        if(order != null){
            order.orderStatus = CTPOrderStatusType.ACTION.code
            order.statusMsg = rsp.rspInfoField.errorMsg
            withDrawOrderDataContainer.remove(key)
            postValues()
        }
    }

    override fun handleRspOrderAction(rsp: RspOrderAction) {
        if (rsp.rspInfoField.errorID != 0 && !Omits.isOmit(rsp.rspInfoField.errorID)) {
            //TODO 这里需要找到那一笔报单 更新它的状态
        }
    }

    private fun dealRspOrderFieldData(data: RspOrderField?) {
        //无效数据 不处理
        if (data == null) {
            return
        }
        //存在数据容器中的数据
        val orderKey =
            "${data.frontID}${data.sessionID}${data.orderRef}${data.instrumentID}${data.investorID}"
        orderDataContainer[orderKey] = data
        val orderStatus = CTPOrderStatusType.from(data.orderStatus)
        //没有全部成交
        if (!orderStatus.isOver) {
            withDrawOrderDataContainer[orderKey] = data
        } else {
            //全部成交
            withDrawOrderDataContainer.remove(orderKey)
        }
    }

    private fun postValues(){
        orderLiveData.postValue(orderDataContainer.values.sortedBy { order -> order.insertDate+order.insertTime })
        withDrawLiveData.postValue(ArrayList(withDrawOrderDataContainer.values.sortedBy { order -> order.insertDate+order.insertTime }))
    }

    override fun getLiveData(): LiveData<List<RspOrderField>> = orderLiveData

    override fun getWithDrawLiveData(): LiveData<List<RspOrderField>> = withDrawLiveData

    override fun handleUserLogout() {
        orderDataContainer.clear()
        withDrawOrderDataContainer.clear()
        postValues()
    }
}