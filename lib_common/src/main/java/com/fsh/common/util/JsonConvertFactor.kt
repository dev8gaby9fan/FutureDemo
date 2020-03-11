package com.fsh.common.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

class JsonConvertFactor : Converter.Factory(){
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val gson = Gson()
        val jsonAdapter = gson.getAdapter(TypeToken.get(JsonObject::class.java))
        return JsonConvert(gson,jsonAdapter)
    }
}

class JsonConvert(var gson:Gson,var adapter:TypeAdapter<JsonObject>) : Converter<ResponseBody,JsonObject>{
    override fun convert(value: ResponseBody): JsonObject? {
        val respStr = value.string()
        try{
            return JsonParser().parse(respStr).asJsonObject
        }catch (e:Exception){

        }
        return null
    }

}