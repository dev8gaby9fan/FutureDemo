package com.future.quote.data

import com.fsh.common.websocket.JSONRequest
import com.google.gson.Gson

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/12
 * description: WebSocket数据包
 *
 */

abstract class WebSocketTextFrame(var aid:String) : JSONRequest{
    companion object{
        const val AID_SUB_QUOTE = "subscribe_quote"
        const val AID_PEEK = "peek_message"
        const val SET_CHART = "set_chart"
        const val CHART_ID = "CHART_ID"
    }
    override fun toJsonString(): String = Gson().toJson(this)
}

/**
 * 订阅行情请求
 */
data class SubscribeQuoteFrame(var ins_list:String) : WebSocketTextFrame(AID_SUB_QUOTE)

/**
 * PeekMessage请求
 */
class PeekMessageFrame : WebSocketTextFrame(AID_PEEK)

/**
 * K线数据请求
 */
class SetChartFrame(val ins_list:String,val duration:Long,val trading_day_start:Long?=null,val trading_day_count:Long?=null,val view_width:Int?=null,val chart_id:String = CHART_ID) : WebSocketTextFrame(SET_CHART)