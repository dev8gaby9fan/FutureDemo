package com.fsh.common.provider

import androidx.lifecycle.LiveData
import com.alibaba.android.arouter.facade.template.IProvider
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import io.reactivex.Observable

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 组件间共享数据的Service接口申明
 *
 */

interface QuoteService : IProvider{
    /**
     * 订阅行情
     * insList为null的时候，取消行情订阅
     */
    fun subscribeQuote(insList:List<String>?):Observable<QuoteEntity>

    /**
     * 搜索行情
     */
    fun searchInstruments(searchKey:String):Observable<List<InstrumentInfo>>

    fun getQuoteByInstrument(instrumentId:String?):QuoteEntity?
}

interface TradeService : IProvider{
    /**
     * 设置交易的合约
     */
    fun setTradeIns(ins:InstrumentInfo)

    /**
     * 是否有账号登录
     */
    fun isTradingLogin():Boolean
}

interface MainService : IProvider{
    companion object{
        const val PAGE_QUOTE = 0
        const val PAGE_TRADE = 1
    }

    /**
     * 切换界面
     */
    fun switchTabPage(index:Int)

    /**
     * 监听切换操作
     */
    fun getSwitchPageLiveData():LiveData<Int>
}
