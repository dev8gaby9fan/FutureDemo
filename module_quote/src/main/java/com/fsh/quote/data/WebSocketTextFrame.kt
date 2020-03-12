package com.fsh.quote.data

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
class PeekMessageFrame() : WebSocketTextFrame(AID_PEEK)