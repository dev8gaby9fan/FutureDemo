package com.fsh.common.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val PATTERN_YYYYMMDD = "yyyyMMdd"
    const val PATTERN_YYYYMMDD_HHMMSS = "yyyyMMdd HH:mm:ss"
    fun formatDate(pattern:String,date: Date):String =
        SimpleDateFormat(pattern).format(date)

    fun parseDate(pattern: String,time:String):Date?{
        return try{
            SimpleDateFormat(pattern).parse(time)
        }catch (e:Exception){
            null
        }
    }

    fun formatNow1():String = formatDate(PATTERN_YYYYMMDD_HHMMSS,Date())


}