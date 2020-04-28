package com.future.quote.model

import androidx.annotation.Keep
import com.fsh.common.util.Omits

class KLineEntity{
    var last_id:Int = Omits.OmitInt
    var trading_day_start_id:Int = Omits.OmitInt
    var trading_day_end_id:Int = Omits.OmitInt
    var data:MutableMap<String,DataEntity> = HashMap()

    var instrumentId:String = Omits.OmitString
    var klineDuration:Long = Omits.OmitLong

    class DataEntity{
        var datetime:Long = 0
        var open:Float = Float.NaN
        var high:Float = Float.NaN
        var low:Float = Float.NaN
        var close:Float = Float.NaN
        var volume:Int = 0
        var open_oi:Int = 0
        var close_oi:Int = 0
    }
}