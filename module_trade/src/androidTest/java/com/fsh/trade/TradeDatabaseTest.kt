package com.fsh.trade

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.room.ConfigDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TradeDatabaseTest {

    private lateinit var database: ConfigDatabase

    @Before
    fun initDatabase(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConfigDatabase::class.java
        ).allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb(){
        database.close()
    }

    @Test
    fun testRoomAdd() = GlobalScope.launch{
        database.brokerConfigDao()
            .update(BrokerConfig("future_demo","00000000",
                "tcp://127.0.0.1:123","9999","future_demo"))
//        val queryAll = database.brokerConfigDao()
//            .queryAll()
//        System.err.println("queryAll size --> ${queryAll.size}")
    }

}