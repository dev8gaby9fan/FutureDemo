package com.fsh.common.ext

import com.google.gson.JsonObject

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

inline fun JsonObject.optString(key:String):String{
    if(has(key)){
        return get(key).asString
    }
    return ""
}

inline fun JsonObject.optDouble(key:String):Double{
    if(has(key)){
        return get(key).asDouble
    }
    return Double.NaN
}

inline fun JsonObject.optLong(key:String):Long{
    if(has(key)){
        return get(key).asLong
    }
    return Long.MIN_VALUE
}
inline fun JsonObject.optBoolean(key:String):Boolean{
    if(has(key)){
        return get(key).asBoolean
    }
    return false
}
inline fun JsonObject.optFloat(key:String):Float{
    if(has(key)){
        return get(key).asFloat
    }
    return Float.NaN
}

inline fun JsonObject.optInt(key:String):Int{
    if(has(key)){
        return get(key).asInt
    }
    return Int.MIN_VALUE
}