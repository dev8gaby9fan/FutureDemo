package com.future.quote.model

import androidx.annotation.Keep
import com.fsh.common.util.Omits

class KLineEntity{
    var last_id:Int = Omits.OmitInt
    var trading_day_start_id:Int = Omits.OmitInt
    var trading_day_end_id:Int = Omits.OmitInt
    var data:MutableMap<String,DataEntity> = HashMap()

    class DataEntity{
        var datetime:Long? = null
        var open:Double? = null
        var high:Double? = null
        var low:Double? = null
        var close:Double? = null
        var volume:Int? = null
        var open_oi:Int? = null
        var close_oi:Int? = null
    }
}