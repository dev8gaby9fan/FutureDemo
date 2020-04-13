package com.future.quote.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.ext.optInt
import com.fsh.common.ext.optString
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.Omits
import com.future.quote.event.BaseEvent.Companion.ACTION_LOAD_INS_OK
import com.google.gson.JsonObject
import io.reactivex.subjects.Subject
import java.lang.Exception

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

interface DataParser<T> {

    fun parse(json: JsonObject): T
}

/**
 * 合约信息解析
 */
class InstrumentParser : DataParser<Int> {
    companion object {
        private val supportClassType = arrayOf("FUTURE_CONT", "FUTURE")
        private val supportedExchange = mapOf(
            "CFFEX" to Pair(5,"中金所"),
            "DCE" to Pair(4,"大商所"),
            "SHFE" to Pair(1,"上期所"),
            "INE" to Pair(2,"能源所"),
            "CZCE" to Pair(3,"郑商所"),
            "KQ" to Pair(0,"主力合约")
        )


        fun getExchangeName(id: String): String {
            if (supportedExchange.containsKey(id)) {
                return supportedExchange[id]!!.second
            }
            return "UnKnown"
        }

        fun getExchangeSortKey(id:String):Int{
            if (supportedExchange.containsKey(id)) {
                return supportedExchange[id]!!.first
            }
            return Int.MAX_VALUE
        }
    }

    override fun parse(json: JsonObject): Int {
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
            val sortKey = subObj.optInt("sort_key")
            val productShortName = subObj.optString("product_short_name")
            val py = subObj.optString("py")
            val underlying_symbol = subObj.optString("underlying_symbol")
            //交易所

            var instrument = InstrumentInfo(insName, instrumentId, exchId, productId)
            instrument.classType = classN
            instrument.eid = exchId
            instrument.pid = productId
            instrument.shortInsId = if("FUTURE_CONT" == classN)underlying_symbol else simInsId
            instrument.volumeMultiple = volumeMultiple
            instrument.priceTick = priceTick
            instrument.priceDecs = priceDecs
            instrument.sortkey = sortKey
            instrument.productShortName = productShortName
            instrument.py = py
            instrument.pid = productId
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
        return ACTION_LOAD_INS_OK
    }

}

class WebSocketFrameParser(private var quoteLieData: Subject<QuoteEntity>) : DataParser<Unit> {
    companion object {
        private const val JSON_KEY_AID = "aid"
        private const val AID_RSP_LOGIN = "rsp_login"
        private const val AID_RETURN_DATA = "rtn_data"
    }

    private val quoteParser: QuoteParser by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        QuoteParser(quoteLieData)
    }

    override fun parse(json: JsonObject) {
        when (json.optString(JSON_KEY_AID)) {
            AID_RSP_LOGIN -> Log.d("WebSocketFrameParser", "received rsp_login")
            AID_RETURN_DATA -> quoteParser.parse(json)
            else -> Log.d("WebSocketFrameParser", "UnHandle AID ${json.optString(JSON_KEY_AID)}")
        }
    }

}

class QuoteParser(private var quoteLieData: Subject<QuoteEntity>) : DataParser<Unit> {
    companion object {
        private const val JSON_DATA = "data"
        private const val JSON_QUOTES = "quotes"
    }

    override fun parse(json: JsonObject) {
        var jsonArray = json.get(JSON_DATA).asJsonArray
        for (jsonObj in jsonArray) {
            val keySet = jsonObj.asJsonObject.keySet()
            for (key in keySet) {
                val dataJson = jsonObj.asJsonObject.get(key)
                when (key) {
                    JSON_QUOTES -> parseQuoteReturn(dataJson.asJsonObject)
                    //TODO 这里未来还需要K线解析
                }
            }
        }
    }

    private fun parseQuoteReturn(jsonObj: JsonObject) {
        for (insId in jsonObj.keySet()) {
            if(!jsonObj.get(insId).isJsonObject){
                Log.d("QuoteParser","parseQuoteReturn ${jsonObj.get(insId)}")
                continue
            }
            val quoteDataObj = jsonObj.get(insId).asJsonObject
            val quoteEntity = QuoteEntity(insId)
            for (property in quoteDataObj.keySet()) {
                val field = QuoteEntity::class.java.getDeclaredField(property)
                field.isAccessible = true
                field.set(quoteEntity, quoteDataObj.optString(property))
            }
            //TODO 这里QuoteInfoMgr storeQuote
            QuoteInfoMgr.mgr.storeQuote(quoteEntity)
            quoteLieData.onNext(quoteEntity)
        }
    }

    private fun parseChartReturn(jsonObj: JsonObject) {

    }

}