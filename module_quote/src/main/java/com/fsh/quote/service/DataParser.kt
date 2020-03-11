package com.fsh.quote.service

import android.util.Log
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
    override fun parse(json: JsonObject) {
        for (instrumentId in json.keySet()) {
            val subObj = json.getAsJsonObject(instrumentId)
            val classN = subObj.get("class").asString
//            if (classN !in supportClassTypes) {
//                Log.w("InstrumentManager", "not supprot class type $classN")
//                continue
//            }
            if ("FUTURE_INDEX" != classN && "FUTURE_CONT" != classN && "FUTURE" != classN) {
                continue
            }
            val expired = subObj.get("expired").asBoolean
            if (expired) {
                continue
            }
            val insName = subObj.get("ins_name").asString
            val exchId = subObj.get("exchange_id").asString
            val simInsId = subObj.get("ins_id").asString
            val productId = subObj.get("product_id").asString
            val volumeMultiple = subObj.get("volume_multiple").asInt
            val priceTick = subObj.get("price_tick").asString
            val priceDecs = subObj.get("price_decs").asString
            val sortKey = subObj.get("sort_key").asString
            val productShortName = subObj.get("product_short_name").asString
            val py = subObj.get("py").asString
            val preVolume = subObj.get("pre_volume").asString
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
            instrument.shortInsId = subObj.get("underlying_symbol").asString
            instrument.deliveryYear = subObj.get("delivery_year").asString
            instrument.deliveryMonth = subObj.get("delivery_month").asString
            instrument.expireTime = subObj.get("expire_datetime").asString
            instrument.maxMarketOrderVolume = subObj.get("max_market_order_volume").asString
            instrument.maxLimitOrderVolume = subObj.get("max_limit_order_volume").asString
            //指数，主力，期货 TODO 需要解析交易时间段
            QuoteInfoMgr.mgr.addInstrument(instrument)
            //组合合约
            if ("FUTURE_COMBINE" == classN) {
//                instrument.leg1_symbol = subObj.optString("leg1_symbol")
//                instrument.leg2_symbol = subObj.optString("leg2_symbol")
//                var exchange = futureCombinedMap[exchId]
//                if (exchange == null) {
//                    exchange = ExchangeInfo(exchId)
//                    futureCombinedMap[exchId] = exchange
//                }
//                exchange.putInstrument(instrument)
            }
            //期权合约
            if ("FUTURE_OPTION" == classN) {
//                instrument.option_class = subObj.optString("option_class")
//                instrument.strike_price = subObj.optString("strike_price")
//                instrument.underlying_multiple = subObj.optString("underlying_multiple")
//                var exchange = futureOptionMap[exchId]
//                if (exchange == null) {
//                    exchange = ExchangeInfo(exchId)
//                    futureOptionMap[exchId] = exchange
//                }
//                exchange.putInstrument(instrument)
            }
        }
    }

}

class QuoteParser : DataParser {
    override fun parse(json: JsonObject) {

    }

}