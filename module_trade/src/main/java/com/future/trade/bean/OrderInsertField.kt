package com.future.trade.bean

import com.fsh.common.util.CommonUtil
import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.future.trade.enums.*
import com.future.trade.repository.TradeApiProvider
import com.future.trade.repository.tradeapi.RtnOrderEvent
import com.sfit.ctp.thosttraderapi.CThostFtdcInputOrderField

interface IOrderInsertField{
    fun toCThostFtdcInputOrderField():CThostFtdcInputOrderField

    fun toOrderString():String
    /**
     * 生成一条报单回报，用于本地计算持仓冻结手数
     */
    fun toRtnOrderEvent():RtnOrderEvent
}

class CTPOrderInsertField(val brokerId:String,val investorId:String,val instrumentId:String,val orderRef:String,
                          val userId:String,val priceType: CTPOrderPriceType,val direction:CTPDirection,val combOffset:CTPCombOffsetFlag,
                          val hedge:CTPHedgeType,val limitPrice:Double,val volumeTotalOriginal:Int,val timeCondition:CTPTimeConditionType,
                          val gtdDate:String,val volumeCondition:CTPVolumeConditionType,val minVolume:Int,val conditionType: CTPContingentConditionType,
                          val stopPrice:Double?,val forceCloseReason:CTPForceCloseReasonType,val isAutoSuspend:Int,val businessUnit:String?,
                          val requestId:Int,val userForceClose:Int,val isSwapOrder:Int,val exchangeId:String,
                          val investUnitId:String?,val accountId:String,val currencyId:String?,val clientId:String?,
                          val ipAddress:String?,val macAddress:String?) : IOrderInsertField{
    override fun toOrderString(): String {
        return "${direction.text}  ${combOffset.text} $instrumentId ${volumeTotalOriginal}手,价格:$limitPrice"
    }


    override fun toCThostFtdcInputOrderField(): CThostFtdcInputOrderField =
        CThostFtdcInputOrderField().apply {
            this.brokerID = brokerId
            investorID = investorId
            instrumentID = instrumentId
            this.orderRef = this@CTPOrderInsertField.orderRef
            userID = userId
            orderPriceType = priceType.code
            this.direction = this@CTPOrderInsertField.direction.direction
            combOffsetFlag = combOffset.offset.toString()
            combHedgeFlag = hedge.code.toString()
            this.limitPrice = this@CTPOrderInsertField.limitPrice
            this.volumeTotalOriginal = this@CTPOrderInsertField.volumeTotalOriginal
            this.timeCondition = this@CTPOrderInsertField.timeCondition.code
            this.gtdDate = this@CTPOrderInsertField.gtdDate
            this.volumeCondition = this@CTPOrderInsertField.volumeCondition.code
            this.minVolume = this@CTPOrderInsertField.minVolume
            this.contingentCondition = this@CTPOrderInsertField.conditionType.code
            if(!Omits.isOmit(this@CTPOrderInsertField.stopPrice)){
                this.stopPrice = this@CTPOrderInsertField.stopPrice!!
            }
            this.forceCloseReason = this@CTPOrderInsertField.forceCloseReason.code
            this.isAutoSuspend = this@CTPOrderInsertField.isAutoSuspend
            this.businessUnit = this@CTPOrderInsertField.businessUnit
            this.requestID = this@CTPOrderInsertField.requestId
            this.userForceClose = this@CTPOrderInsertField.userForceClose
            this.isSwapOrder = this@CTPOrderInsertField.isSwapOrder
            this.exchangeID = this@CTPOrderInsertField.exchangeId
            this.investUnitID = this@CTPOrderInsertField.investUnitId
            this.accountID = this@CTPOrderInsertField.accountId
            this.currencyID = this@CTPOrderInsertField.currencyId
            this.clientID = CommonUtil.getAppId()
            this.ipAddress = this@CTPOrderInsertField.ipAddress
            this.macAddress = this@CTPOrderInsertField.macAddress
        }
    override fun toRtnOrderEvent(): RtnOrderEvent {
        val user = TradeApiProvider.providerCTPTradeApi().getCurrentUser()
        val rspOrderField = RspOrderField(brokerId,investorId,instrumentId,orderRef,userId,priceType.code,direction.direction,combOffset.text,
            hedge.text,limitPrice,volumeTotalOriginal,timeCondition.code,gtdDate,volumeCondition.code,minVolume,conditionType.code,
            stopPrice?:0.0,forceCloseReason.code,isAutoSuspend,businessUnit?:Omits.OmitString,requestId,Omits.OmitString,
            exchangeId,Omits.OmitString,Omits.OmitString,instrumentId,Omits.OmitString,Omits.OmitInt,'0',Omits.OmitInt,
            Omits.OmitString,Omits.OmitInt,Omits.OmitString,'0',CTPOrderStatusType.STATUS_CUSTOME_SEND.code,'0',
            0,volumeTotalOriginal,user!!.tradingDay,DateUtils.formatNow3(),Omits.OmitString,Omits.OmitString,Omits.OmitString,Omits.OmitString,
            Omits.OmitString,Omits.OmitString,Omits.OmitInt,user.frontID,user.sessionID,Omits.OmitString,CTPOrderStatusType.STATUS_CUSTOME_SEND.notion,userForceClose,
            Omits.OmitString,Omits.OmitInt,Omits.OmitString,Omits.OmitInt,isSwapOrder,Omits.OmitString,investUnitId?:Omits.OmitString,user.userID,
            Omits.OmitString,Omits.OmitString,Omits.OmitString)
        return RtnOrderEvent(RtnOrder(rspOrderField))
    }
    companion object{
        //构造一个限价单的委托对象
        fun getLimitPriceOrder(exchangeId:String,instrumentId:String,limitPrice:Double){

        }
        //构造一个市价单的委托对象
        fun getMarketPriceOrder(exchangeId:String,instrumentId:String){

        }
    }
}