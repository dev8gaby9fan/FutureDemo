package com.future.trade.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.provider.TradeService
import com.future.trade.repository.TradeApiProvider

@Route(path = ARouterPath.Service.SERVICE_TRADE)
class TradeServiceImpl : TradeService{
    private var _tradeInsLiveData:MutableLiveData<InstrumentInfo> = MutableLiveData()
    val tradeInsLiveData:LiveData<InstrumentInfo> = _tradeInsLiveData
    init {
        Log.d("TradeServiceImpl","init")
    }
    override fun init(context: Context?) {
    }

    /**
     * 设置交易界面的交易合约
     */
    override fun setTradeIns(ins: InstrumentInfo) {
        _tradeInsLiveData.postValue(ins)
    }

    /**
     * 判断用户是否有登录交易账号
     */
    override fun isTradingLogin(): Boolean =
        TradeApiProvider.providerCTPTradeApi().isUserLogined()
}