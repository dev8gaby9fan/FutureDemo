package com.fsh.quote.service

import android.util.Log
import com.fsh.common.model.ExchangeInfo
import com.fsh.common.model.InstrumentInfo
import com.fsh.quote.event.BaseEvent
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
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

    companion object{
        val mgr:QuoteInfoMgr by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            QuoteInfoMgr()
        }
    }

    fun addInstrument(ins:InstrumentInfo){
        var exchange = exchangeMap[ins.eid]
        if(exchange == null){
            exchange = ExchangeInfo(ins.eid,ins.eid)
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

    /**
     * 搜索合约
     */
    fun searchIns(key:String):List<InstrumentInfo>{
        TODO()
    }
}