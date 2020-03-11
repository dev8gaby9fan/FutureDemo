package com.fsh.common.websocket

import okhttp3.WebSocket
import okio.ByteString
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 连接实例管理类
 *
 */

class FWebSocketClientMgr{
    private var connectionMap = ConcurrentHashMap<Int,FWebSocketClient>(10)
    //最新的连接实例
    private var lastClient:FWebSocketClient? = null
    fun addConnection(webSocket:WebSocket){
        var client = connectionMap[webSocket.hashCode()]
        client = client?: FWebSocketClientImpl(webSocket,true)
        connectionMap[client.getClientHashCode()] = client
        lastClient?.close()
        lastClient = client
    }

    fun onClientDisconnect(webSocket: WebSocket){
        var client = connectionMap.remove(webSocket.hashCode())
        if(lastClient?.getClientHashCode() == client?.getClientHashCode()){
            lastClient?.setConnected(false)
            lastClient = null
        }
    }

    fun isConnected():Boolean = lastClient?.isConnected() ?: false

    fun getClient():FWebSocketClient? = lastClient

    fun sendMessage(jsonReq:JSONRequest){
        if(lastClient == null){
            throw WebSocketException("can't send jsonReq,because connection has closed")
        }
        lastClient?.sendMessage(jsonReq)
    }

    fun sendMessage(text:String){
        if(lastClient == null){
            throw WebSocketException("can't send text,because connection has closed")
        }
        lastClient?.sendMessage(text)
    }

    fun sendMessage(byteString:ByteString){
        if(lastClient == null){
            throw WebSocketException("can't send byteString,because connection has closed")
        }
        lastClient?.sendMessage(byteString)
    }

    fun close(){
        val client = connectionMap.remove(lastClient?.getClientHashCode())
        client?.close()
        connectionMap.forEach {
            if(it.value.isConnected()){
                it.value.close()
            }
        }
        connectionMap.clear()
    }
}