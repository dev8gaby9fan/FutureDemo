package com.future.quote.enums

import com.fsh.common.util.DateUtils

enum class FutureChartType(val title:String,val duration:Long,val chartType: ChartType,val pattern:String){
    L_1MIN("分时",
        FutureChartDuration._1MIN,
        ChartType.Line,
        DateUtils.PATTERN_HHMM
    ),
    K_1DAY("1日",
        FutureChartDuration._1DAY,
        ChartType.Candel,
        DateUtils.PATTERN_MMDD
    ),
    K_1MIN("1分",
        FutureChartDuration._1MIN,
        ChartType.Candel,
        DateUtils.PATTERN_HHMM
    ),
    K_5MIN("5分",
        FutureChartDuration._5MIN,
        ChartType.Candel,
        DateUtils.PATTERN_HHMM
    ),
    K_15MIN("15分",
        FutureChartDuration._15MIN,
        ChartType.Candel,
        DateUtils.PATTERN_HHMM
    ),
    K_30MIN("30分",
        FutureChartDuration._30MIN,
        ChartType.Candel,
        DateUtils.PATTERN_HHMM
    ),
    K_1HOUR("1时",
        FutureChartDuration._1HOUR,
        ChartType.Candel,
        DateUtils.PATTERN_HHMM
    );
    companion object{
        val TYPE_LIST:List<FutureChartType> = values().toList()
    }
}

enum class ChartType{
    Line,
    Candel,
}

object FutureChartDuration{
    const val _1MILLIS = 1000000
    private const val _1SECOND:Long = 1000000000
    const val _1MIN:Long = 60* _1SECOND
    const val _5MIN:Long = 5 * _1MIN
    const val _15MIN:Long = 15 * _1MIN
    const val _30MIN:Long = 30 * _1MIN
    const val _1HOUR:Long = 60 * _1MIN
    const val _1DAY:Long = 24 * _1HOUR
}