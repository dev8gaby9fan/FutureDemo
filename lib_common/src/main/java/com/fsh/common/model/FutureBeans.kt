package com.fsh.common.model

import android.os.Parcelable
import com.fsh.common.R
import com.fsh.common.util.Omits
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.collections.ArrayList

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
data class ExchangeInfo(var name:String,var id:String,var sortKey:Int) :Parcelable{
    @IgnoredOnParcel
    private val insList:ArrayList<InstrumentInfo> = ArrayList(20)
    @IgnoredOnParcel
    private val insMap:ConcurrentHashMap<String, InstrumentInfo> = ConcurrentHashMap()

    @IgnoredOnParcel
    private val productMap:ConcurrentHashMap<String, ProductInfo> = ConcurrentHashMap()

    fun addInstrument(ins: InstrumentInfo,tradingTime: JsonElement?){
        insMap[ins.id] = ins
        var product = getProduct(ins.pid)
        if(product == null){
            product = ProductInfo(ins.name, ins.pid, ins.eid)
            productMap[product.id] = product
        }
        if(tradingTime != null){
            val tradingTimeObj = Gson().fromJson(tradingTime,TradingTime::class.java)
            product.tradingTime = tradingTimeObj
        }
    }

    fun getInstrument(id:String): InstrumentInfo? = insMap[id]

    fun getProduct(id:String): ProductInfo? = productMap[id]

    fun getInstruments():List<InstrumentInfo>{
        if(insList.isEmpty()){
            insList.addAll(insMap.values)
            insList.sortWith(Comparator { ins1, ins2 -> ins1.sortkey.compareTo(ins2.sortkey) })
        }
        return insList
    }

    fun searchInstrument(key:String):List<InstrumentInfo>{
        return getInstruments().filter{(it.py?.contains(key,true) ?: false)
                || it.name.contains(key,true)
                || it.pid.contains(key,false) }
    }
}

//品种信息
@Parcelize
class ProductInfo(var name:String,var id:String,var eid:String) : Parcelable{
    var tradingTime:TradingTime? = null
            set(value){
                if(field == null){
                    field = value
                }
            }
}

data class TradingTime(var day:List<List<String>>,var night:List<List<String>>)

//合约信息
@Parcelize
data class InstrumentInfo(var name:String,var id:String,var eid:String,var pid:String) : Parcelable{
    var classType:String? = null
    var volumeMultiple:Int = 1
    var priceTick:String? = null
    var priceDecs:String? = null
    var sortkey:Int = Omits.OmitInt
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

    //组合合约字段
    var leg1symbol:String? = null
    var leg2symbol:String? = null

    //期权合约字段

    var optionclass:String? = null
    var strikeprice:String? = null
    var underlyingmultiple:String? = null
    //CTP 合约ID格式
    var ctpInstrumentId:String = Omits.OmitString
    //CTP 合约格式的交易所信息
    var ctpExchangeId:String = Omits.OmitString
    //是否是主力合约
    var isMainIns:Boolean = false
    //主力合约ID
    var mainInsId:String = Omits.OmitString
}

/**
 *
 */
//行情数据
@Parcelize
data class QuoteEntity(var instrument_id:String) : Parcelable{
    //深度行情(10档行情),目前天勤系统有数据的只有5档行情
    var ask_price10:String = Omits.OmitPrice
    var ask_volume10:String = Omits.OmitPrice
    var ask_price9:String = Omits.OmitPrice
    var ask_volume9:String = Omits.OmitPrice
    var ask_price8:String = Omits.OmitPrice
    var ask_volume8:String = Omits.OmitPrice
    var ask_price7:String = Omits.OmitPrice
    var ask_volume7:String = Omits.OmitPrice
    var ask_price6:String = Omits.OmitPrice
    var ask_volume6:String = Omits.OmitPrice
    var ask_price5:String = Omits.OmitPrice
    var ask_volume5:String = Omits.OmitPrice
    var ask_price4:String = Omits.OmitPrice
    var ask_volume4:String = Omits.OmitPrice
    var ask_price3:String = Omits.OmitPrice
    var ask_volume3:String = Omits.OmitPrice
    var ask_price2:String = Omits.OmitPrice
    var ask_volume2:String = Omits.OmitPrice
    var ask_price1:String = Omits.OmitPrice
    var ask_volume1:String = Omits.OmitPrice
    var bid_price1:String = Omits.OmitPrice
    var bid_volume1:String = Omits.OmitPrice
    var bid_price2:String = Omits.OmitPrice
    var bid_volume2:String = Omits.OmitPrice
    var bid_price3:String = Omits.OmitPrice
    var bid_volume3:String = Omits.OmitPrice
    var bid_price4:String = Omits.OmitPrice
    var bid_volume4:String = Omits.OmitPrice
    var bid_price5:String = Omits.OmitPrice
    var bid_volume5:String = Omits.OmitPrice
    var bid_price6:String = Omits.OmitPrice
    var bid_volume6:String = Omits.OmitPrice
    var bid_price7:String = Omits.OmitPrice
    var bid_volume7:String = Omits.OmitPrice
    var bid_price8:String = Omits.OmitPrice
    var bid_volume8:String = Omits.OmitPrice
    var bid_price9:String = Omits.OmitPrice
    var bid_volume9:String = Omits.OmitPrice
    var bid_price10:String = Omits.OmitPrice
    var bid_volume10:String = Omits.OmitPrice

