package com.future.quote.service

import android.util.Log
import androidx.core.util.Pools
import com.fsh.common.ext.optInt
import com.fsh.common.ext.optString
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.Omits
import com.future.quote.event.BaseEvent.Companion.ACTION_LOAD_INS_OK
import com.future.quote.model.ChartEntity
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.google.gson.*
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

class WebSocketFrameParser(
    private var quoteLieData: Subject<QuoteEntity>,
    private var chartLiveData: Subject<List<KLineEntity>>
) : DataParser<Unit> {
    companion object {
        private const val JSON_KEY_AID = "aid"
        private const val AID_RSP_LOGIN = "rsp_login"
        private const val AID_RETURN_DATA = "rtn_data"
    }

    private val quoteParser: QuoteParser by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        QuoteParser(quoteLieData, chartLiveData)
    }

    override fun parse(json: JsonObject) {
        when (json.optString(JSON_KEY_AID)) {
            AID_RSP_LOGIN -> Log.d("WebSocketFrameParser", "received rsp_login")
            AID_RETURN_DATA -> quoteParser.parse(json)
            else -> Log.d("WebSocketFrameParser", "UnHandle AID ${json.optString(JSON_KEY_AID)}")
        }
    }

}

class QuoteParser(
    private var quoteLieData: Subject<QuoteEntity>,
    private var chartLiveData: Subject<List<KLineEntity>>
) : DataParser<Unit> {
    companion object {
        private const val JSON_DATA = "data"
        private const val JSON_QUOTES = "quotes"
        private const val JSON_KLINES = "klines"
        private const val JSON_CHARTS = "charts"
        private const val JSON_MDHIS_MORE_DATA = "mdhis_more_data"
        const val JSON_CHART_ID = "CHART_ID"
        private const val JSON_STATE = "state"
    }

    private var quoteEntityPools: Pools.Pool<QuoteEntity> = Pools.SimplePool(50)

    override fun parse(json: JsonObject) {
        //这里是拿到data对象里面的数据
        var klineList: List<KLineEntity>? = null
        var jsonArray = json.get(JSON_DATA).asJsonArray
        for (jsonObj in jsonArray) {
            val keySet = jsonObj.asJsonObject.keySet()
            for (key in keySet) {
                val dataJson = jsonObj.asJsonObject.get(key)
                when (key) {
                    JSON_QUOTES -> parseQuoteReturn(dataJson.asJsonObject)
                    JSON_KLINES -> klineList = parseKlineReturn(dataJson.asJsonObject)
                    JSON_CHARTS -> parseChartReturn(dataJson.asJsonObject)
                    JSON_MDHIS_MORE_DATA -> {
                    }
                    else -> Log.d("QuoteParser", "json can't parse $key")
                }
            }
        }
        if (klineList != null) {
            chartLiveData.onNext(klineList)
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

    private fun parseKlineReturn(jsonObj: JsonObject): List<KLineEntity> {
        Log.d("QuoteParser", "parseKlineReturn $jsonObj")
        var returnParams = ArrayList<KLineEntity>()
        for (insId in jsonObj.keySet()) {
            if (Omits.isOmit(insId)) continue
            val instrumentKLines = DiffEntity.getInstrumentKLineEntity(insId)
            //klineDataByDuration里面的key是时间（60000000000），它里面有data对象
            val insElement = jsonObj.get(insId) ?: continue
            val klineDataByDuration = insElement as JsonObject
            for (duration in klineDataByDuration.keySet()) {
                val klineEntityElement = klineDataByDuration.get(duration) ?: continue
                val klineEntityJson = klineEntityElement as JsonObject
                val kLineEntity = instrumentKLines[duration] ?: KLineEntity()
                instrumentKLines[duration] = kLineEntity
                kLineEntity.instrumentId = insId
                kLineEntity.klineDuration = duration.toLong()
                returnParams.add(kLineEntity)
                for (klinePropertyName in klineEntityJson.keySet()) {
                    val property = klineEntityJson.get(klinePropertyName)
                    if (property !is JsonObject) {
                        try {
                            setJsonValueToObjProperty(
                                kLineEntity,
                                property as JsonPrimitive,
                                KLineEntity::class.java.getDeclaredField(klinePropertyName)
                            )
                            Log.d("QuoteParser", "parse kline $duration $klinePropertyName:$property")
                        }catch (e:Throwable) {
                            Log.e("QuoteParser","no field $klinePropertyName found in KLineEntity $klineEntityJson")
                        }


                    } else {
                        if (klinePropertyName == JSON_DATA) {
                            for (dataKey in property.keySet()) {
                                val dataEntity =
                                    kLineEntity.data[dataKey] ?: KLineEntity.DataEntity()
                                kLineEntity.data[dataKey] = dataEntity
                                val dataJsonEle = property.get(dataKey) ?: continue
                                for (dataPropertyName in dataJsonEle.asJsonObject.keySet()) {
                                    val dataProperty =
                                        dataJsonEle.asJsonObject.get(dataPropertyName) ?: continue
//                                        Log.d("QuoteParser","parse kline $duration $klinePropertyName:$dataKey:$dataPropertyName:$dataProperty")
                                    setJsonValueToObjProperty(
                                        dataEntity,
                                        dataProperty as JsonPrimitive,
                                        KLineEntity.DataEntity::class.java.getDeclaredField(
                                            dataPropertyName
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
        return returnParams
    }

    private fun setJsonValueToObjProperty(obj: Any, property: JsonPrimitive, field: Field) {
        field.isAccessible = true
        when (field.type.toString()) {
            "int" -> field.set(obj, property.asInt)
            "float" -> field.set(obj, property.asFloat)
            "double" -> field.set(obj, property.asDouble)
            "long" -> field.set(obj, property.asLong)
            "short" -> field.set(obj, property.asShort)
            "class java.lang.String" -> field.set(obj, property.asString)
            "boolean" -> field.set(obj, property.asBoolean)
        }
    }


    private fun parseChartReturn(jsonObj: JsonObject) {
        Log.d("QuoteParser", "start to parse Chart return $jsonObj")
        val chartIdJson = jsonObj.get(JSON_CHART_ID) ?: return
        val chartEntity = DiffEntity.getChartEntity(JSON_CHART_ID)
        for (key in chartIdJson.asJsonObject.keySet()) {
            if (chartIdJson.asJsonObject.get(key) is JsonNull) return
            if (key == JSON_STATE && chartIdJson.asJsonObject.get(key) != null) {
                val stateJson = chartIdJson.asJsonObject.getAsJsonObject(key)
                for (stateType in stateJson.keySet()) {
                    chartEntity.state[stateType] = stateJson.optString(stateType)
                }
            } else {
                try {
                    setJsonValueToObjProperty(
                        chartEntity,
                        chartIdJson.asJsonObject.get(key) as JsonPrimitive,
                        ChartEntity::class.java.getDeclaredField(key)
                    )
                }catch (e:Throwable) {
                    Log.e("QuoteParser","no field $key found in ChartEntity class ${chartIdJson.asJsonObject}")
                }

            }
        }
    }

}