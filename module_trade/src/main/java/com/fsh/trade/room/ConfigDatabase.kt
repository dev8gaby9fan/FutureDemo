package com.fsh.trade.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fsh.trade.bean.BrokerConfig

@Database(entities = arrayOf(BrokerConfig::class),version = 1)
abstract class ConfigDatabase : RoomDatabase(){
    abstract fun brokerConfigDao():BrokerConfigDao
}