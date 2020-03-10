package com.fsh.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 期货信息类
 *
 */
//交易所信息
@Parcelize
data class ExchangeInfo(var name:String,var id:String) :Parcelable{
    private val insMap:ConcurrentHashMap<String, InstrumentInfo> by lazy {
        ConcurrentHashMap<String, InstrumentInfo>()
    }

    private val productMap:ConcurrentHashMap<String, ProductInfo> by lazy{
        ConcurrentHashMap<String, ProductInfo>()
    }

    fun addInstrument(ins: InstrumentInfo){
        insMap[ins.id] = ins
        var product = getProduct(ins.pid)
        if(product == null){
            product = ProductInfo(ins.name, ins.pid, ins.eid)
            productMap[product.id] = product
        }
    }

    fun getInstrument(id:String): InstrumentInfo? = insMap[id]

    fun getProduct(id:String): ProductInfo? = productMap[id]
}

//品种信息
@Parcelize
data class ProductInfo(var name:String,var id:String,var eid:String) : Parcelable

//合约信息
@Parcelize
data class InstrumentInfo(var name:String,var id:String,var eid:String,var pid:String) : Parcelable{
    var classType:String? = null
    var volumeMultiple:Int? = null
    var priceTick:String? = null
    var priceDecs:String? = null
    var sortkey:String? = null
    var py:String? = null
    var productShortName:String? = null
    var deliveryYear:String? = null
    var deliveryMonth:String? = null
    var expireTime:String? = null
    var maxMarketOrderVolume:String? = null
    var maxLimitOrderVolume:String? = null
    var margin:String? = null
    var commission:String? = null
    var mmsa:String? = null
    var shortInsId:String?=null

    //组合合约字段
    var leg1symbol:String? = null
    var leg2symbol:String? = null

    //期权合约字段

    var optionclass:String? = null
    var strikeprice:String? = null
    var underlyingmultiple:String? = null
}

//行情数据
@Parcelize
data class QuoteEntity(var id:String) : Parcelable{
    //最后更新时间
    var datetime:String? = null
    //卖一价
    var askprice1:Double? = null
    //卖一量
    var askvolume1:Int? = null
    //买一价
    var bidprice1:Double? = null
    //买一量
    var bidvolume1:Int? = null
    //最新价
    var lastprice:Double? = null
    //均价
    var average:Double? = null
    //成交量
    var volume:Int? = null
    //成交额
    var amount:Int? = null
    //持仓量
    var openinterest:Int? = null
    //最高价
    var highest:Double? = null
    //最低价
    var lowest:Double? = null
    //昨日未平仓量
    var preopeninterest:Int? = null
    //昨收价
    var preclose:Double? = null
    //开仓价
    var open:Double? = null
    //平仓价
    var close:Double? = null
    //跌停价
    var lowerlimit:Double? = null
    //涨停价
    var upperlimit:Double? = null
    //昨结算价
    var presettlement:Double? = null
    //结算价
    var settlement:Double? = null
}

