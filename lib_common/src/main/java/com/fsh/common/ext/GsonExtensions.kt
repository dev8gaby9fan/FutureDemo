package com.fsh.common.ext

import com.fsh.common.util.Omits
import com.google.gson.JsonNull
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
        val obj = get(key)
        if(obj is JsonNull){
            return Omits.OmitString
        }
        return get(key).asString
    }
    return Omits.OmitString
}

inline fun JsonObject.optDouble(key:String):Double{
    if(has(key)){
        val obj = get(key)
        if(obj is JsonNull){
            return Omits.OmitDouble
        }
        return get(key).asDouble
    }
    return Double.NaN
}

inline fun JsonObject.optLong(key:String):Long{
    if(has(key)){
        val obj = get(key)
        if(obj is JsonNull){
            return Omits.OmitLong
        }
        return get(key).asLong
    }
    return Long.MIN_VALUE
}
inline fun JsonObject.optBoolean(key:String):Boolean{
    if(has(key)){
        val obj = get(key)
        if(obj is JsonNull){
            return false
        }
        return get(key).asBoolean
    }
    return false
}
inline fun JsonObject.optFloat(key:String):Float{
    if(has(key)){
        val obj = get(key)
        if(obj is JsonNull){
            return Omits.OmitFloat
        }
        return get(key).asFloat
    }
    return Float.NaN
}

inline fun JsonObject.optInt(key:String):Int{
    if(has(key)){
        val obj = get(key)
        if(obj is JsonNull){
            return Omits.OmitInt
        }
        return get(key).asInt
    }
    return Int.MIN_VALUE
}