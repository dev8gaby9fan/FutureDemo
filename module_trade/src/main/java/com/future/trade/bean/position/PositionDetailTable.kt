package com.future.trade.bean.position

import com.fsh.common.util.ARouterUtils
import com.future.trade.bean.*
import com.future.trade.enums.CTPOrderStatusType
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * 持仓明细存储容器
 */
class PositionDetailTable : Comparator<String>{

    //根据持仓明细的Key排序
    private val map:TreeMap<String,RspPositionDetailField> = TreeMap(this)
    //委托记录，主要用于计算冻结手数
    private val orderMap:MutableMap<String,RspOrderField> = HashMap()
    //持仓手数
    var posVolume:Int = 0
        private set(value){
            field = value
        }
    //冻结手数
    var frozenVolume:Int = 0
        private set(value){
            field = value
        }
    //开仓成本
    var openCost:Double = 0.0
        private set(value){
            field = value
        }
    //持仓成本
    var posCost:Double = 0.0
        private set(value){
            field = value
        }


    override fun compare(o1: String?, o2: String?): Int {
        if(o2 == o1) return 0
        if(o1 == null) return -1
        if(o2 == null) return 1
        return o1.compareTo(o2)
    }

    fun putPositionDetail(field:RspPositionDetailField){
        val key = field.openDate + field.tradeID.trim() + field.tradeType
        //如果是二次查询，先将之前的数据删除，然后再用新数据重新计算一下
        if(map.containsKey(key)){
            val pre = map[key]
            posVolume -= pre!!.volume
            openCost -= pre.openPrice
            posCost -= pre.lastSettlementPrice
        }
        map[key] = field
        posVolume += field.volume
        openCost  += field.openPrice
        posCost += field.lastSettlementPrice
    }

    fun removePositionDetail(key:String):RspPositionDetailField?{
        val pos = map.remove(key)
        if(pos != null){
            posVolume -= pos.volume
            openCost -= pos.openPrice
        }
        return pos
    }

    fun closePositionByRtnTrade(rtn:RtnTrade):Pair<RtnTrade,Boolean>{
        if(map.isEmpty()){
            return Pair(rtn,false)
        }
        //需要平掉的仓位
        var needToClose = rtn.rspField.volume
        val iterator = map.iterator()
        while (iterator.hasNext()){
            val mapItemEntity = iterator.next()
            if(mapItemEntity.value.volume < needToClose){
                iterator.remove()
                needToClose -= mapItemEntity.value.volume
            }else{
                needToClose = 0
                mapItemEntity.value.volume -= needToClose
                break
            }
        }
        //这里获取合约的时候，合约ID格式是快期合约格式
        val instrument = ARouterUtils.getQuoteService().getInstrumentById("${rtn.rspField.exchangeID}.${rtn.rspField.instrumentID}")
        val closedVolume = rtn.rspField.volume - needToClose
        //改容器还剩下多少仓位
        posVolume -= closedVolume
        //还需要将持仓成本减去 成交回报，都是今仓，所以这里的价格都用开仓价格计算
        posCost -= closedVolume * rtn.rspField.price * instrument!!.volumeMultiple
        posCost -= closedVolume * rtn.rspField.price * instrument.volumeMultiple
        //需要进一步平的仓位
        rtn.rspField.volume = needToClose
        return Pair(rtn,needToClose == 0)
    }

    fun onRspQryOrder(rsp: RspQryOrder):Pair<RspQryOrder,Boolean>{
        if(posVolume == 0){
            return Pair(rsp,false)
        }
        val returnField = handleRspOrderField(rsp.rspField!!)
        rsp.rspField = returnField.first
        return Pair(rsp,returnField.second)
    }

    fun onRtnOrder(rtn:RtnOrder):Pair<RtnOrder,Boolean>{
        if(posVolume == 0){
            return Pair(rtn,false)
        }
        val returnField = handleRspOrderField(rtn.rspField)
        rtn.rspField = returnField.first
        return Pair(rtn,returnField.second)
    }

    fun onRspOrderInsert(rsp:RspOrderInsert):Pair<RspOrderInsert,Boolean>{
        return Pair(rsp,false)
    }

    private fun handleRspOrderField(rspOrderField: RspOrderField):Pair<RspOrderField,Boolean>{
        val field = rspOrderField
        val orderKey = field.frontID.toString() + field.sessionID.toString() + field.orderRef + field.instrumentID
        if(orderMap.containsKey(orderKey)){
            val pre = orderMap[orderKey]!!
            //这一笔委托的挂单数量，先手动减去
            frozenVolume -= (pre.volumeTotalOriginal - pre.volumeTraded)
        }
        val orderStatus = CTPOrderStatusType.from(field.orderStatus)
        val storeField = field.clone()
        val needToFrozen = if(!orderStatus.isOver) storeField.volumeTotalOriginal - storeField.volumeTraded else 0
        val currentOrderFrozenVol: Int
        //需要冻结的手数大于可以冻结的手数，冻结手数就等于持仓手数
        //同时，存储在map中的委托记录的冻结手数为此笔委托冻结的手数
        if(needToFrozen > (posVolume - frozenVolume)){
            //当前委托冻结的持仓数量
            currentOrderFrozenVol = posVolume - frozenVolume
            //总的冻结数据=持仓数量
            frozenVolume = posVolume
            //将存储在map中的委托的成交量修改为总量-委托的挂单量
            storeField.volumeTraded = storeField.volumeTotalOriginal - currentOrderFrozenVol
            //计算一下还有多少手需要进一步冻结的；成交量 = 总量 - (还剩没有冻结的 = 需要冻结量 - 已经冻结量)
            field.volumeTraded = field.volumeTotalOriginal - (needToFrozen - currentOrderFrozenVol)
        }else{
            //当前委托的冻结数量 = 委托的挂单量
            currentOrderFrozenVol = needToFrozen
            frozenVolume += currentOrderFrozenVol
        }
        orderMap[orderKey] = storeField
        return Pair(rspOrderField,currentOrderFrozenVol == needToFrozen)
    }

    fun contains(key:String):Boolean = map.contains(key)
}

/**
 * 平仓之后，还剩下多少需要平的
 * @param remainVolume 剩下的手数
 * @param remainOpenCost 剩下的开仓成本
 * @param remainPositionCost 剩下的持仓成本
 */
data class CloseReturnParams(var remainVolume:Int,var remainOpenCost:Double,var remainPositionCost:Double)