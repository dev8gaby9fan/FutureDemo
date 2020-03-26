package com.fsh.trade.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.util.Omits
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig

import com.fsh.trade.repository.TradeApiProvider
import com.fsh.trade.repository.tradeapi.*
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel : BaseViewModel<TradeApiRepository>(){
    private val disposables = CompositeDisposable()
    private var _loginLiveData:MutableLiveData<TradeLoginFlowEvent> = MutableLiveData()
    val loginLiveData:LiveData<TradeLoginFlowEvent> = _loginLiveData

    init {
        repository = TradeApiProvider.providerCTPTradeApi()
        disposables.add(repository!!.getTradeEventObserver()
            .subscribe {handleTradeEvents(it)})
    }

    fun reqUserLogin(tradeAccount:TradeAccountConfig,broker:BrokerConfig){
        repository!!.reqUserLogin(broker,tradeAccount)
    }

    private fun handleTradeEvents(event:TradeEvent){
        when(event){
            //柜台连接成功
            is FrontConnectedEvent -> {_loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.FrontConnected,event))}
            //柜台断开连接
            is FrontDisconnectedEvent ->{_loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.FrontDisconnected,event))}
            //认证响应
            is RspAuthenEvent -> {
                val rspAuthenEvent = event
                if(Omits.isOmit(rspAuthenEvent.rsp.rspInfoField.errorMsg) && rspAuthenEvent.rsp.rspInfoField.errorID != 0){
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.AuthenFail,event))
                }else{
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.AuthenSuccess,event))
                }
            }
            //正在请求登录
            is ReqUserLoginEvent -> {_loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.ReqUserLogin,event))}
            //登录响应
            is RspUserLoginEvent -> {
                val rspLoginEvent = event
                //登录成功
                if(rspLoginEvent.rsp.rspInfo.errorID == 0){
                    //这里发起查询确认结算单记录，看查询的响应结果，
                    // 如果有结果就不加载结算单数据，如果没有就加载结算单数据
                    repository?.reqQryConfirmSettlement()
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspUserLoginSuccess,event))
                }else{
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspUserLoginFail,event))
                }
            }
            //查询今日确认记录
            is RspQryConfirmSettlementEvent ->{
                val rspQryConfirmSettlementEvent = event
                if(rspQryConfirmSettlementEvent.rsp.rspInfoField.errorID != 0 && (rspQryConfirmSettlementEvent.rsp.rspField == null || Omits.isOmit(rspQryConfirmSettlementEvent.rsp.rspField?.confirmDate))){
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspQryConfirmSettlementNoData,event))
                }else{
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspQryConfirmSettlementData,event))
                }
            }
            //正在查询结算单
            is ReqQrySettlementEvent -> {}
            //查询结算单响应
            is RspQrySettlementEvent -> {
                val rspSettlementEvent = event
                if(rspSettlementEvent.rsp.bIsLast && (rspSettlementEvent.rsp.rspInfoField.errorID == 0 || !Omits.isOmit(rspSettlementEvent.rsp.rspField?.content))){
                    _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspSettlementInfo,event))
                }
            }
            //确认结算单响应
            is RspConfirmSettlementEvent -> {
                _loginLiveData.postValue(TradeLoginFlowEvent(TradeLoginFlowType.RspConfirmSettlementInfo,event))
            }
        }
    }

    fun reqConfirmSettlementInfo(){
        repository?.reqConfirmSettlement()
    }

    fun reqUserLogout(){
        repository?.reqUserLogout()
    }

    override fun onDestroy() {
        disposables.clear()
    }
}
