package com.fsh.common.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SPUtils private constructor(application: Application) {
    private val sp:SharedPreferences
    init {
        sp = application.getSharedPreferences("",Context.MODE_PRIVATE)
    }
    companion object{
        val instance:SPUtils by lazy{
            SPUtils(CommonUtil.application!!)
        }
    }

    fun put(key:String,value:Int,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putInt(key,value).commit()
        }else{
            sp.edit().putInt(key,value).apply()
        }
        return this
    }


    fun put(key:String,value:String,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putString(key,value).commit()
        }else{
            sp.edit().putString(key,value).apply()
        }
        return this
    }

    fun put(key:String,value:Boolean,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putBoolean(key,value).commit()
        }else{
            sp.edit().putBoolean(key,value).apply()
        }
        return this
    }

    fun put(key:String,value:Long,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putLong(key,value).commit()
        }else{
            sp.edit().putLong(key,value).apply()
        }
        return this
    }

    fun put(key:String,value:Float,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putFloat(key,value).commit()
        }else{
            sp.edit().putFloat(key,value).apply()
        }
        return this
    }

    fun put(key:String,value:Set<String>,isCommit:Boolean = true):SPUtils{
        if(isCommit){
            sp.edit().putStringSet(key,value).commit()
        }else{
            sp.edit().putStringSet(key,value).apply()
        }
        return this
    }
    fun getString(key:String,default:String = Omits.OmitString):String{
        return sp.getString(key,default)!!
    }

    fun getInt(key:String,default:Int = Omits.OmitInt):Int{
        return sp.getInt(key,default)
    }

    fun getLong(key:String,default:Long = Omits.OmitLong):Long{
        return sp.getLong(key,default)
    }

    fun getBoolean(key:String,default:Boolean=false):Boolean{
        return sp.getBoolean(key,default)
    }

    fun getStringSet(key:String,default:Set<String> = HashSet<String>(0)):Set<String>{
        return sp.getStringSet(key,default)!!
    }
}