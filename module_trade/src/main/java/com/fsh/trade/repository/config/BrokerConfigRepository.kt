package com.fsh.trade.repository.config

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fsh.common.repository.BaseRepository
import com.fsh.common.util.CommonUtil
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.room.ConfigDatabase
import javax.security.auth.callback.Callback

class BrokerConfigRepository : BaseRepository {
    private val configDB by lazy {
        Room.databaseBuilder(CommonUtil.application!!, ConfigDatabase::class.java, "db_config.db")
            .addCallback(object:RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Log.e("BrokerConfigRepository","create SQLiteDatabase ${db.version}")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    Log.e("BrokerConfigRepository","onOpen ${db.version}")

                }

                override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                    Log.e("BrokerConfigRepository","onDestructiveMigration ${db.version}")
                }
            })
            .build()
    }

    suspend fun getBrokers(): List<BrokerConfig> {
        return configDB.brokerConfigDao().queryAll()
    }

    suspend fun saveBroker(broker: BrokerConfig) {
        configDB.brokerConfigDao().update(broker)
    }

    suspend fun deleteBroker(broker: BrokerConfig) {
        configDB.brokerConfigDao().delete(broker)
    }
}