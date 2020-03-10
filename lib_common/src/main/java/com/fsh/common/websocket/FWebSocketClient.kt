package com.fsh.common.websocket

import okhttp3.WebSocket
import okio.ByteString

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: WebSocket客户端
 *
 */
interface FWebSocketClient {
    fun sendMessage(text:String)

    fun sendMessage(jsonReq:JSONRequest)

    fun sendMessage(byteReq:ByteString)

    fun setConnected(isConnected: Boolean)

    fun isConnected(): Boolean

    fun getClientHashCode(): Int

    fun getWebSocket(): WebSocket

    fun close()
}

class FWebSocketClientImpl(var connection:WebSocket,var flag:Boolean) : FWebSocketClient{
    override fun sendMessage(text: String) {
        checkConnection()
        connection.send(text)
    }

    override fun sendMessage(jsonReq: JSONRequest) {
        sendMessage(jsonReq.toJsonString())
    }

    override fun sendMessage(byteReq: ByteString) {
        checkConnection()
        connection.send(byteReq)
    }

    override fun setConnected(isConnected: Boolean) {
        flag = isConnected
    }

    override fun isConnected(): Boolean = flag

    override fun getClientHashCode(): Int = connection.hashCode()

    override fun getWebSocket(): WebSocket = connection

    override fun close() {
        if(isConnected()){
            connection.close(FWebSocket.CLOSE_MANUAL,"close by manual")
        }
    }

    private fun checkConnection(){
        if(!isConnected()){
            throw WebSocketException("connection has closed")
        }
    }
}

/**
 * JSON请求
 */
interface JSONRequest{

    fun toJsonString():String
}