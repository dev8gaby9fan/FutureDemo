package com.future.trade.repository.config

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fsh.common.repository.BaseRepository
import com.fsh.common.util.CommonUtil
import com.future.trade.bean.BrokerConfig
import com.future.trade.room.ConfigDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class BrokerConfigRepository : BaseRepository {
    private val SIMNOW_FRONT_1 = BrokerConfig("上期模拟盘中","simnow_client_test","0000000000000000","tcp://180.168.146.187:10201","9999","futuredemo")
    private val SIMNOW_FRONT_2 = BrokerConfig("上期模拟盘后","simnow_client_test","0000000000000000","tcp://180.168.146.187:10130","9999","futuredemo")
    private val configDB:ConfigDatabase by lazy {
        Room.databaseBuilder(CommonUtil.application!!, ConfigDatabase::class.java, "db_config.db")
            .addCallback(object:RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Log.e("BrokerConfigRepository","create SQLiteDatabase ${db.version}")
                    GlobalScope.async {
                        //这里插入数据
                        configDB.brokerConfigDao().update(SIMNOW_FRONT_1)
                        configDB.brokerConfigDao().update(SIMNOW_FRONT_2)
                        Log.e("BrokerConfigRepository","insert default broker config success")
                    }
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

    fun getBrokers(): LiveData<List<BrokerConfig>> =  configDB.brokerConfigDao().queryAll()

    suspend fun saveBroker(broker: BrokerConfig) {
        configDB.brokerConfigDao().update(broker)
    }

    suspend fun deleteBroker(broker: BrokerConfig) {
        configDB.brokerConfigDao().delete(broker)
    }
}