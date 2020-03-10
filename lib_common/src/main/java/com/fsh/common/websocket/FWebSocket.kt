package com.fsh.common.websocket

import android.os.SystemClock
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: WebSocket连接
 *
 */

class FWebSocket : WebSocketListener() {
    private val clientMgr: FWebSocketClientMgr by lazy {
        FWebSocketClientMgr()
    }

    companion object {
        const val CLOSE_MANUAL: Int = 10001
        const val STATUS_CONNECTED = 20000
        const val STATUS_CLOSED = 20001
        const val STATUS_CLOSED_MANUAL = 20002
    }

    private var _dataStream: Subject<FWebSocketMsg> = PublishSubject.create()
    private var connectStatus: AtomicInteger = AtomicInteger(STATUS_CLOSED)
    private lateinit var connectReq: Request
    //之前是否有连接成功过
    private var preIsConnected:Boolean = false
    private var connectJob: Job? = null
    lateinit var okHttpClient: OkHttpClient
    lateinit var serverURL: String
    var reconnectTimeout: Long = 10 * 1000
    var autoReconnect: Boolean = true
    val dataStream = _dataStream

    fun connect() {
        connectJob?.cancel()
        //这里需要等待一下

        connectJob = GlobalScope.launch(Dispatchers.IO) {
            if(preIsConnected){
                GlobalScope.async {
                    SystemClock.sleep(reconnectTimeout)
                }.await()
            }
            try {
                var client = okHttpClient.newWebSocket(connectReq, this@FWebSocket)
                clientMgr.addConnection(client)
            } catch (e: Exception) {
                changeStatus(STATUS_CLOSED)
            }
        }
    }

    private fun changeStatus(status: Int) {
        connectStatus.compareAndSet(connectStatus.get(), status)
        _dataStream.onNext(FWebSocketStatusMsg(status))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        preIsConnected = true
        changeStatus(STATUS_CONNECTED)
        clientMgr.addConnection(webSocket)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        clientMgr.onClientDisconnect(webSocket)
        if (clientMgr.isConnected()) {
            return
        }
        if (code == CLOSE_MANUAL) {
            changeStatus(STATUS_CLOSED_MANUAL)
        } else {
            //这里重连
            changeStatus(STATUS_CLOSED)
            connect()
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        changeStatus(STATUS_CLOSED)
        clientMgr.close()
        connect()
    }

    fun sendMessage(text:String){
        clientMgr.sendMessage(text)
    }
    fun sendMessage(jsonReq:JSONRequest){
        clientMgr.sendMessage(jsonReq)
    }
    fun sendMessage(byteString:ByteString){
        clientMgr.sendMessage(byteString)
    }

    fun close(){
        clientMgr.close()
    }
}