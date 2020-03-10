package com.fsh.common.websocket

import okio.ByteString

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: TODO there need some info to descript current java file
 *
 */

enum class FWebSocketMsgType{
    MSG_TEXT,
    MSG_BYTESTRING,
    MSG_STATUS,
    MSG_ERROR,
}

open class FWebSocketMsg(var msgType:FWebSocketMsgType)

/**
 * 连接状态消息
 */
data class FWebSocketStatusMsg(var status:Int) : FWebSocketMsg(FWebSocketMsgType.MSG_STATUS)

/**
 * 文本消息
 */
data class FWebSocketTextMsg(var resp:String) : FWebSocketMsg(FWebSocketMsgType.MSG_TEXT)

/**
 * 二进制消息
 */
data class FWebSocketByteMsg(var resp:ByteString) : FWebSocketMsg(FWebSocketMsgType.MSG_BYTESTRING)

/**
 * 异常信息
 */
data class FWebSocketErrorMsg(var msg:String,var cause:Exception? = null) : FWebSocketMsg(FWebSocketMsgType.MSG_ERROR)

