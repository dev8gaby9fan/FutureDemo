package com.future.quote.enums

enum class FutureChartType(val title:String,val duration:Long,val chartType: ChartType){
    L_1MIN("分时",
        FutureChartDuration._1MIN,
        ChartType.Line
    ),
    K_1DAY("1日",
        FutureChartDuration._1DAY,
        ChartType.Candel
    ),
    K_1MIN("1分",
        FutureChartDuration._1MIN,
        ChartType.Candel
    ),
    K_5MIN("5分",
        FutureChartDuration._5MIN,
        ChartType.Candel
    ),
    K_15MIN("15分",
        FutureChartDuration._15MIN,
        ChartType.Candel
    ),
    K_30MIN("30分",
        FutureChartDuration._30MIN,
        ChartType.Candel
    ),
    K_1HOUR("1时",
        FutureChartDuration._1HOUR,
        ChartType.Candel
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
    private const val _1SECOND:Long = 1000000000
    const val _1MIN:Long = 60* _1SECOND
    const val _5MIN:Long = 5 * _1MIN
    const val _15MIN:Long = 15 * _1MIN
    const val _30MIN:Long = 30 * _1MIN
    const val _1HOUR:Long = 60 * _1MIN
    const val _1DAY:Long = 24 * _1HOUR
}