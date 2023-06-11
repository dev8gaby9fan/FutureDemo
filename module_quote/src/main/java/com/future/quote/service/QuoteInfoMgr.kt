package com.future.quote.service

import com.fsh.common.model.ExchangeInfo
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.model.TradingTime
import com.google.gson.JsonElement
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: Quote服务数据管理类
 *
 */
class QuoteInfoMgr {
    //5个交易所
    private val exchangeMap: ConcurrentHashMap<String, ExchangeInfo> = ConcurrentHashMap(5)
    private val quoteMap: ConcurrentHashMap<String, QuoteEntity> = ConcurrentHashMap(100)

    companion object {
        val mgr: QuoteInfoMgr by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            QuoteInfoMgr()
        }
    }

    fun addInstrument(ins: InstrumentInfo, tradingTime: TradingTime?) {
        var exchange = exchangeMap[ins.exchangeID]
        if (exchange == null) {
            exchange = ExchangeInfo(
                ShinnyHttpInstrumentParser.getExchangeName(ins.exchangeID),
                ins.exchangeID,
                ShinnyHttpInstrumentParser.getExchangeSortKey(ins.exchangeID)
            )
            exchangeMap[ins.exchangeID] = exchange
        }
        exchange.addInstrument(ins, tradingTime)
    }

    fun getInstrument(insId: String, exchangeId: String? = null): InstrumentInfo? {
        if (exchangeId == null) {
            exchangeMap.forEach {
                var ins = it.value.getInstrument(insId)
                if (ins != null) {
                    return ins
                }
            }
            return null
        } else {
            return exchangeMap[exchangeId]?.getInstrument(insId)
        }
    }

    fun getExchange(exchangeId: String): ExchangeInfo {
        return exchangeMap[exchangeId]!!
    }

    /**
     * 搜索合约
     */
    fun searchIns(key: String): List<InstrumentInfo> {
        val result: MutableList<InstrumentInfo> = ArrayList()
        for (entity in exchangeMap) {
            result.addAll(entity.value.searchInstrument(key))
        }
        return result
    }

    fun getExchangeList(): List<ExchangeInfo> {
        val list = ArrayList(exchangeMap.values)
        list.sortWith(Comparator { exc1, exc2 -> exc1.sortKey.compareTo(exc2.sortKey) })
        return list
    }

    fun storeQuote(quoteEntity: QuoteEntity): QuoteEntity {
        var storeQuote = quoteMap[quoteEntity.instrument_id]
        if (storeQuote == null) {
            storeQuote = QuoteEntity(quoteEntity.instrument_id)
            quoteMap[quoteEntity.instrument_id] = storeQuote
        }
        storeQuote.updateQuoteEntity(quoteEntity)
        return storeQuote
    }

    fun getQuoteEntity(insId: String?): QuoteEntity? {
        return quoteMap[insId]
    }
}