package com.fsh.trade.repository.tradeapi

import android.util.Log
import com.fsh.common.util.CommonUtil
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import com.sfit.ctp.thosttraderapi.*
import com.sfit.ctp.thosttraderapi.CThostFtdcReqUserLoginField
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: CTP TradeApi 实现
 *
 */

class CTPTradeApi : TradeApiSource, CThostFtdcTraderSpi() {
    private val appPath = CommonUtil.application!!.filesDir.absolutePath
    private var tradeApi:CThostFtdcTraderApi? = null
    private lateinit var account:TradeAccountConfig
    private lateinit var broker:BrokerConfig
    private val nRequestIDFactor:AtomicInteger = AtomicInteger(0)
    override fun initTradeApi() {
        tradeApi = CThostFtdcTraderApi.CreateFtdcTraderApi(appPath)
        tradeApi!!.RegisterSpi(this)
        tradeApi!!.RegisterFront(broker.frontIp)
        tradeApi!!.SubscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.SubscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.Init()
        Log.d("CTPTradeApi","initTradeApi")
    }

    override fun reqAuthenticate() {
        val authField = CThostFtdcReqAuthenticateField()
        authField.brokerID = broker.brokerId
        authField.appID = broker.appId
        authField.authCode = broker.authCode
        authField.userProductInfo = broker.userProductInfo
        tradeApi?.ReqAuthenticate(authField, nRequestIDFactor.getAndIncrement())
        Log.d("CTPTradeApi","reqAuthenticate ${broker.brokerId} ${broker.frontIp} ${broker.appId}")
    }

    override fun reqUserLogin(brokerConfig: BrokerConfig, account: TradeAccountConfig) {
        this.broker = brokerConfig
        this.account = account
        if(tradeApi == null){
            initTradeApi()
        }
    }

    override fun reqUserLogout() {

    }
    //柜台连接成功
    override fun OnFrontConnected() {
        super.OnFrontConnected()
        //1.先认证柜台
        reqAuthenticate()
    }

    //认证响应
    override fun OnRspAuthenticate(
        pRspAuthenticateField: CThostFtdcRspAuthenticateField?,
        pRspInfo: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspAuthenticate(pRspAuthenticateField, pRspInfo, nRequestID, bIsLast)
        Log.d("CTPTradeApi","OnRspAuthenticate ${pRspInfo?.errorID} ${pRspInfo?.errorMsg}")
        //2.发起登录
        if(pRspInfo != null && pRspInfo.errorID == CODE_SUCCESS){
            val loginField = CThostFtdcReqUserLoginField()
            loginField.brokerID = broker.brokerId
            loginField.userID = account.investorId
            loginField.password = account.password
            tradeApi?.ReqUserLogin(CommonUtil.application!!,loginField, nRequestIDFactor.getAndIncrement())
        }
    }

    override fun OnFrontDisconnected(p0: Int) {
        super.OnFrontDisconnected(p0)
        nRequestIDFactor.compareAndSet(nRequestIDFactor.get(),0)
    }


    //登录响应
    override fun OnRspUserLogin(
        respLoginField: CThostFtdcRspUserLoginField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspUserLogin(respLoginField, rspField, nRequestID, bIsLast)
        Log.d("CTPTradeApi","OnRspAuthenticate ${rspField?.errorID} ${rspField?.errorMsg}")
        if(rspField == null || rspField!!.errorID != CODE_SUCCESS){
            return;
        }
        //3.1请求交易账号信息
        val reqAccountField = CThostFtdcQryTradingAccountField()
        reqAccountField.brokerID = broker.brokerId
        reqAccountField.currencyID = "CNY"
        reqAccountField.investorID = account.investorId
        tradeApi?.ReqQryTradingAccount(reqAccountField,nRequestIDFactor.getAndIncrement())
        Log.d("CTPTradeApi","ReqQryTradingAccount ${account.investorId}")
        //3.请求结算单信息
        val reqSettlementField = CThostFtdcQrySettlementInfoField()
        reqSettlementField.accountID = account.investorId
        reqSettlementField.brokerID = broker.brokerId
        reqSettlementField.currencyID = "CNY"
        reqSettlementField.investorID = account.investorId
        tradeApi?.ReqQrySettlementInfo(reqSettlementField,nRequestIDFactor.getAndIncrement())
        Log.d("CTPTradeApi","ReqQrySettlementInfo ${account.investorId}")
    }

    override fun OnRspQryTradingAccount(
        accountField: CThostFtdcTradingAccountField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQryTradingAccount(accountField, rspField, nRequestID, bIsLast)
        Log.d("CTPTradeApi","OnRspQryTradingAccount ${rspField?.errorID} ${rspField?.errorMsg} ${accountField?.accountID} ${accountField?.settlementID}")
    }




    //请求结算单响应
    override fun OnRspQrySettlementInfo(
        pSettlementInfo: CThostFtdcSettlementInfoLongField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQrySettlementInfo(pSettlementInfo, rspField, nRequestID, bIsLast)
        Log.d("CTPTradeApi","OnRspQrySettlementInfo ${rspField?.errorID} ${rspField?.errorMsg}" +
                "${pSettlementInfo?.investorID} ${pSettlementInfo?.brokerID} ${pSettlementInfo?.settlementID} ${pSettlementInfo?.sequenceNo} \r\n $bIsLast ${pSettlementInfo?.content}")
        //4.确认结算单
    }
    //5.确认结算单响应
    override fun OnRspQrySettlementInfoConfirm(
        p0: CThostFtdcSettlementInfoConfirmField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQrySettlementInfoConfirm(p0, p1, p2, p3)
        //登录成功
    }

    override fun OnRspExecOrderInsert(
        p0: CThostFtdcInputExecOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspExecOrderInsert(p0, p1, p2, p3)
    }



    override fun OnRtnOrder(p0: CThostFtdcOrderField?) {
        super.OnRtnOrder(p0)
    }

    override fun OnRspOrderInsert(
        p0: CThostFtdcInputOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspOrderInsert(p0, p1, p2, p3)
    }


    companion object{
        private const val CODE_AUTH = 10000
        private const val CODE_USER_LOGIN = 10001
        private const val CODE_REQ_SETTLEMENT = 10002
        private const val CODE_COMFIRM_SETTLEMENT = 10003
        private const val CODE_USER_LOGOUT = 10004
        private const val CODE_SUCCESS = 0
        init {
            try {
                System.loadLibrary("thosttraderapi")
                System.loadLibrary("thosttraderapi_wrap")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}