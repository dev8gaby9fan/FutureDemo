package com.fsh.trade.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: 柜台配置项
 *
 *
 * 1.BrokerConfig
 * appId 穿透式监管 APPID，中继模式为ReplayAppId
 * authCode 穿透式监管认证码
 * frontIp 柜台前置交易地址
 *
 * 2.TradeAccountConfig
 * investorId 投资者交易账号
 * password 交易账号密码
 */

@Parcelize
@Entity(tableName = "table_broker")
data class BrokerConfig(@ColumnInfo var appId:String, @ColumnInfo var authCode:String, @ColumnInfo var frontIp:String, @PrimaryKey @ColumnInfo var brokerId:String,@ColumnInfo var userProductInfo:String) : Parcelable

@Parcelize
data class TradeAccountConfig(var investorId:String,var password:String) : Parcelable