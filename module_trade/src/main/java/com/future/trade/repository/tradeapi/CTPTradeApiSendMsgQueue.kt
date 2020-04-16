package com.future.trade.repository.tradeapi

import android.util.Log
import java.util.concurrent.LinkedBlockingQueue

class CTPTradeApiSendMsgQueue (private val handler:DataHandler): Thread("CTP_TRADE_API"){
    private val delayMillis = 500
    private val queue = LinkedBlockingQueue<CTPRequestFrame<*>>()
    private val lock:Object = Object()
    //上一次数据处理的时间
    private var preHandleTime:Long = 0
    fun put(data:CTPRequestFrame<*>){
        try{
            queue.put(data)
        }catch (e:InterruptedException){
            return
        }
        if(!isAlive){
            start()
        }
    }

    override fun run() {
        while(true){
            try{
                val take = queue.take()
                var currentTime = System.currentTimeMillis()
                synchronized(lock){
                    while (currentTime - preHandleTime < delayMillis){
                        lock.wait(delayMillis-(currentTime - preHandleTime))
                        currentTime = System.currentTimeMillis()
                    }
                }
                handler.handleData(take)
                preHandleTime = System.currentTimeMillis()
            }catch (e:InterruptedException){
                if(!queue.isEmpty()){
                    handler.handleDataWhenInterrupted(queue.toList())
                }
                queue.clear()
                return
            }
        }
    }

    interface DataHandler{
        fun handleData(data:CTPRequestFrame<*>)

        fun handleDataWhenInterrupted(list:List<CTPRequestFrame<*>>)
    }

    fun release(){
        if(!isInterrupted){
            interrupt()
        }
    }
}