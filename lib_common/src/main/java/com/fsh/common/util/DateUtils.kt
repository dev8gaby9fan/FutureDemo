package com.fsh.common.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val PATTERN_YYYYMMDD = "yyyyMMdd"
    const val PATTERN_YYYYMMDD_HHMMSS = "yyyyMMdd HH:mm:ss"
    const val PATTERN_HHMMSS = "HH:mm:ss"
    const val PATTERN_HHMM = "HH:mm"
    const val PATTERN_MMDD = "MM/dd"
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
    fun formatNow2():String = formatDate(PATTERN_YYYYMMDD,Date())
    fun formatNow3():String = formatDate(PATTERN_HHMMSS,Date())

    fun parseDate1(time:String):Date? = parseDate(PATTERN_YYYYMMDD_HHMMSS,time)
    fun parseDate2(time:String):Date? = parseDate(PATTERN_YYYYMMDD,time)
    fun parseDate3(time:String):Date? = parseDate(PATTERN_HHMMSS,time)


}