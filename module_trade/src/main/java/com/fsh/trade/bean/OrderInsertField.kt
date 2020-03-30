package com.fsh.trade.bean

import com.sfit.ctp.thosttraderapi.CThostFtdcInputOrderField

interface IOrderInsertField{
    fun toCTPInputOrderField():CThostFtdcInputOrderField
}

//class CTPOrderInsertField(val brokerId:String,val investorId:String,val instrumentID:String,val orderRef:String,
//                          val userId:String,) : IOrderInsertField{
//
//
//
//    override fun toCTPInputOrderField(): CThostFtdcInputOrderField {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}