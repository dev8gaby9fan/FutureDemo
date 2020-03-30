package com.fsh.trade.bean

import com.sfit.ctp.thosttraderapi.CThostFtdcInputOrderActionField

interface IInputOrderActionField{
    fun toCThostFtdcInputOrderActionField():CThostFtdcInputOrderActionField
}

class CTPInputOrderActionField(val brokerId:String,val investorId:String,val orderActionRef:Int,var orderRef:String,
                               val requestId:Int,val frontId:Int,val sessionId:Int,val exchangeId:String,
                               val orderSysId:String,val actionFlag:Char,val limitPrice:Double?,val volumeChange:Int?,
                               val userId:String?,val instrumentId:String,val investUnitId:String?,
                               val ipAddress:String?,val macAddress:String?) : IInputOrderActionField{
    override fun toCThostFtdcInputOrderActionField(): CThostFtdcInputOrderActionField =
        CThostFtdcInputOrderActionField().apply {
            this.brokerID = brokerId
            this.investorID = investorId
            this.orderActionRef = this@CTPInputOrderActionField.orderActionRef
            this.orderRef = this@CTPInputOrderActionField.orderRef
            this.requestID = this@CTPInputOrderActionField.requestId
            this.frontID = frontId
            this.sessionID = sessionId
            this.exchangeID = exchangeId
            this.orderSysID = orderSysId
            this.actionFlag = this@CTPInputOrderActionField.actionFlag
            this.limitPrice = this@CTPInputOrderActionField.limitPrice?: 0.0
            this.volumeChange = this@CTPInputOrderActionField.volumeChange?:0
            this.userID = userId
            this.instrumentID = instrumentId
            this.investUnitID = investUnitId
            this.ipAddress = this@CTPInputOrderActionField.ipAddress
            this.macAddress = this@CTPInputOrderActionField.macAddress
        }

}