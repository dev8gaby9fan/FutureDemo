package com.future.trade.bean.position

import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.ARouterUtils
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

    /**
     * 交易账号退出登录
     */
    fun onRspUserLogout()

    /**
     * 行情更新
     */
    fun onQuoteUpdate(quoteEntity:QuoteEntity)
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
    //获取昨仓数量
    fun getYesterdayPosition():Int
    //获取今仓数量
    fun getTodayPosition():Int
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

    fun isSelected():Boolean

    fun setSelected(flag:Boolean)
}
abstract class SimplePosition : Position {
    //是否选中状态
    private var selectedStatus:Boolean = false
    override fun getInstrumentId(): String = Omits.OmitString

    override fun getExchangeId(): String = Omits.OmitString

    override fun getPosition(): Int = 0

    override fun getAvailable(): Int = 0

    override fun getSpecPosition(): Int = 0

    override fun getHedgePosition(): Int = 0

    override fun getOpenCost(): Double = 0.0

    override fun getPositionCost(): Double = 0.0

    override fun getPositionProfit(): Double = 0.0

    override fun getOpenPositionProfit(): Double = 0.0

    override fun getDirection(): CTPDirection = CTPDirection.Unknown

    override fun getHedgeType(): CTPHedgeType = CTPHedgeType.Unknown

    override fun compare(obj: Position): Boolean {
        return getInstrumentId() == obj.getInstrumentId() && getPosition() == obj.getPosition()
                && getAvailable() == obj.getAvailable() && getExchangeId() == obj.getExchangeId()
                && getDirection() == obj.getDirection() && !isDataChanged()
    }

    override fun isSelected(): Boolean = selectedStatus

    override fun setSelected(flag: Boolean) {
        selectedStatus = flag
    }

    /**
      * =====================================数据处理===============================================
      */

     override fun onRspPositionDetail(rsp: RspPositionDetailField) {}

     override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> = Pair(rsp,false)

     override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> = Pair(rtn,false)

     override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> = Pair(rsp,false)

     override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> = Pair(rtn,false)

    override fun onQuoteUpdate(quoteEntity: QuoteEntity) {}
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
    private var dataChanged:Boolean = false
    private var posProfit:Double = 0.0
    private var openProfit:Double = 0.0
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
        dataChanged = true
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
        dataChanged = true
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

    override fun onRspUserLogout() {
        tdSpecPos.onUserLogout()
        tdHedgePos.onUserLogout()
        ydSpecPos.onUserLogout()
        ydHedgePos.onUserLogout()
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

    override fun getTodayPosition(): Int {
        return tdSpecPos.posVolume + tdHedgePos.posVolume
    }

    override fun getYesterdayPosition(): Int {
        return ydSpecPos.posVolume + ydHedgePos.posVolume
    }

    override fun getOpenCost(): Double {
        return tdSpecPos.openCost + tdHedgePos.openCost + ydSpecPos.openCost + ydHedgePos.openCost
    }

    override fun getPositionCost(): Double {
        return tdSpecPos.posCost + tdHedgePos.posCost + ydSpecPos.posCost + ydHedgePos.posCost
    }

    override fun getPositionProfit(): Double {
        return posProfit
    }

    override fun getOpenPositionProfit(): Double {
        return openProfit
    }

    override fun getInstrumentId(): String {
        return insId ?: Omits.OmitPrice
    }

    override fun getExchangeId(): String {
        return exchId ?: Omits.OmitPrice
    }

    override fun isDataChanged(): Boolean = dataChanged

    override fun dataChanged(isChanged: Boolean) {
        dataChanged = isChanged
    }

    override fun onQuoteUpdate(quoteEntity: QuoteEntity) {
        val posCost = getPositionCost()
        val openCost = getOpenCost()
        val instrument = ARouterUtils.getQuoteService().getInstrumentById("$exchId.$insId")?:return
        if(posCost > 0.0 && !Omits.isOmit(quoteEntity.last_price)){
            posProfit = calculateProfit(posCost,quoteEntity.last_price.toDouble(),instrument)
        }
        if(openCost > 0.0 && !Omits.isOmit(quoteEntity.last_price)){
            openProfit = calculateProfit(openCost,quoteEntity.last_price.toDouble(),instrument)
        }
    }

    private fun calculateProfit(cost:Double,lastPrice:Double,instrument:InstrumentInfo):Double{
       return if(getDirection() == CTPDirection.Buy){
            (lastPrice * getPosition() - cost)*instrument.volumeMultiple
        }else{
            (cost - lastPrice * getPosition())*instrument.volumeMultiple
        }
    }

    companion object{
        fun newInstance(exchangeId:String):ExchangePosition{
            val from = ExchangeType.from(exchangeId)
            requireNotNull(from)
            return from.newExchangePositionInstance()
        }
    }
}







