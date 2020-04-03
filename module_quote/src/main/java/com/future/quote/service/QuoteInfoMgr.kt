package com.future.quote.service

import android.util.Log
import com.fsh.common.model.ExchangeInfo
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.future.quote.event.BaseEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import okhttp3.internal.connection.Exchange
import org.json.JSONObject
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
    private var exchangeMap:ConcurrentHashMap<String,ExchangeInfo> = ConcurrentHashMap(5)
    private var quoteMap:ConcurrentHashMap<String,QuoteEntity> = ConcurrentHashMap(100)
    companion object{
        val mgr:QuoteInfoMgr by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            QuoteInfoMgr()
        }
    }

    fun addInstrument(ins:InstrumentInfo){
        var exchange = exchangeMap[ins.eid]
        if(exchange == null){
            exchange = ExchangeInfo(InstrumentParser.getExchangeName(ins.eid),ins.eid,InstrumentParser.getExchangeSortKey(ins.eid))
            exchangeMap[ins.eid] = exchange
        }
        exchange.addInstrument(ins)
    }

    fun getInstrument(insId:String,exchangeId:String? = null):InstrumentInfo?{
        if(exchangeId == null){
            exchangeMap.forEach {
                var ins = it.value.getInstrument(insId)
                if(ins != null){
                    return ins
                }
            }
            return null
        }else{
            return exchangeMap[exchangeId]?.getInstrument(insId)
        }
    }

    fun getExchange(exchangeId:String):ExchangeInfo{
        return exchangeMap[exchangeId]!!
    }

    /**
     * 搜索合约
     */
    fun searchIns(key:String):List<InstrumentInfo>{
        TODO()
    }

    fun getExchangeList():List<ExchangeInfo>{
        val list = ArrayList(exchangeMap.values)
        list.sortWith(Comparator { exc1, exc2 -> exc1.sortKey.compareTo(exc2.sortKey) })
        return list
    }

    fun storeQuote(quoteEntity:QuoteEntity){
        var storeQuote = quoteMap[quoteEntity.instrument_id]
        if(storeQuote == null){
            storeQuote = QuoteEntity(quoteEntity.instrument_id)
            quoteMap[quoteEntity.instrument_id] = storeQuote
        }
        storeQuote.updateQuoteEntity(quoteEntity)
    }

    fun getQuoteEntity(insId:String?):QuoteEntity?{
        return quoteMap[insId]
    }
}