package com.future.quote.service

import android.util.Log
import androidx.core.util.Pools
import com.fsh.common.ext.optInt
import com.fsh.common.ext.optString
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.Omits
import com.future.quote.event.BaseEvent.Companion.ACTION_LOAD_INS_OK
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.subjects.Subject
import java.lang.Exception
import java.lang.reflect.Field

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 数据解析
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
            "CFFEX" to Pair(5, "中金所"),
            "DCE" to Pair(4, "大商所"),
            "SHFE" to Pair(1, "上期所"),
            "INE" to Pair(2, "能源所"),
            "CZCE" to Pair(3, "郑商所"),
            "KQ" to Pair(0, "主力合约")
        )


        fun getExchangeName(id: String): String {
            if (supportedExchange.containsKey(id)) {
                return supportedExchange[id]!!.second
            }
            return "UnKnown"
        }

        fun getExchangeSortKey(id: String): Int {
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

            when (classN) {
                //期货普通合约格式
                "FUTURE" -> {
                    instrument.isMainIns = false
                    instrument.ctpExchangeId = exchId
                    instrument.ctpInstrumentId = subObj.optString("ins_id")
                    //快期主力合约格式：KQ.m@CFFEX.IF,这里代表这个品种的主力合约ID
                    instrument.mainInsId = "KQ.m@$exchId.$productId"
                }
                //主力合约
                "FUTURE_CONT" -> {
                    instrument.isMainIns = true
                    //快期主力合约的underlying_symbol格式为: CFFEX.IF2001
                    val splitSymbole = underlying_symbol.split(Regex("\\."))
                    instrument.ctpExchangeId = splitSymbole[0]
                    instrument.ctpInstrumentId = splitSymbole[1]
                    //主力合约对应的真实合约
                    instrument.mainInsId = underlying_symbol
                }
            }

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
        private const val JSON_KLINES = "klines"
        private const val JSON_CHARTS = "charts"
    }

    private var quoteEntityPools: Pools.Pool<QuoteEntity> = Pools.SimplePool(50)

    override fun parse(json: JsonObject) {
        //这里是拿到data对象里面的数据
        var jsonArray = json.get(JSON_DATA).asJsonArray
        for (jsonObj in jsonArray) {
            val keySet = jsonObj.asJsonObject.keySet()
            for (key in keySet) {
                val dataJson = jsonObj.asJsonObject.get(key)
                when (key) {
                    JSON_QUOTES -> parseQuoteReturn(dataJson.asJsonObject)
                    JSON_KLINES -> parseKlineReturn(dataJson.asJsonObject)
                    JSON_CHARTS -> parseChartReturn(dataJson.asJsonObject)
                    else -> Log.d("QuoteParser", "json can't parse $key")
                    //TODO 这里未来还需要K线解析
                }
            }
        }
    }

    private fun parseQuoteReturn(jsonObj: JsonObject) {
        for (insId in jsonObj.keySet()) {
            if (!jsonObj.get(insId).isJsonObject) {
                Log.d("QuoteParser", "parseQuoteReturn ${jsonObj.get(insId)}")
                continue
            }
            val quoteDataObj = jsonObj.get(insId).asJsonObject
            val quoteEntity = obtainQuoteEntity(insId)
            quoteEntity.instrument_id = insId
            for (property in quoteDataObj.keySet()) {
                val field = QuoteEntity::class.java.getDeclaredField(property)
                field.isAccessible = true
                field.set(quoteEntity, quoteDataObj.optString(property))
            }
            val storeQuote = QuoteInfoMgr.mgr.storeQuote(quoteEntity)
//            quoteEntityPools.release(quoteEntity)
            quoteLieData.onNext(storeQuote)
        }
    }

    private fun obtainQuoteEntity(insId: String): QuoteEntity {
        return /*quoteEntityPools.acquire() ?: */QuoteEntity(insId)
    }

    private fun parseKlineReturn(jsonObj: JsonObject) {
        Log.d("QuoteParser", "start to parse kline data $jsonObj")
        for (insId in jsonObj.keySet()) {
            if (Omits.isOmit(insId)) continue
            val instrumentKLines = DiffEntity.getInstrumentKLineEntity(insId)
            //klineDataByDuration里面的key是时间（60000000000），它里面有data对象
            val insElement = jsonObj.remove(insId) ?: continue
            val klineDataByDuration = insElement as JsonObject
            for (duration in klineDataByDuration.keySet()) {
                val klineEntityElement = klineDataByDuration.get(duration) ?: continue
                val klineEntityJson = klineEntityElement as JsonObject
                val kLineEntity = instrumentKLines[duration] ?: KLineEntity()
                instrumentKLines[duration] = kLineEntity
                Log.d("QuoteParser", "start to parse kline duration $duration")
                for (klinePropertyName in klineEntityJson.keySet()) {
                    try {
                        val property = klineEntityJson.get(klinePropertyName)
                        if (property !is JsonObject) {
//                            val field =
//                                KLineEntity::class.java.getDeclaredField(klinePropertyName)
//                            field.isAccessible = true
//                            field.set(kLineEntity, property)
//                            klineEntityJson.optInt()
                            setJsonValueToObjProperty(kLineEntity,property as JsonPrimitive,KLineEntity::class.java.getDeclaredField(klinePropertyName))
                        } else {
                            if (klinePropertyName == JSON_DATA) {
                                Log.d("QuoteParser","start to parse kline data ")
                                for (dataKey in property.keySet()) {
                                    val dataEntity =
                                        kLineEntity.data[dataKey] ?: KLineEntity.DataEntity()
                                    kLineEntity.data[dataKey] = dataEntity
                                    val dataJsonEle = property.get(dataKey) ?: continue
                                    for (dataPropertyName in dataJsonEle.asJsonObject.keySet()) {
                                        val dataProperty = property.get(dataPropertyName) ?: continue
                                        setJsonValueToObjProperty(dataEntity,dataProperty as JsonPrimitive,KLineEntity.DataEntity::class.java.getDeclaredField(dataPropertyName))
                                    }
                                }
                                Log.d("QuoteParser","parse kline data end DataEntity size ${kLineEntity.data.size}")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.d(
                        "QuoteParser",
                        "end to parse kline duration $duration ${instrumentKLines.size}"
                    )
                }
            }

            Log.d("QuoteParser", "parse instrument kline over $insId ${instrumentKLines.size}")
        }
        Log.d("QuoteParser", "parse kline data end $jsonObj")
    }

    private fun setJsonValueToObjProperty(obj:Any, property: JsonPrimitive, field:Field){
        field.isAccessible = true
        if(property.isString){
            field.set(obj,property.asString)
        }else if(property.isNumber){
            val numbProperty = property.asNumber
            if(numbProperty is Int){
                field.set(obj,property.asInt)
            }else if(numbProperty is Double){
                field.set(obj,property.asDouble)
            }else if(numbProperty is Long){
                field.set(obj,property.asLong)
            }else if(numbProperty is Float){
                field.set(obj,property.asFloat)
            }else if(numbProperty is Byte){
                field.set(obj,numbProperty.toByte())
            }
        }else if(property.isBoolean){
            field.set(obj,property.asBoolean)
        }
    }


    private fun parseChartReturn(jsonObj: JsonObject) {
        Log.d("QuoteParser","parseChartReturn $jsonObj")
    }

}