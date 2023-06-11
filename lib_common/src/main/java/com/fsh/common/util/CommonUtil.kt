package com.fsh.common.util

import android.content.res.Resources
import android.graphics.Color
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.fsh.common.base.BaseApplication
import java.io.InputStream

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: TODO there need some info to descript current java file
 *
 */

object CommonUtil {
    lateinit var application:BaseApplication

    fun getAppId():String{
        if(!this::application.isInitialized){
            return "com.fsh.future"
        }
        return application.packageName
    }

    fun getColorRes(@ColorRes resId:Int):Int{
        if(!this::application.isInitialized){
            return Color.BLACK
        }
        return try{
            application.resources.getColor(resId)
        }catch (e: Resources.NotFoundException){
            return Color.BLACK
        }
    }

    fun getStringRes(@StringRes resId: Int):String{
        if(!this::application.isInitialized){
            return Omits.OmitString
        }
        return try{
            application.resources.getString(resId)
        }catch (e: Resources.NotFoundException){
            return Omits.OmitString
        }
    }

    fun getStringArrayRes(@ArrayRes resId: Int):Array<String>{
        if(!this::application.isInitialized){
            return emptyArray()
        }
        return try{
            application.resources.getStringArray(resId)
        }catch (e: Resources.NotFoundException){
            return emptyArray()
        }
    }

    fun openRawResource(@RawRes resId: Int):InputStream {
        return application.resources.openRawResource(resId)
    }
}