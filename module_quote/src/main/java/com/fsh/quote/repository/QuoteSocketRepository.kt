package com.fsh.quote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.model.QuoteEntity
import com.fsh.common.repository.BaseRepository
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.common.websocket.*
import com.fsh.quote.BuildConfig
import com.fsh.quote.data.PeekMessageFrame
import com.fsh.quote.data.WebSocketTextFrame
import com.fsh.quote.service.DataParser
import com.fsh.quote.service.WebSocketFrameParser
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import okio.ByteString
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 行情服务WebSocket连接
 */

class QuoteSocketRepository : BaseRepository {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var _statusData: MutableLiveData<Int> = MutableLiveData()
    private var _quoteData: Subject<QuoteEntity> = PublishSubject.create<QuoteEntity>()
    private var socketStatus:Int = FWebSocket.STATUS_CLOSED
    private val webSocketFrameParser:DataParser<Unit>  by lazy(mode=LazyThreadSafetyMode.SYNCHRONIZED){
        WebSocketFrameParser(_quoteData)
    }
    val statusData:LiveData<Int> = _statusData
    val quoteData = _quoteData
    private val webSocket: FWebSocket by lazy {
        FWebSocket().apply {
            okHttpClient = RetrofitUtils.okHttpClient
            serverURL = BuildConfig.WS_URL
        }
    }

    init {
        disposables.add(webSocket.dataStream.subscribe {
            when (it.msgType) {
                FWebSocketMsgType.MSG_TEXT -> handleTextMsg((it as FWebSocketTextMsg).resp)
                FWebSocketMsgType.MSG_BYTESTRING -> handleByteMsg((it as FWebSocketByteMsg).resp)
                FWebSocketMsgType.MSG_ERROR -> handleErrorMsg(it as FWebSocketErrorMsg)
                FWebSocketMsgType.MSG_STATUS -> handleStatusMsg(it as FWebSocketStatusMsg)
            }
        })
    }

    fun connectSocket(){
        webSocket.connect()
    }

    private fun handleTextMsg(text: String) {
        Log.e("QuoteSocketRepository","text msg $text")
        webSocketFrameParser.parse(JsonParser().parse(text).asJsonObject)
        sendMessage(PeekMessageFrame())
    }

    private fun handleByteMsg(byteMsg: ByteString) {
        Log.e("QuoteSocketRepository","byte msg ${byteMsg.utf8()}")
    }

    private fun handleErrorMsg(msg: FWebSocketErrorMsg) {
        Log.e("QuoteSocketRepository","error msg ${msg.msg}",msg.cause)
    }

    private fun handleStatusMsg(msg: FWebSocketStatusMsg) {
        Log.e("QuoteSocketRepository","status msg ${msg.status}")
        socketStatus = msg.status
        _statusData.postValue(msg.status)
    }

    fun sendMessage(webSocketFrame:WebSocketTextFrame){
        webSocket.sendMessage(webSocketFrame)
        Log.d("QuoteSocketRepository","send message [${webSocketFrame.toJsonString()}]")
    }

    fun isSocketConnected():Boolean = socketStatus == FWebSocket.STATUS_CONNECTED
}