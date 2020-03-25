package com.fsh.trade.repository.tradeapi

import android.util.Log
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import com.sfit.ctp.thosttraderapi.*
import com.sfit.ctp.thosttraderapi.CThostFtdcReqUserLoginField
import io.reactivex.subjects.Subject
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: CTP TradeApi 实现
 * CTP API每秒只能请求一次服务，不能请求太快
 */

class CTPTradeApi : TradeApiSource, CThostFtdcTraderSpi() {
    private var tradeEventPublish: Subject<TradeEvent>? = null
    override fun registerSubject(publish: Subject<TradeEvent>) {
        tradeEventPublish = publish
    }

    private val appPath = CommonUtil.application!!.filesDir.absolutePath
    private var tradeApi: CThostFtdcTraderApi? = null
    private lateinit var account: TradeAccountConfig
    private lateinit var broker: BrokerConfig
    private val nRequestIDFactor: AtomicInteger = AtomicInteger(0)

    //柜台连接成功
    override fun OnFrontConnected() {
        super.OnFrontConnected()
        tradeEventPublish?.onNext(FrontConnectedEvent(broker))
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
        Log.d("CTPTradeApi", "OnRspAuthenticate ${pRspInfo?.errorID} ${pRspInfo?.errorMsg}")
        tradeEventPublish?.onNext(RspAuthenEvent(pRspAuthenticateField, pRspInfo))
        //2.发起登录
        if (pRspInfo != null && pRspInfo.errorID == CODE_SUCCESS) {
            val loginField = CThostFtdcReqUserLoginField()
            loginField.brokerID = broker.brokerId
            loginField.userID = account.investorId
            loginField.password = account.password
            tradeApi?.ReqUserLogin(
                CommonUtil.application!!,
                loginField,
                nRequestIDFactor.getAndIncrement()
            )
            tradeEventPublish?.onNext(ReqUserLoginEvent(account, broker))
        }
    }

    override fun OnFrontDisconnected(p0: Int) {
        super.OnFrontDisconnected(p0)
        tradeEventPublish?.onNext(FrontDisconnectedEvent(broker))
        nRequestIDFactor.compareAndSet(nRequestIDFactor.get(), 0)
    }

    override fun OnRspUserLogout(
        p0: CThostFtdcUserLogoutField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspUserLogout(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspUserLogoutEvent(p0, p1, p3))
    }

    //登录响应
    override fun OnRspUserLogin(
        respLoginField: CThostFtdcRspUserLoginField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspUserLogin(respLoginField, rspField, nRequestID, bIsLast)
        Log.d("CTPTradeApi", "OnRspUserLogin ${rspField?.errorID} ${rspField?.errorMsg}")
        tradeEventPublish?.onNext(RspUserLoginEvent(respLoginField, rspField))
//        if (rspField == null || rspField!!.errorID != CODE_SUCCESS) {
//            return
//        }
//        //查询用户的结算单确认记录
//        val field = CThostFtdcQrySettlementInfoConfirmField()
//        field.investorID = account.investorId
//        field.accountID = respLoginField?.userID
//        field.brokerID = respLoginField?.brokerID
//        tradeApi?.ReqQrySettlementInfoConfirm(field, nRequestIDFactor.getAndIncrement())
//        Log.d("CTPTradeApi", "ReqQrySettlementInfo ${account.investorId}")
    }

    //查询资金响应
    override fun OnRspQryTradingAccount(
        accountField: CThostFtdcTradingAccountField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQryTradingAccount(accountField, rspField, nRequestID, bIsLast)
        tradeEventPublish?.onNext(RspQryTradingAccountEvent(accountField, rspField, bIsLast))
        Log.d(
            "CTPTradeApi",
            "OnRspQryTradingAccount ${rspField?.errorID} ${rspField?.errorMsg} ${accountField?.accountID} ${accountField?.settlementID}"
        )
    }


    //请求结算单响应
    override fun OnRspQrySettlementInfo(
        pSettlementInfo: CThostFtdcSettlementInfoLongField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQrySettlementInfo(pSettlementInfo, rspField, nRequestID, bIsLast)
        tradeEventPublish?.onNext(RspQrySettlementEvent(pSettlementInfo, rspField, bIsLast))
        Log.d(
            "CTPTradeApi", "OnRspQrySettlementInfo ${rspField?.errorID} ${rspField?.errorMsg}" +
                    "${pSettlementInfo?.investorID} ${pSettlementInfo?.brokerID} ${pSettlementInfo?.settlementID} ${pSettlementInfo?.sequenceNo} \r\n $bIsLast ${pSettlementInfo?.content}"
        )
        //4.确认结算单
        if(bIsLast && (rspField?.errorID == null || rspField?.errorID ==0)){
            val field = CThostFtdcSettlementInfoConfirmField()
            field.settlementID = pSettlementInfo!!.settlementID
            field.brokerID = pSettlementInfo!!.brokerID
            field.investorID = pSettlementInfo!!.investorID
            field.accountID = pSettlementInfo!!.investorID
            field.confirmDate = DateUtils.formatNow1()
            field.confirmTime = DateUtils.formatNow3()
            Log.d("CTPTradeApi","ReqSettlementInfoConfirm ${pSettlementInfo!!.settlementID} ${pSettlementInfo!!.brokerID} ${pSettlementInfo!!.investorID} ${field.confirmDate} ${field.confirmTime}")
            tradeApi?.ReqSettlementInfoConfirm(field,nRequestIDFactor.getAndIncrement())
        }
    }

