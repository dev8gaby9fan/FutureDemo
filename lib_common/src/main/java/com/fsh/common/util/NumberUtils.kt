package com.fsh.common.util

import java.math.BigDecimal

object NumberUtils {

    fun formatNum(num:Double?, pattern: Double?):String{
        if(num == null){
            return Omits.OmitString
        }
        if(pattern == null){
            return num.toString()
        }
        val patternStr = pattern.toString()
        if(!patternStr.matches(Regex.fromLiteral("-?\\d+(\\.\\d+)?"))){
            return String.format("%d",num)
        }
        val digitCount = patternStr.split(Regex.fromLiteral("\\."))[1].length
        return String.format("%${digitCount}f", num)
    }

    fun formatNum(num:String?, pattern:String?):String{
        return formatNum(BigDecimal(num).toDouble(),BigDecimal(pattern).toDouble())
    }
}