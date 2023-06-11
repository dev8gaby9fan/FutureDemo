package com.future.trade.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.future.trade.bean.BrokerConfig

@Database(entities = [BrokerConfig::class],version = 1)
abstract class ConfigDatabase : RoomDatabase(){
    abstract fun brokerConfigDao():BrokerConfigDao

}