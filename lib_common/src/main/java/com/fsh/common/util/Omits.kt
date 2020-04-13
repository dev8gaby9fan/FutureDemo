package com.fsh.common.util

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/12
 * description: 定义基本数据类型的初始化常量
 *
 */

object Omits {

    const val OmitString = ""
    val OmitStringArray = arrayOfNulls<String>(0)
    val OmitInt = Integer.MIN_VALUE
    const val OmitDouble = java.lang.Double.MIN_VALUE
    const val OmitLong = java.lang.Long.MIN_VALUE
    const val OmitFloat = java.lang.Float.MIN_VALUE
    val OmitBytes = ByteArray(0)
    const val OmitPrice = "--"//空价格
    const val OmitPrice1 = "-"//天勤没有价格时的空字符串

    fun isOmit(nValue: Int): Boolean {
        return nValue == OmitInt
    }

    fun isOmit(lValue: Long): Boolean {
        return lValue == OmitLong
    }

    fun isOmit(sValue: String?): Boolean {
        return sValue == null || OmitString == sValue || sValue == OmitPrice || sValue == OmitPrice1
    }

    fun isOmit(fValue: Float): Boolean {
        return fValue == OmitFloat
    }

    fun isOmit(dValue: Double?): Boolean {
        return dValue == null || dValue == OmitDouble
    }

    fun isOmit(byValue: ByteArray?): Boolean {
        return byValue == null || OmitBytes == byValue
    }
}