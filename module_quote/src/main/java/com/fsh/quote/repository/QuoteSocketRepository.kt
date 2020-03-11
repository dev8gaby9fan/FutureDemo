package com.fsh.quote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.model.QuoteEntity
import com.fsh.common.repository.BaseRepository
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.common.websocket.*
import com.fsh.quote.BuildConfig
import io.reactivex.disposables.CompositeDisposable
import okio.ByteString

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
    private var _quoteData: MutableLiveData<QuoteEntity> = MutableLiveData()
    val statusData:LiveData<Int> = _statusData
    val quoteData:LiveData<QuoteEntity> = _quoteData
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
    }

    private fun handleByteMsg(byteMsg: ByteString) {
        Log.e("QuoteSocketRepository","byte msg ${byteMsg.utf8()}")
    }

    private fun handleErrorMsg(msg: FWebSocketErrorMsg) {
        Log.e("QuoteSocketRepository","error msg ${msg.msg}",msg.cause)
    }

    private fun handleStatusMsg(msg: FWebSocketStatusMsg) {
        Log.e("QuoteSocketRepository","status msg ${msg.status}")
        _statusData.postValue(msg.status)
    }
}