    var last_price:String = Omits.OmitPrice
    var highest:String = Omits.OmitPrice
    var lowest:String = Omits.OmitPrice
    var open:String = Omits.OmitPrice
    var close:String = Omits.OmitPrice
    var average:String = Omits.OmitPrice
    var volume:String = Omits.OmitPrice
    var amount:String = Omits.OmitPrice
    var open_interest:String = Omits.OmitPrice
    var settlement:String = Omits.OmitPrice
    var upper_limit:String = Omits.OmitPrice
    var lower_limit:String = Omits.OmitPrice
    var pre_open_interest:String = Omits.OmitPrice
    var pre_settlement:String = Omits.OmitPrice
    var pre_close:String = Omits.OmitPrice
    var datetime:String = Omits.OmitPrice
    var updown:String = Omits.OmitPrice
    var updown_ratio:String = Omits.OmitPrice
    //自定义的字符串
    var quoteTextColor:Int = R.color.quote_white

    fun updateQuoteEntity(quoteEntity: QuoteEntity){
        if(!Omits.isOmit(quoteEntity.ask_price10)){
            ask_price10 = quoteEntity.ask_price10
        }
        if(!Omits.isOmit(quoteEntity.ask_volume10)){
            ask_volume10 = quoteEntity.ask_volume10
        }
        if(!Omits.isOmit(quoteEntity.ask_price9)){
            ask_price9 = quoteEntity.ask_price9
        }
        if(!Omits.isOmit(quoteEntity.ask_volume9)){
            ask_volume9 = quoteEntity.ask_volume9
        }
        if(!Omits.isOmit(quoteEntity.ask_price8)){
            ask_price8 = quoteEntity.ask_price8
        }
        if(!Omits.isOmit(quoteEntity.ask_volume8)){
            ask_volume8 = quoteEntity.ask_volume8
        }
        if(!Omits.isOmit(quoteEntity.ask_price7)){
            ask_price7 = quoteEntity.ask_price7
        }
        if(!Omits.isOmit(quoteEntity.ask_volume7)){
            ask_volume7 = quoteEntity.ask_volume7
        }
        if(!Omits.isOmit(quoteEntity.ask_price6)){
            ask_price6 = quoteEntity.ask_price6
        }
        if(!Omits.isOmit(quoteEntity.ask_volume6)){
            ask_volume6 = quoteEntity.ask_volume6
        }
        if(!Omits.isOmit(quoteEntity.ask_price5)){
            ask_price5 = quoteEntity.ask_price5
        }
        if(!Omits.isOmit(quoteEntity.ask_volume5)){
            ask_volume5 = quoteEntity.ask_volume5
        }
        if(!Omits.isOmit(quoteEntity.ask_price4)){
            ask_price4 = quoteEntity.ask_price4
        }
        if(!Omits.isOmit(quoteEntity.ask_volume4)){
            ask_volume4 = quoteEntity.ask_volume4
        }
        if(!Omits.isOmit(quoteEntity.ask_price3)){
            ask_price3 = quoteEntity.ask_price3
        }
        if(!Omits.isOmit(quoteEntity.ask_volume3)){
            ask_volume3 = quoteEntity.ask_volume3
        }
        if(!Omits.isOmit(quoteEntity.ask_price2)){
            ask_price2 = quoteEntity.ask_price2
        }
        if(!Omits.isOmit(quoteEntity.ask_volume2)){
            ask_volume2 = quoteEntity.ask_volume2
        }
        if(!Omits.isOmit(quoteEntity.ask_price1)){
            ask_price1 = quoteEntity.ask_price1
        }
        if(!Omits.isOmit(quoteEntity.ask_volume1)){
            ask_volume1 = quoteEntity.ask_volume1
        }
        if(!Omits.isOmit(quoteEntity.ask_price1)){
            ask_price1 = quoteEntity.ask_price1
        }
        if(!Omits.isOmit(quoteEntity.bid_price1)){
            bid_price1 = quoteEntity.bid_price1
        }
        if(!Omits.isOmit(quoteEntity.bid_volume1)){
            bid_volume1 = quoteEntity.bid_volume1
        }
        if(!Omits.isOmit(quoteEntity.bid_price2)){
            bid_price2 = quoteEntity.bid_price2
        }
        if(!Omits.isOmit(quoteEntity.bid_volume2)){
            bid_volume2 = quoteEntity.bid_volume2
        }
        if(!Omits.isOmit(quoteEntity.bid_price3)){
            bid_price3 = quoteEntity.bid_price3
        }
        if(!Omits.isOmit(quoteEntity.bid_volume3)){
            bid_volume3 = quoteEntity.bid_volume3
        }
        if(!Omits.isOmit(quoteEntity.bid_price4)){
            bid_price4 = quoteEntity.bid_price4
        }
        if(!Omits.isOmit(quoteEntity.bid_volume4)){
            bid_volume4 = quoteEntity.bid_volume4
        }
        if(!Omits.isOmit(quoteEntity.bid_price5)){
            bid_price5 = quoteEntity.bid_price5
        }
        if(!Omits.isOmit(quoteEntity.bid_volume5)){
            bid_volume5 = quoteEntity.bid_volume5
        }
        if(!Omits.isOmit(quoteEntity.bid_price6)){
            bid_price6 = quoteEntity.bid_price6
        }
        if(!Omits.isOmit(quoteEntity.bid_volume6)){
            bid_volume6 = quoteEntity.bid_volume6
        }
        if(!Omits.isOmit(quoteEntity.bid_price7)){
            bid_price7 = quoteEntity.bid_price7
        }
        if(!Omits.isOmit(quoteEntity.bid_volume7)){
            bid_volume7 = quoteEntity.bid_volume7
        }
        if(!Omits.isOmit(quoteEntity.bid_price8)){
            bid_price8 = quoteEntity.bid_price8
        }
        if(!Omits.isOmit(quoteEntity.bid_volume8)){
            bid_volume8 = quoteEntity.bid_volume8
        }
        if(!Omits.isOmit(quoteEntity.bid_price9)){
            bid_price9 = quoteEntity.bid_price9
        }
        if(!Omits.isOmit(quoteEntity.bid_volume9)){
            bid_volume9 = quoteEntity.bid_volume9
        }
        if(!Omits.isOmit(quoteEntity.bid_price10)){
            bid_price10 = quoteEntity.bid_price10
        }
        if(!Omits.isOmit(quoteEntity.bid_volume10)){
            bid_volume10 = quoteEntity.bid_volume10
        }
        if(!Omits.isOmit(quoteEntity.last_price)){
            last_price = quoteEntity.last_price
        }
        if(!Omits.isOmit(quoteEntity.highest)){
            highest = quoteEntity.highest
        }
        if(!Omits.isOmit(quoteEntity.lowest)){
            lowest = quoteEntity.lowest
        }
        if(!Omits.isOmit(quoteEntity.open)){
            open = quoteEntity.open
        }
        if(!Omits.isOmit(quoteEntity.close)){
            close = quoteEntity.close
        }
        if(!Omits.isOmit(quoteEntity.average)){
            average = quoteEntity.average
        }
        if(!Omits.isOmit(quoteEntity.volume)){
            volume = quoteEntity.volume
        }
        if(!Omits.isOmit(quoteEntity.amount)){
            amount = quoteEntity.amount
        }
        if(!Omits.isOmit(quoteEntity.open_interest)){
            open_interest = quoteEntity.open_interest
        }
        if(!Omits.isOmit(quoteEntity.settlement)){
            settlement = quoteEntity.settlement
        }
        if(!Omits.isOmit(quoteEntity.upper_limit)){
            upper_limit = quoteEntity.upper_limit
        }
        if(!Omits.isOmit(quoteEntity.lower_limit)){
            lower_limit = quoteEntity.lower_limit
        }
        if(!Omits.isOmit(quoteEntity.pre_open_interest)){
            pre_open_interest = quoteEntity.pre_open_interest
        }
        if(!Omits.isOmit(quoteEntity.pre_settlement)){
            pre_settlement = quoteEntity.pre_settlement
        }
        if(!Omits.isOmit(quoteEntity.pre_close)){
            pre_close = quoteEntity.pre_close
        }
        if(!Omits.isOmit(quoteEntity.datetime)){
            datetime = quoteEntity.datetime
        }
        if(!Omits.isOmit(last_price) && !Omits.isOmit(pre_settlement)){
            var lastP = BigDecimal(last_price)
            var preS = BigDecimal(pre_settlement)
            updown = lastP.subtract(preS).toString()
            updown_ratio  = String.format("%.2f%%",lastP.subtract(preS).divide(preS,BigDecimal.ROUND_HALF_UP).toDouble())
            if(updown.toDouble() > 0){
                quoteTextColor = R.color.quote_red
            }else if(updown.toDouble() < 0){
                quoteTextColor = R.color.quote_green
            }
        }
    }
}

