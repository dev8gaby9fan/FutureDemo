package com.fsh.trade.room

import androidx.room.*
import com.fsh.trade.bean.BrokerConfig

@Dao
interface BrokerConfigDao{


    @Query("select * from table_broker")
    suspend fun queryAll():List<BrokerConfig>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun update(config:BrokerConfig)

    @Delete
    suspend fun delete(config:BrokerConfig)
}