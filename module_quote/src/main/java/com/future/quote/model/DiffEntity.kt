package com.future.quote.model

import java.util.concurrent.ConcurrentHashMap

object DiffEntity {
    val klines:ConcurrentHashMap<String,ConcurrentHashMap<String,KLineEntity>> = ConcurrentHashMap(20)
    val charts:ConcurrentHashMap<String,ChartEntity> = ConcurrentHashMap(200)

    fun getInstrumentKLineEntity(instrumentId:String):ConcurrentHashMap<String,KLineEntity>{
        val map = klines[instrumentId] ?: ConcurrentHashMap()
        if(!klines.containsKey(instrumentId)){
            klines[instrumentId] = map
        }
        return map
    }

    fun clearInstrumentKLineEntity(instrumentId: String){
        val remove = klines.remove(instrumentId)
        remove?.clear()
    }

    fun getChartEntity(key: String):ChartEntity{
        val entity = charts[key] ?: ChartEntity()
        if(!charts.containsKey(key)){
            charts[key] = entity
        }
        return entity
    }
}