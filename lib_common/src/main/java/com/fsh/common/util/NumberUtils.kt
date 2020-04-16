package com.fsh.common.util

import java.math.BigDecimal
import java.util.regex.Pattern

object NumberUtils {

    fun formatNum(num: Double?, pattern: Double?): String {
        if (num == null) {
            return Omits.OmitString
        }
        if (pattern == null) {
            return num.toString()
        }
        return formatNum(num.toString(), pattern.toString())
    }

    fun formatNum(num: String?, pattern: String?): String {
        if (Omits.isOmit(num)) {
            return Omits.OmitPrice
        }
        if (Omits.isOmit(pattern)) {
            return num ?: Omits.OmitPrice
        }
        val patternStr = pattern.toString()
        if (!Pattern.matches("([1-9]\\d*\\.\\d*)|(0\\.\\d*[1-9]\\d*)|(0\\.\\d*)", patternStr)) {
            return BigDecimal(num).toLong().toString()
        }
        val digitCount = patternStr.split(Regex("\\."))[1].length
        return String.format("%.${digitCount}f", num!!.toDouble())
    }

    fun add(num1: Double, num2: Double): String {
        return add(num1.toString(), num2.toString())
    }

    fun add(num1: String, num2: String): String {
        val m1 = BigDecimal(num1)
        val m2 = BigDecimal(num2)
        return m1.add(m2).toString()
    }
}