package com.fsh.common.websocket

import okio.IOException

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: WebSocket异常信息
 *
 */
class WebSocketException(var msg:String) :IOException(msg)