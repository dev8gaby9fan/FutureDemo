package com.future.trade.repository.tradeapi

import android.util.Log
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.future.trade.bean.*
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.repository.TradeApiProvider
import com.sfit.ctp.thosttraderapi.*
import com.sfit.ctp.thosttraderapi.CThostFtdcReqUserLoginField
import io.reactivex.subjects.Subject
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.log


/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/13
 * description: CTP TradeApi 实现
 * CTP API每秒只能请求一次服务，不能请求太快
 */

class CTPTradeApi : TradeApiSource, CThostFtdcTraderSpi(),CTPTradeApiSendMsgQueue.DataHandler{
    private var tradeEventPublish: Subject<TradeEvent>? = null
    override fun registerSubject(publish: Subject<TradeEvent>) {
        tradeEventPublish = publish
    }

    private val appPath = CommonUtil.application!!.filesDir.absolutePath
    private var tradeApi: CThostFtdcTraderApi? = null
    private var account: TradeAccountConfig? = null
    private var broker: BrokerConfig? = null
    private val nRequestIDFactor: AtomicInteger = AtomicInteger(0)
    private val isTradeApiInited:AtomicBoolean = AtomicBoolean(false)
    private val sendReqQueue = CTPTradeApiSendMsgQueue(this)
    //柜台连接成功
    override fun OnFrontConnected() {
        super.OnFrontConnected()
        Log.d("CTPTradeApi","OnFrontConnected")
        isTradeApiInited.compareAndSet(isTradeApiInited.get(),true)
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
        tradeEventPublish?.onNext(RspAuthenEvent(RspAuthencate(RspAuthenticateField.fromCTPAPIRsp(pRspAuthenticateField),RspInfoField.fromCTPAPIRsp(pRspInfo),bIsLast)))
        //2.发起登录
        if (pRspInfo != null && pRspInfo.errorID == CODE_SUCCESS) {
            val loginField = CThostFtdcReqUserLoginField()
            loginField.brokerID = broker?.brokerId
            loginField.userID = account?.investorId
            loginField.password = account?.password
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
        Log.d("CTPTradeApi","OnFrontDisconnected")
        isTradeApiInited.compareAndSet(isTradeApiInited.get(),false)
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
        //收到退出登录响应就清空登录账号
        broker = null
        account = null
        tradeEventPublish?.onNext(RspUserLogoutEvent(RspUserLogout(RspUserLogoutField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)))
    }

    //登录响应
    override fun OnRspUserLogin(
        respLoginField: CThostFtdcRspUserLoginField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspUserLogin(respLoginField, rspField, nRequestID, bIsLast)
        tradeEventPublish?.onNext(RspUserLoginEvent(RspUserLogin(RspUserLoginField.fromCTPAPI(respLoginField),
            RspInfoField.fromCTPAPIRsp(rspField),bIsLast)))
    }

    //查询资金响应
    override fun OnRspQryTradingAccount(
        accountField: CThostFtdcTradingAccountField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQryTradingAccount(accountField, rspField, nRequestID, bIsLast)
        tradeEventPublish?.onNext(RspQryTradingAccountEvent(RspTradingAccount(RspTradingAccountField.fromCPTAPI(accountField),
            RspInfoField.fromCTPAPIRsp(rspField),bIsLast)))
    }


    //请求结算单响应
    override fun OnRspQrySettlementInfo(
        pSettlementInfo: CThostFtdcSettlementInfoLongField?,
        rspField: CThostFtdcRspInfoField?,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        super.OnRspQrySettlementInfo(pSettlementInfo, rspField, nRequestID, bIsLast)
        tradeEventPublish?.onNext(RspQrySettlementEvent(RspQrySettlementInfo(RspQrySettlementInfoField.fromCTPAPI(pSettlementInfo),
            RspInfoField.fromCTPAPIRsp(rspField),bIsLast)))
    }

    //查询结算单响应
    override fun OnRspQrySettlementInfoConfirm(
        p0: CThostFtdcSettlementInfoConfirmField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQrySettlementInfoConfirm(p0, p1, p2, p3)
        val rsp = RspQrySettlementInfoConfirm(RspQrySettlementInfoConfirmField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)
        tradeEventPublish?.onNext(RspQryConfirmSettlementEvent(rsp))
        Log.d(
            "CTPTradeApi",
            "OnRspQrySettlementInfoConfirm --> ${rsp.rspField?.accountID} ${rsp.rspField?.brokerID} ${rsp.rspField?.settlementID} ${rsp.rspField?.confirmDate} ${rsp.rspField?.confirmTime} ${rsp.rspInfoField.errorID} ${rsp.rspInfoField.errorMsg} ${rsp.bIsLast}"
        )
        // 3.请求结算单信息
        if (rsp.rspField?.settlementID == null && Omits.isOmit(rsp.rspInfoField.errorID)) {
            val reqSettlementField = CThostFtdcQrySettlementInfoField()
            reqSettlementField.accountID = account?.investorId
            reqSettlementField.brokerID = broker?.brokerId
            reqSettlementField.investorID = account?.investorId
            tradeApi?.ReqQrySettlementInfo(reqSettlementField, nRequestIDFactor.getAndIncrement())
            tradeEventPublish?.onNext(ReqQrySettlementEvent(account, broker))
            Log.d("CTPTradeApi","ReqQrySettlementInfo ${account?.investorId} ${broker?.brokerId}")
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
        val rsp = RspConfirmSettlementInfo(RspQrySettlementInfoConfirmField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)
        tradeEventPublish?.onNext(RspConfirmSettlementEvent(rsp))
        Log.d("CTPTradeApi","OnRspSettlementInfoConfirm ${rsp.rspInfoField.errorMsg} ${rsp.rspInfoField.errorID} ${rsp.bIsLast}")
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
        val rspData = RspQryOrder(RspOrderField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)
        tradeEventPublish?.onNext(RspQryOrderEvent(rspData))
        Log.d("CTPTradeApi","OnRspQryOrder ${rspData.rspField?.orderSysID} ${rspData.rspInfoField.errorMsg} ${rspData.rspInfoField.errorID}")
    }

    //委托汇报
    override fun OnRtnOrder(p0: CThostFtdcOrderField?) {
        super.OnRtnOrder(p0)
        val rtnData = RtnOrder(RspOrderField.fromCTPAPI(p0)!!)
        tradeEventPublish?.onNext(RtnOrderEvent(rtnData))
        Log.d("CTPTradeApi","OnRtnOrder ${rtnData.rspField.orderSysID}")
    }

    //撤单响应
    override fun OnRspOrderAction(
        p0: CThostFtdcInputOrderActionField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspOrderAction(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspOrderActionEvent(RspOrderAction(RspOrderActionField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)))
    }

    //查询成交响应
    override fun OnRspQryTrade(
        p0: CThostFtdcTradeField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQryTrade(p0, p1, p2, p3)
        val rspData = RspQryTrade(RspTradeField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)
        tradeEventPublish?.onNext(RspQryTradeEvent(rspData))
        Log.d("CTPTradeApi","OnRtnTrade ${rspData.rspField?.orderSysID} ${rspData.rspInfoField.errorMsg} ${rspData.rspInfoField.errorID}")
    }

    //成交回报
    override fun OnRtnTrade(p0: CThostFtdcTradeField?) {
        super.OnRtnTrade(p0)
        val rtnData = RtnTrade(RspTradeField.fromCTPAPI(p0)!!)
        tradeEventPublish?.onNext(RtnTradeEvent(rtnData))
        Log.d("CTPTradeApi","OnRtnTrade ${rtnData.rspField.orderSysID}")
    }

    //持仓明细响应
    override fun OnRspQryInvestorPositionDetail(
        p0: CThostFtdcInvestorPositionDetailField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspQryInvestorPositionDetail(p0, p1, p2, p3)
        Log.d("CTPTradeApi","OnRspQryInvestorPositionDetail")
        tradeEventPublish?.onNext(RspQryPositionDetailEvent(RspQryPositionDetail(RspPositionDetailField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)))
    }

    //报单响应--注意报单失败
    override fun OnRspOrderInsert(
        p0: CThostFtdcInputOrderField?,
        p1: CThostFtdcRspInfoField?,
        p2: Int,
        p3: Boolean
    ) {
        super.OnRspOrderInsert(p0, p1, p2, p3)
        tradeEventPublish?.onNext(RspOrderInsertEvent(RspOrderInsert(RspOrderInsertField.fromCTPAPI(p0),
            RspInfoField.fromCTPAPIRsp(p1),p3)))
    }

    override fun OnErrRtnOrderInsert(p0: CThostFtdcInputOrderField?, p1: CThostFtdcRspInfoField?) {
        super.OnErrRtnOrderInsert(p0, p1)
        Log.d("CTPTradeApi","OnErrRtnOrderInsert ${p1?.errorID} ${p1?.errorMsg} ${p0?.instrumentID} ${CTPDirection.from(p0!!.direction)?.text}[${p0.direction}] ${CTPCombOffsetFlag.from(p0.combOffsetFlag[0])?.text}[${p0.combOffsetFlag}] volumeTotalOriginal=${p0.volumeTotalOriginal} ${p0.orderRef} ${p0.requestID}")
    }

    override fun OnErrRtnOrderAction(p0: CThostFtdcOrderActionField?, p1: CThostFtdcRspInfoField?) {
        super.OnErrRtnOrderAction(p0, p1)
        Log.d("CTPTradeApi","OnErrRtnOrderAction ${p1?.errorID} ${p1?.errorMsg} ${p0?.actionFlag} ${p0?.ipAddress}")
    }

    override fun handleDataWhenInterrupted(list: List<CTPRequestFrame<*>>) {
        for(frame in list){
            handleData(frame)
        }
    }

    override fun handleData(data: CTPRequestFrame<*>) {
        when(data.frameType){
            CTPRequestFrameType.UserLogout -> tradeApi?.ReqUserLogout(data.frame as CThostFtdcUserLogoutField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.ConfirmSettlement -> tradeApi?.ReqSettlementInfoConfirm(data.frame as CThostFtdcSettlementInfoConfirmField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.QryOrder -> tradeApi?.ReqQryOrder(data.frame as CThostFtdcQryOrderField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.QryTrade -> tradeApi?.ReqQryTrade(data.frame as CThostFtdcQryTradeField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.QryPositionDetail -> tradeApi?.ReqQryInvestorPositionDetail(data.frame as CThostFtdcQryInvestorPositionDetailField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.QryTradingAccount -> tradeApi?.ReqQryTradingAccount(data.frame as CThostFtdcQryTradingAccountField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.OrderInsert -> tradeApi?.ReqOrderInsert(data.frame as CThostFtdcInputOrderField,nRequestIDFactor.getAndIncrement())
            CTPRequestFrameType.OrderAction -> tradeApi?.ReqOrderAction(data.frame as CThostFtdcInputOrderActionField,nRequestIDFactor.getAndIncrement())
        }
        Log.d("CTPTradeApi","[${broker?.brokerId} , ${account?.investorId}] send ${data.frameType}")
    }

    /**
     * ==========================================================================================
     * =============================  开放接口方法    ===========================================
     * ==========================================================================================
     */
    override fun initTradeApi() {
        tradeApi = CThostFtdcTraderApi.CreateFtdcTraderApi(appPath)
        tradeApi!!.RegisterSpi(this)
        tradeApi!!.RegisterFront(broker?.frontIp)
        tradeApi!!.SubscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.SubscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK)
        tradeApi!!.Init()
        tradeEventPublish?.onNext(InitApiEvent(broker))
        Log.d("CTPTradeApi", "initTradeApi")
    }

    override fun reqAuthenticate() {
        if(broker != null){
            val authField = CThostFtdcReqAuthenticateField()
            authField.brokerID = broker?.brokerId
            authField.appID = broker?.appId
            authField.authCode = broker?.authCode
            authField.userProductInfo = broker?.userProductInfo
            tradeApi?.ReqAuthenticate(authField, nRequestIDFactor.getAndIncrement())
            tradeEventPublish?.onNext(ReqAuthenEvent(broker))
            Log.d("CTPTradeApi","[${account?.investorId}] reqAuthenticate")
        }
    }

    override fun reqUserLogin(brokerConfig: BrokerConfig, account: TradeAccountConfig) {
        this.broker = brokerConfig
        this.account = account
        if (!isTradeApiInited.get()) {
            initTradeApi()
        }else{
            reqAuthenticate()
        }
    }

    override fun reqUserLogout() {
        val logoutField = CThostFtdcUserLogoutField().apply {
            brokerID = broker?.brokerId
            userID = account?.investorId
        }
        sendReqQueue.put(CTPUserLogoutFrame(logoutField))
    }

    override fun reqQryConfirmSettlement() {
        val reqField = CThostFtdcQrySettlementInfoConfirmField().apply {
            brokerID = broker?.brokerId
            accountID = account?.investorId
            investorID = account?.investorId
        }
        tradeApi?.ReqQrySettlementInfoConfirm(reqField,nRequestIDFactor.getAndIncrement())
        Log.d("CTPTradeApi","[${account?.investorId}] req qry confirm settlement")
    }

    override fun reqConfirmSettlement() {
        val reqField = CThostFtdcSettlementInfoConfirmField().apply {
            accountID = account?.investorId
            brokerID = broker?.brokerId
            investorID = account?.investorId
            confirmDate = DateUtils.formatNow1()
            confirmTime = DateUtils.formatNow3()
        }
        sendReqQueue.put(CTPConfirmSettlement(reqField))
    }


    override fun reqQryOrder() {
        val refField = CThostFtdcQryOrderField().apply {
            brokerID = broker?.brokerId
            investorID = account?.investorId
        }
        sendReqQueue.put(CTPQryOrder(refField))
    }

    override fun reqQryTrade() {
        val reqField = CThostFtdcQryTradeField().apply {
            brokerID = broker?.brokerId
            investorID = account?.investorId
        }
        sendReqQueue.put(CTPQryTrade(reqField))
    }

    override fun reqQryTradingAccount() {
        val reqField = CThostFtdcQryTradingAccountField().apply{
            investorID = account?.investorId
            accountID = account?.investorId
            brokerID = broker?.brokerId
        }
        sendReqQueue.put(CTPQryTradingAccount(reqField))
    }

    override fun reqQryPositionDetail() {
        val reqField = CThostFtdcQryInvestorPositionDetailField().apply {
            brokerID = broker?.brokerId
            investorID = account?.investorId
        }
        sendReqQueue.put(CTPQryPositionDetail(reqField))
    }

    override fun reqOrderInsert(order:IOrderInsertField) {
        val reqField = order.toCThostFtdcInputOrderField()
        sendReqQueue.put(CTPOrderInsert(reqField))
    }

    override fun reqOrderAction(action:IInputOrderActionField) {
        val reqField = action.toCThostFtdcInputOrderActionField()
        tradeApi?.ReqOrderAction(reqField,nRequestIDFactor.getAndIncrement())
        sendReqQueue.put(CTPOrderAction(reqField))
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