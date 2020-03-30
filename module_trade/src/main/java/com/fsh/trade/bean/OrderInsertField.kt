package com.fsh.trade.bean

import com.fsh.trade.enums.*
import com.sfit.ctp.thosttraderapi.CThostFtdcInputOrderField

interface IOrderInsertField{
    fun toCThostFtdcInputOrderField():CThostFtdcInputOrderField
}

class CTPOrderInsertField(val brokerId:String,val investorId:String,val instrumentId:String,val orderRef:String,
                          val userId:String,val priceType: CTPOrderPriceType,val direction:CTPDirection,val combOffset:CTPCombOffsetFlag,
                          val hedge:CTPHedgeType,val limitPrice:Double,val volumeTotalOriginal:Int,val timeCondition:CTPTimeConditionType,
                          val gtdDate:String,val volumeCondition:CTPVolumeConditionType,val minVolume:Int,val conditionType: CTPContingentConditionType,
                          val stopPrice:Double,val forceCloseReason:CTPForceCloseReasonType,val isAutoSuspend:Int,val businessUnit:String?,
                          val requestId:Int,val userForceClose:Int,val isSwapOrder:Int,val exchangeId:String,
                          val investUnitId:String?,val accountId:String,val currencyId:String?,val clientId:String,
                          val ipAddress:String,val macAddress:String) : IOrderInsertField{



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
            this.stopPrice = this@CTPOrderInsertField.stopPrice
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
            this.clientID = this@CTPOrderInsertField.clientId
            this.ipAddress = this@CTPOrderInsertField.ipAddress
            this.macAddress = this@CTPOrderInsertField.macAddress
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