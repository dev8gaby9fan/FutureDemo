package com.future.quote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.model.QuoteEntity
import com.fsh.common.repository.BaseRepository
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.common.util.Omits
import com.fsh.common.websocket.*
import com.future.quote.BuildConfig
import com.future.quote.data.PeekMessageFrame
import com.future.quote.data.SetChartFrame
import com.future.quote.data.SubscribeQuoteFrame
import com.future.quote.data.WebSocketTextFrame
import com.future.quote.service.DataParser
import com.future.quote.service.WebSocketFrameParser
import com.future.quote.enums.ChartType
import com.future.quote.enums.FutureChartType
import com.future.quote.model.KLineEntity
import com.google.gson.JsonParser
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import okio.ByteString
import java.util.concurrent.atomic.AtomicReference

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
    private var _quoteData: Subject<QuoteEntity> = PublishSubject.create()
    private var _chartData:Subject<List<KLineEntity>> = PublishSubject.create()
    private var socketStatus:Int = FWebSocket.STATUS_CLOSED
    private val webSocketFrameParser:DataParser<Unit>  by lazy(mode=LazyThreadSafetyMode.SYNCHRONIZED){
        WebSocketFrameParser(_quoteData,_chartData)
    }
    val statusData:LiveData<Int> = _statusData
    val quoteData = _quoteData
    val chartData = _chartData
    private var preSubscribedQuoteFrame:AtomicReference<SubscribeQuoteFrame> = AtomicReference(SubscribeQuoteFrame(Omits.OmitString))
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
//        Log.e("QuoteSocketRepository","text msg $text")
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
        if(msg.status == FWebSocket.STATUS_CONNECTED && !Omits.isOmit(preSubscribedQuoteFrame.get().ins_list)){
            sendMessage(preSubscribedQuoteFrame.get())
        }
        socketStatus = msg.status
        _statusData.postValue(msg.status)
    }

    fun sendMessage(webSocketFrame:WebSocketTextFrame){
        webSocket.sendMessage(webSocketFrame)
    }

    /**
     * isAppend 是否是叠加订阅操作,如果不是就不订阅之前已经订阅过的合约行情
     */
    fun subscribeQuote(insList:String,isAppend:Boolean = false){
        var frame = SubscribeQuoteFrame(insList)
        if(isAppend){
            var subscribeIns = if(!Omits.isOmit(preSubscribedQuoteFrame.get().ins_list)) "${preSubscribedQuoteFrame.get().ins_list},$insList"
                              else insList
            //去除重复订阅的合约
            subscribeIns = subscribeIns.split(",").distinctBy { it }.joinToString(",")
            frame = SubscribeQuoteFrame(subscribeIns)
        }
        Log.d("QuoteSocketRepository","subscribeQuote ${frame.ins_list}")
        sendMessage(frame)
        preSubscribedQuoteFrame.set(frame)

    }

    fun unSubscribeQuote(insList:String){
        val preFrame = preSubscribedQuoteFrame.get() ?: return
        val unSubList = insList.split(",")
        preFrame.ins_list = preFrame.ins_list.split(",")
            .filter { unSubList.contains(it) || Omits.isOmit(it)}
            .distinctBy { it }
            .joinToString { "," }
        sendMessage(preFrame)
    }

    fun isSocketConnected():Boolean = socketStatus == FWebSocket.STATUS_CONNECTED

    /**
     * 请求曲线数据
     */
    fun setChart(instrumentId:String, type: FutureChartType, viewWidth:Int = 500){
        val reqFrame = if(type.chartType == ChartType.Line){
            SetChartFrame(instrumentId,type.duration,0, 86400000000000L)
        }else{
            SetChartFrame(instrumentId,type.duration,null,null,viewWidth)
        }
        Log.d("QuoteSocketRepository","setChart $type ${reqFrame.toJsonString()}")
        sendMessage(reqFrame)
    }
}