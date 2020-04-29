package com.future.quote.model

class ChartEntity {
    val state:MutableMap<String,String> = HashMap()
    val left_id:Int = -1
    val right_id:Int = -1
    var more_data:Boolean = false
}