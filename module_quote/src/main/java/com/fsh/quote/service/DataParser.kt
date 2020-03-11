package com.fsh.quote.service

import android.util.Log
import com.fsh.common.ext.optInt
import com.fsh.common.ext.optString
import com.fsh.common.model.InstrumentInfo
import com.google.gson.JsonObject

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

interface DataParser {

    fun parse(json: JsonObject)
}

/**
 * 合约信息解析
 */
class InstrumentParser : DataParser {
    companion object{
        private val supportClassType = arrayOf("FUTURE_CONT","FUTURE")
        private val supportedExchange = mapOf("CFFEX" to "中金所","DCE" to "大商所","SHFE" to "上期所","INE" to "能源所","CZCE" to "郑商所","KQ" to "主力合约")
        fun getExchangeName(id:String):String{
            if(supportedExchange.containsKey(id)){
                return supportedExchange[id]!!
            }
            return "UnKnown"
        }
    }
    override fun parse(json: JsonObject) {
        for (instrumentId in json.keySet()) {
            val subObj = json.getAsJsonObject(instrumentId)
            val classN = subObj.optString("class")
            if (classN !in supportClassType) {
                continue
            }
            val expired = subObj.get("expired").asBoolean
            if (expired) {
                continue
            }
            val insName = subObj.optString("ins_name")
            val exchId = subObj.optString("exchange_id")
            val simInsId = subObj.optString("ins_id")
            val productId = subObj.optString("product_id")
            val volumeMultiple = subObj.optInt("volume_multiple")
            val priceTick = subObj.optString("price_tick")
            val priceDecs = subObj.optString("price_decs")
            val sortKey = subObj.optString("sort_key")
            val productShortName = subObj.optString("product_short_name")
            val py = subObj.optString("py")
            val preVolume = subObj.optString("pre_volume")
            //交易所

            var instrument = InstrumentInfo(insName,simInsId,exchId,productId)
            instrument.classType = classN
            instrument.eid = exchId
            instrument.pid = productId
            instrument.shortInsId = simInsId
            instrument.volumeMultiple = volumeMultiple
            instrument.priceTick = priceTick
            instrument.priceDecs = priceDecs
            instrument.sortkey = sortKey
            instrument.productShortName = productShortName
            instrument.py = py
            instrument.pid = productId
            instrument.shortInsId = subObj.optString("underlying_symbol")
            instrument.deliveryYear = subObj.optString("delivery_year")
            instrument.deliveryMonth = subObj.optString("delivery_month")
            instrument.expireTime = subObj.optString("expire_datetime")
            instrument.maxMarketOrderVolume = subObj.optString("max_market_order_volume")
            instrument.maxLimitOrderVolume = subObj.optString("max_limit_order_volume")
            //指数，主力，期货
            QuoteInfoMgr.mgr.addInstrument(instrument)
            //组合合约
//            if ("FUTURE_COMBINE" == classN) {
//                instrument.leg1_symbol = subObj.optString("leg1_symbol")
//                instrument.leg2_symbol = subObj.optString("leg2_symbol")
//                var exchange = futureCombinedMap[exchId]
//                if (exchange == null) {
//                    exchange = ExchangeInfo(exchId)
//                    futureCombinedMap[exchId] = exchange
//                }
//                exchange.putInstrument(instrument)
//            }
            //期权合约
//            if ("FUTURE_OPTION" == classN) {
//                instrument.option_class = subObj.optString("option_class")
//                instrument.strike_price = subObj.optString("strike_price")
//                instrument.underlying_multiple = subObj.optString("underlying_multiple")
//                var exchange = futureOptionMap[exchId]
//                if (exchange == null) {
//                    exchange = ExchangeInfo(exchId)
//                    futureOptionMap[exchId] = exchange
//                }
//                exchange.putInstrument(instrument)
//            }
        }
    }

}

class QuoteParser : DataParser {
    override fun parse(json: JsonObject) {

    }

}