package com.future.quote.service

import android.util.Log
import com.fsh.common.ext.optString
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.TradingTime
import com.google.gson.stream.JsonReader

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2023/6/9
 * description: 快期HTTP合约数据解析
 *
 */
class ShinnyHttpInstrumentParser {
    companion object {
        private const val TAG = "HttpInstrumentParser"
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
                return supportedExchange[id]?.second ?: ""
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

    /**
     * 快期合约数据太大了，不能直接解析，需要使用JsonReader解析，不然会OOM
     */
    fun doParse(jsonReader: JsonReader) {
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            val instrumentKey = jsonReader.nextName()
            jsonReader.beginObject()
            var skip = false
            var instrument: InstrumentInfo? = null
            var tradingTime: TradingTime? = null
            while (jsonReader.hasNext()) {
                //跳过解析
                if (skip) {
                    while (jsonReader.hasNext()) {
                        jsonReader.skipValue()
                    }
                    break
                }
                val nextName = jsonReader.nextName()
                when (nextName) {
                    "class" -> {
                        val classValue = jsonReader.nextString()
                        skip = classValue !in supportClassType
                        instrument = InstrumentInfo(classValue)
                        instrument.instrumentID = instrumentKey
                        tradingTime = null
                    }

                    "commission" -> {
                        val commissionValue = jsonReader.nextDouble()
                        instrument?.commission = commissionValue.toString()
                    }

                    "exchange_id" -> {
                        val exchange = jsonReader.nextString()
                        instrument?.exchangeID = exchange
                    }

                    "expired" -> {
                        val expired = jsonReader.nextBoolean()
                        skip = expired
                    }

                    "ins_id" -> {
                        val ins_id = jsonReader.nextString()
                        instrument?.insId = ins_id
                    }

                    "ins_name" -> {
                        val ins_name = jsonReader.nextString()
                        instrument?.instrumentName = ins_name
                    }

                    "instrument_id" -> {
                        val instrument_id = jsonReader.nextString()
                        instrument?.instrumentID = instrument_id
                    }

                    "margin" -> {
                        val margin = jsonReader.nextDouble()
                        instrument?.margin = margin.toString()
                    }

                    "price_decs" -> {
                        val price_decs = jsonReader.nextInt()
                        instrument?.priceDecs = price_decs.toString()
                    }

                    "price_tick" -> {
                        val price_tick = jsonReader.nextDouble()
                        instrument?.priceTick = price_tick.toString()
                    }

                    "py" -> {
                        val py = jsonReader.nextString()
                        instrument?.py = py
                    }

                    "sort_key" -> {
                        val sort_key = jsonReader.nextInt()
                        instrument?.sortkey = sort_key
                    }

                    "trading_time" -> {
                        tradingTime = TradingTime(emptyList(), emptyList())
                        jsonReader.beginObject()
                        while (jsonReader.hasNext()) {
                            val nameT1 = jsonReader.nextName()
                            jsonReader.beginArray()
                            val times = mutableListOf<List<String>>()
                            if ("day" != nameT1) {
                                tradingTime.night = times
                            } else {
                                tradingTime.day = times
                            }
                            while (jsonReader.hasNext()) {
                                jsonReader.beginArray()
                                val time = mutableListOf<String>()
                                while (jsonReader.hasNext()) {
                                    val t = jsonReader.nextString()
                                    time.add(t)
                                }
                                jsonReader.endArray()
                                times.add(time)
                            }
                            jsonReader.endArray()
                        }
                        jsonReader.endObject()

                    }

                    "underlying_product" -> {
                        val underlying_product = jsonReader.nextString()
                        print("$nextName $underlying_product")
                    }

                    "volume_multiple" -> {
                        val volume_multiple = jsonReader.nextInt()
                        instrument?.volumeMultiple = volume_multiple
                    }

                    "product_short_name" -> {
                        val productShortName = jsonReader.nextString()
                        instrument?.productShortName = productShortName
                    }

                    "underlying_symbol" -> {
                        val underlyingSymbol = jsonReader.nextString()
                        instrument?.underlyingSymbol = underlyingSymbol
                    }

                    "product_id" -> {
                        val productId = jsonReader.nextString()
                        instrument?.productID = productId
                    }

                    "max_market_order_volume" -> {
                        val maxMarketOrderVolume = jsonReader.nextString()
                        instrument?.maxMarketOrderVolume = maxMarketOrderVolume
                    }

                    "max_limit_order_volume" -> {
                        val maxLimitOrderVolume = jsonReader.nextString()
                        instrument?.maxLimitOrderVolume = maxLimitOrderVolume
                    }

                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
            if (skip) {
                instrument = null
                tradingTime = null
                continue
            }
            when (instrument?.classType) {
                "FUTURE" -> {
                    instrument.isMainIns = false
                    instrument.ctpExchangeId = instrument.exchangeID
                    instrument.ctpInstrumentId = instrument.insId
                    //快期主力合约格式：KQ.m@CFFEX.IF,这里代表这个品种的主力合约ID
                    instrument.mainInsId = "KQ.m@${instrument.exchangeID}.${instrument.productID}"
                    QuoteInfoMgr.mgr.addInstrument(instrument, tradingTime)
                }

                "FUTURE_CONT" -> {
                    instrument.isMainIns = true
                    val instrumentIdSplit = instrument.instrumentID.split(".")
                    instrument.productID = instrumentIdSplit.last()
                    //快期主力合约的underlying_symbol格式为: CFFEX.IF2001
                    val splitSymbole = instrument.underlyingSymbol?.split(Regex("\\.")) ?: emptyList()
                    instrument.ctpExchangeId = if (splitSymbole.isEmpty()) "" else splitSymbole[0]
                    instrument.ctpInstrumentId = if (splitSymbole.isEmpty()) "" else splitSymbole[1]
                    //主力合约对应的真实合约
                    instrument.mainInsId = instrument.underlyingSymbol ?: ""
                    QuoteInfoMgr.mgr.addInstrument(instrument, tradingTime)
                }
            }
        }
        jsonReader.endObject()
    }
}