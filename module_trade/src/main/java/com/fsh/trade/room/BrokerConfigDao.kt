package com.fsh.trade.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fsh.trade.bean.BrokerConfig

@Dao
interface BrokerConfigDao{


    @Query("select * from table_broker")
    fun queryAll():LiveData<List<BrokerConfig>>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun update(config:BrokerConfig)

    @Delete
    suspend fun delete(config:BrokerConfig)
}