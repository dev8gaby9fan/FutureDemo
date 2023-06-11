package com.future.quote.model

import com.fsh.common.util.Omits

class ChartEntity {
    var state:MutableMap<String,String> = HashMap()
    var left_id:Int = -1
    var right_id:Int = -1
    var more_data:Boolean = false
    var ready:Int = Omits.OmitInt
}