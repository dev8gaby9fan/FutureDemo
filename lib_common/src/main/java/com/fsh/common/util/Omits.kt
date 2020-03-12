package com.fsh.common.util

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/12
 * description: TODO there need some info to descript current java file
 *
 */

object Omits {

   const val OmitString = ""
    val OmitStringArray = arrayOfNulls<String>(0)
    val OmitInt = Integer.MIN_VALUE
    val OmitDouble = java.lang.Double.MIN_VALUE
    val OmitLong = java.lang.Long.MIN_VALUE
    val OmitFloat = java.lang.Float.MIN_VALUE
    val OmitBytes = ByteArray(0)
    val OmitPrice = "--"//空价格

    fun isOmit(nValue: Int): Boolean {
        return nValue == OmitInt
    }

    fun isOmit(lValue: Long): Boolean {
        return lValue == OmitLong
    }

    fun isOmit(sValue: String?): Boolean {
        return sValue == null || OmitString == sValue || sValue == OmitPrice
    }

    fun isOmit(fValue: Float): Boolean {
        return fValue == OmitFloat
    }

    fun isOmit(dValue: Double): Boolean {
        return dValue == OmitDouble
    }

    fun isOmit(byValue: ByteArray?): Boolean {
        return byValue == null || OmitBytes == byValue
    }
}