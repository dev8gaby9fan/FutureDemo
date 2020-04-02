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
        if(!patternStr.matches(Regex.fromLiteral("([1-9]\\d*\\.\\d*)|(0\\.\\d*[1-9]\\d*)"))){
            return String.format("%d",num.toInt())
        }
        val digitCount = patternStr.split(Regex.fromLiteral("\\."))[1].length
        return String.format("%${digitCount}f", num)
    }

    fun formatNum(num:String?, pattern:String?):String{
        return formatNum(BigDecimal(num).toDouble(),BigDecimal(pattern).toDouble())
    }

    fun add(num1:Double,num2:Double):String{
        return add(num1.toString(),num2.toString())
    }
    fun add(num1:String,num2:String):String{
        val m1 = BigDecimal(num1)
        val m2 = BigDecimal(num2)
        return m1.add(m2).toString()
    }
}