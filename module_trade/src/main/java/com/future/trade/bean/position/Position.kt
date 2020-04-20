package com.future.trade.bean.position

import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType
import com.future.trade.enums.ExchangeType
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.util.DiffComparable

interface PositionDataHandler{
    /**
     * 处理持仓明细响应数据
     */
    fun onRspPositionDetail(rsp:RspPositionDetailField)
    /**
     * 委托查询响应处理
     * RspQryOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRspQryOrder(rsp:RspQryOrder):Pair<RspQryOrder,Boolean>

    /**
     * 委托回报
     * RtnOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRtnOrder(rtn:RtnOrder):Pair<RtnOrder,Boolean>

    /**
     * 委托响应处理
     * RspOrderInsert 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRspOrderInsert(rsp:RspOrderInsert):Pair<RspOrderInsert,Boolean>


    /**
     * 成交回报处理
     * RtnOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRtnTrade(rtn:RtnTrade):Pair<RtnTrade,Boolean>
}

/**
 * 持仓数据
 */
interface Position : PositionDataHandler , DiffComparable<Position>{
    //获取合约ID
    fun getInstrumentId():String
    //获取交易所ID
    fun getExchangeId():String
    //持仓手数
    fun getPosition():Int
    //可用手数
    fun getAvailable():Int
    //投机仓位数量
    fun getSpecPosition():Int
    //套保仓位数量
    fun getHedgePosition():Int

    //开仓成本
    fun getOpenCost():Double
    //获取持仓成本
    fun getPositionCost():Double

    //获取持仓盈亏等同于逐日盈亏
    fun getPositionProfit():Double
    //获取开仓盈亏等同于逐笔盈亏
    fun getOpenPositionProfit():Double
    //持仓方向
    fun getDirection():CTPDirection
    //投机套保仓位类型
    fun getHedgeType():CTPHedgeType
    //创建平仓的委托数据
    fun getCloseOrderFields(volume: Int, priceType: SupportTransactionOrderPrice, limitPrice:Double):List<IOrderInsertField>

    fun isDataChanged():Boolean

    fun dataChanged(isChanged:Boolean)
}
abstract class SimplePosition : Position {
    override fun getInstrumentId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getExchangeId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAvailable(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSpecPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHedgePosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDirection(): CTPDirection {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHedgeType(): CTPHedgeType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun compare(obj: Position): Boolean {
        return getInstrumentId() == obj.getInstrumentId() && getPosition() == obj.getPosition()
                && getAvailable() == obj.getAvailable() && getExchangeId() == obj.getExchangeId()
                && getDirection() == obj.getDirection() && !isDataChanged()
    }

     /**
      * =====================================数据处理===============================================
      */

     override fun onRspPositionDetail(rsp: RspPositionDetailField) {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }
}

/**
 * 不同交易所的合约
 */
abstract class ExchangePosition : SimplePosition(){
    //今投机仓
    protected var tdSpecPos:PositionDetailTable = PositionDetailTable()
    //今套保仓
    protected var tdHedgePos:PositionDetailTable = PositionDetailTable()
    //昨投机仓
    protected var ydSpecPos:PositionDetailTable = PositionDetailTable()
    //今套保仓
    protected var ydHedgePos:PositionDetailTable = PositionDetailTable()

    private var insId:String? = null
    private var exchId:String? = null
    private var direction:CTPDirection? = null
    private var dataChagned:Boolean = false
    /**
     * 持仓明细处理
     */
    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        if(Omits.isOmit(insId)){
            insId = rsp.instrumentID
        }
        if(Omits.isOmit(exchId)){
            exchId = rsp.exchangeID
        }
        if(direction == null){
            direction = CTPDirection.from(rsp.direction)
        }
        dataChagned = true
        //今仓
        if(rsp.openDate == rsp.tradingDay){
            //今投机仓
            if(rsp.hedgeFlag == CTPHedgeType.Speculation.code){
                tdSpecPos.putPositionDetail(rsp)
            }else{
                tdHedgePos.putPositionDetail(rsp)
            }
        }else{
            //昨投机仓
            if(rsp.hedgeFlag == CTPHedgeType.Speculation.code){
                ydSpecPos.putPositionDetail(rsp)
            }else{
                ydHedgePos.putPositionDetail(rsp)
            }
        }
    }

    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        if(Omits.isOmit(insId)){
            insId = rtn.rspField.instrumentID
        }
        if(Omits.isOmit(exchId)){
            exchId = rtn.rspField.exchangeID
        }
        if(direction == null){
            direction = CTPDirection.from(rtn.rspField.direction)
        }
        dataChagned = true
        return if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.Open.offset){
            onRtnTradeOpenPosition(rtn)
        }else{//平仓
            onRtnTradeClosePosition(rtn)
        }
    }

    private fun onRtnTradeOpenPosition(rtn: RtnTrade): Pair<RtnTrade, Boolean>{
        //投机仓位，直接将仓位存储在今投机容器内
        return if(rtn.rspField.hedgeFlag == CTPHedgeType.Speculation.code){
            onStorePositionDetailByTrade(rtn,tdSpecPos)
        }else{
            onStorePositionDetailByTrade(rtn,tdHedgePos)
        }

    }

    private fun onStorePositionDetailByTrade(rtn:RtnTrade,posTable:PositionDetailTable): Pair<RtnTrade, Boolean>{
        val positionDetail = RspPositionDetailField.fromTrade(rtn.rspField)
        posTable.putPositionDetail(positionDetail)
        return Pair(rtn,true)
}

    abstract fun onRtnTradeClosePosition(rtn: RtnTrade) : Pair<RtnTrade, Boolean>

    override fun getDirection(): CTPDirection = direction!!

    override fun getPosition(): Int {
        return tdHedgePos.posVolume + tdSpecPos.posVolume + ydHedgePos.posVolume + ydSpecPos.posVolume
    }

    override fun getAvailable(): Int {
        return tdHedgePos.posVolume + tdSpecPos.posVolume + ydHedgePos.posVolume + ydSpecPos.posVolume - (ydHedgePos.frozenVolume + ydSpecPos.frozenVolume + tdSpecPos.frozenVolume + tdHedgePos.frozenVolume)
    }

    override fun getSpecPosition(): Int {
        return tdSpecPos.posVolume + ydSpecPos.posVolume
    }

    override fun getHedgePosition(): Int {
        return tdHedgePos.posVolume + ydHedgePos.posVolume
    }

    override fun getOpenCost(): Double {
        return tdSpecPos.openCost + tdHedgePos.openCost + ydSpecPos.openCost + ydHedgePos.openCost
    }

    override fun getPositionCost(): Double {
        return tdSpecPos.posCost + tdHedgePos.posCost + ydSpecPos.posCost + ydHedgePos.posCost
    }

    override fun getPositionProfit(): Double {
        return 0.0
    }

    override fun getOpenPositionProfit(): Double {
        return 0.0
    }

    override fun getInstrumentId(): String {
        return insId ?: Omits.OmitPrice
    }

    override fun getExchangeId(): String {
        return exchId ?: Omits.OmitPrice
    }

    override fun isDataChanged(): Boolean = dataChagned

    override fun dataChanged(isChanged: Boolean) {
        dataChagned = isChanged
    }

    companion object{
        fun newInstance(exchangeId:String):ExchangePosition{
            val from = ExchangeType.from(exchangeId)
            requireNotNull(from)
            return from.newExchangePositionInstance()
        }
    }
}