    //查询结算单响应
    override fun OnRspQrySettlementInfoConfirm(
        p0: CThostFtdcSettlementInfoConfirmField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQrySettlementInfoConfirm(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspQryConfirmSettlementEvent(p0, p1, p3))
        Log.d(
            "CTPTradeApi",
            "OnRspQrySettlementInfoConfirm --> ${p0?.investorID} ${p0?.brokerID} ${p0?.settlementID} ${p0?.confirmDate} ${p0?.confirmTime} ${p1?.errorID} ${p1?.errorMsg} $p3"
        )
        // 3.请求结算单信息
        if (p0?.settlementID == null && p1?.errorID == null) {
            val reqSettlementField = CThostFtdcQrySettlementInfoField()
            reqSettlementField.accountID = account.investorId
            reqSettlementField.brokerID = broker.brokerId
            reqSettlementField.investorID = account.investorId
            tradeApi?.ReqQrySettlementInfo(reqSettlementField, nRequestIDFactor.getAndIncrement())
            tradeEventPublish?.onNext(ReqQrySettlementEvent(account, broker))
            Log.d("CTPTradeApi","ReqQrySettlementInfo ${account.investorId} ${broker.brokerId}")
        }
    }

    //5.确认结算单响应
    override fun OnRspSettlementInfoConfirm(
        p0: CThostFtdcSettlementInfoConfirmField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspSettlementInfoConfirm(p0, p1, p2, p3)
        Log.d("CTPTradeApi","OnRspSettlementInfoConfirm ${p1?.errorMsg} ${p1?.errorID} $p3")
        tradeEventPublish?.onNext(RspConfirmSettlementEvent(p0, p1, p3))
    }

    //
    override fun OnRspExecOrderInsert(
        p0: CThostFtdcInputExecOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspExecOrderInsert(p0, p1, p2, p3)
    }

    //查询委托响应
    override fun OnRspQryOrder(
        p0: CThostFtdcOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQryOrder(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspQryOrderEvent(p0, p1, p3))
    }

    //委托汇报
    override fun OnRtnOrder(p0: CThostFtdcOrderField?) {
        super.OnRtnOrder(p0)
        tradeEventPublish?.onNext(RtnOrderEvent(p0))
    }

    //撤单响应
    override fun OnRspOrderAction(
        p0: CThostFtdcInputOrderActionField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspOrderAction(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspOrderActionEvent(p0, p1, p3))
    }

    //查询成交响应
    override fun OnRspQryTrade(
        p0: CThostFtdcTradeField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQryTrade(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspQryTradeEvent(p0, p1, p3))
    }

    //成交回报
    override fun OnRtnTrade(p0: CThostFtdcTradeField?) {
        super.OnRtnTrade(p0)
        tradeEventPublish?.onNext(RtnTradeEvent(p0))
    }

    //持仓明细响应
    override fun OnRspQryInvestorPositionDetail(
        p0: CThostFtdcInvestorPositionDetailField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQryInvestorPositionDetail(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspQryPositionDetailEvent(p0, p1, p3))
    }

    //报单响应--注意报单失败
    override fun OnRspOrderInsert(
        p0: CThostFtdcInputOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspOrderInsert(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspOrderInsertEvent(p0, p1))
    }

    /**
     * ==========================================================================================
     * =============================  开放接口方法    ===========================================
     * ==========================================================================================
     */
    override fun initTradeApi() {
        tradeApi = CThostFtdcTraderApi.CreateFtdcTraderApi(appPath)
        tradeApi!!.RegisterSpi(this)
        tradeApi!!.RegisterFront(broker.frontIp)
        tradeApi!!.SubscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.SubscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.Init()
        tradeEventPublish?.onNext(InitApiEvent(broker))
        Log.d("CTPTradeApi", "initTradeApi")
    }

    override fun reqAuthenticate() {
        val authField = CThostFtdcReqAuthenticateField()
        authField.brokerID = broker.brokerId
        authField.appID = broker.appId
        authField.authCode = broker.authCode
        authField.userProductInfo = broker.userProductInfo
        tradeApi?.ReqAuthenticate(authField, nRequestIDFactor.getAndIncrement())
        tradeEventPublish?.onNext(ReqAuthenEvent(broker))
        Log.d("CTPTradeApi", "reqAuthenticate ${broker.brokerId} ${broker.frontIp} ${broker.appId}")
    }

    override fun reqUserLogin(brokerConfig: BrokerConfig, account: TradeAccountConfig) {
        this.broker = brokerConfig
        this.account = account
        if (tradeApi == null) {
            initTradeApi()
        }
    }

    override fun reqUserLogout() {
        val logoutField = CThostFtdcUserLogoutField()
        logoutField.brokerID = broker.brokerId
        logoutField.userID = account.investorId
        tradeApi?.ReqUserLogout(logoutField, nRequestIDFactor.getAndIncrement())
    }

    override fun reqQryConfirmSettlement() {
        val reqField = CThostFtdcQrySettlementInfoConfirmField()
        reqField.brokerID = broker.brokerId
        reqField.accountID = account.investorId
        reqField.investorID = account.investorId
        tradeApi?.ReqQrySettlementInfoConfirm(reqField,nRequestIDFactor.getAndIncrement())
    }

    override fun reqConfirmSettlement() {
        val reqField = CThostFtdcQrySettlementInfoConfirmField()
        reqField.accountID = account.investorId
        reqField.brokerID = broker.brokerId
        reqField.investorID = account.investorId
        tradeApi?.ReqQrySettlementInfoConfirm(reqField, nRequestIDFactor.getAndIncrement())
    }


    override fun reqQryOrder() {

    }

    override fun reqQryTrade() {

    }

    override fun reqQryPositionDetail() {

    }

    override fun reqOrderInsert() {

    }

    override fun reqOrderAction() {

    }

    companion object {
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