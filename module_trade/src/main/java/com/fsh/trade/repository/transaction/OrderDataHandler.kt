package com.fsh.trade.repository.transaction

import androidx.collection.ArrayMap
import com.fsh.common.util.Omits
import com.fsh.trade.bean.RspOrderField
import com.fsh.trade.bean.RspQryOrder
import com.fsh.trade.bean.RtnOrder

/**
 * 处理委托响应、回报数据
 */
interface IOrderHandler{
    /**
     * 处理查询委托响应
     */
    fun handleRspQryOrder(rsp: RspQryOrder)

    /**
     * 处理委托回报
     */
    fun handleRtnOrder(rtn: RtnOrder)

    /**
     * 获取委托列表
     */
    fun getOrderList():List<RspOrderField>

    /**
     * 获取挂单列表
     */
    fun getWithDrawList():List<RspOrderField>
}

class OrderDataHandler : IOrderHandler{
    //初始化容器大小为100，委托数据多于挂单数据
    private val orderDataContainer:ArrayMap<String,RspOrderField> = ArrayMap(100)
    //初始化20条数据,挂单数据会比较少
    private val withDrawOrderDataContainer:ArrayMap<String,RspOrderField> = ArrayMap(20)
    override fun handleRspQryOrder(rsp: RspQryOrder) {
        dealRspOrderFieldData(rsp.rspField)
    }

    override fun handleRtnOrder(rtn: RtnOrder) {
        dealRspOrderFieldData(rtn.rspField)
    }

    private fun dealRspOrderFieldData(data:RspOrderField?){
        //无效数据 不处理
        if(data == null || Omits.isOmit(data.orderSysID)){
            return
        }
        //存在数据容器中的数据
        val orderKey = "${data.frontID}${data.sessionID}${data.orderSysID}${data.instrumentID}${data.investorID}"
        orderDataContainer[orderKey] = data
        //没有全部成交
        if(data.orderStatus.toInt() != 48){
            withDrawOrderDataContainer[orderKey] = data
        }else{
            //全部成交
            withDrawOrderDataContainer.remove(orderKey)
        }
    }

    override fun getOrderList(): List<RspOrderField> {
       return ArrayList<RspOrderField>(orderDataContainer.values)
    }

    override fun getWithDrawList(): List<RspOrderField> {
        return ArrayList<RspOrderField>(withDrawOrderDataContainer.values)
    }

}