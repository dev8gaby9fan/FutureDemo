package com.fsh.trade.bean

import com.fsh.common.util.Omits
import com.fsh.trade.util.DiffComparable
import com.sfit.ctp.thosttraderapi.*

/**
 * 声明一些和交易相关的data class
 */


data class RspInfoField(var errorID:Int = Omits.OmitInt, var errorMsg:String=Omits.OmitString){
    companion object{
        fun fromCTPAPIRsp(rspInfoField: CThostFtdcRspInfoField?):RspInfoField =
            if(rspInfoField == null){
                RspInfoField()
            }else{
                RspInfoField(rspInfoField.errorID,rspInfoField.errorMsg)
            }
    }
}

/**
 * 响应消息基类
 */
 open class APIRspTradeObj(var rspInfoField:RspInfoField,var bIsLast:Boolean)

data class RspAuthenticateField(var brokerID:String,var userID:String,var userProductInfo:String,
                                var appID:String,var appType:Char){
    companion object{
        fun fromCTPAPIRsp(rsp: CThostFtdcRspAuthenticateField?):RspAuthenticateField? =
            if(rsp == null)
                null
            else
                RspAuthenticateField(rsp.brokerID,rsp.userID,rsp.userProductInfo,rsp.appID,rsp.appType)
    }
}

/**
 * 认证响应
 */
class RspAuthencate(var rspField:RspAuthenticateField?,rspInfo:RspInfoField,isLast:Boolean):APIRspTradeObj(rspInfo,isLast)

/**
 * 登录响应数据
 */
data class RspUserLoginField(var tradingDay:String,var loginTime:String,var brokerID:String,var userID:String,var systemName:String,var frontID:Int,var sessionID:Int,var maxOrderRef:String,var SHFETime:String,var DCETime:String,var CZCETime:String,var FFEXTime:String,var INETime:String){
    companion object{
        fun fromCTPAPI(rspField: CThostFtdcRspUserLoginField?):RspUserLoginField? =
            if(rspField == null) null
            else RspUserLoginField(rspField.tradingDay,rspField.loginTime,rspField.brokerID,rspField.userID,rspField.systemName,
                rspField.frontID,rspField.sessionID,rspField.maxOrderRef,rspField.shfeTime,rspField.dceTime,rspField.czceTime,rspField.ffexTime,rspField.ineTime)
    }
}

/**
 * 登录响应
 */
class RspUserLogin(var rspField:RspUserLoginField?,var rspInfo:RspInfoField,isLast:Boolean):APIRspTradeObj(rspInfo,isLast)

/**
 * 登出响应数据
 */
data class RspUserLogoutField(var brokerID:String,var userID:String){
    companion object{
        fun fromCTPAPI(rsp: CThostFtdcUserLogoutField?):RspUserLogoutField? =
            if(rsp == null) null
            else RspUserLogoutField(rsp.brokerID,rsp.userID)
    }
}

/**
 * 登出响应
 */
class RspUserLogout(var rspField:RspUserLogoutField?,var rspInfo: RspInfoField,isLast:Boolean):APIRspTradeObj(rspInfo,isLast)

/**
 * 查询确认结算单记录响应数据
 */
data class RspQrySettlementInfoConfirmField(var brokerID:String,var investorID:String,var confirmDate:String,var confirmTime:String,var settlementID:Int,var accountID:String,var currencyID:String){
    companion object{
        fun fromCTPAPI(rsp: CThostFtdcSettlementInfoConfirmField?):RspQrySettlementInfoConfirmField? =
            if(rsp == null) null
            else RspQrySettlementInfoConfirmField(rsp.brokerID,rsp.investorID,rsp.confirmDate,rsp.confirmTime,rsp.settlementID,rsp.accountID,rsp.currencyID)
    }
}

/**
 * 查询确认结算单记录响应
 */
class RspQrySettlementInfoConfirm(var rspField:RspQrySettlementInfoConfirmField?,rspInfo:RspInfoField,bIsLast:Boolean):APIRspTradeObj(rspInfo,bIsLast)

data class RspQrySettlementInfoField(var tradingDay:String,var settlementID:Int,var brokerID:String,var investorID: String,var sequenceNo:Int,var content:String){
    companion object{
        fun fromCTPAPI(rsp:CThostFtdcSettlementInfoLongField?):RspQrySettlementInfoField? =
            if(rsp == null) null
            else RspQrySettlementInfoField(rsp.tradingDay,rsp.settlementID,rsp.brokerID,rsp.investorID,rsp.sequenceNo,rsp.content)
    }
}

/**
 * 查询结算单响应
 */
class RspQrySettlementInfo(var rspField:RspQrySettlementInfoField?,rspInfo:RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 确认结算单响应
 */
class RspConfirmSettlementInfo(var rspField:RspQrySettlementInfoConfirmField?,rspInfo:RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 查询资金响应数据
 */
data class RspTradingAccountField(var brokerID: String,var accountID: String,var preMortgage:Double,var preCredit:Double,var preDeposit:Double,var preBalance:Double,var preMargin:Double,var interestBase:Double,var interest:Double,var deposit:Double,var withDraw:Double,var frozenMargin:Double,var frozonCash:Double,var frozenCommission:Double,var currMargin:Double,var cashIn:Double,var commission:Double,var closeProfit:Double,var positionProfit:Double,var balance:Double,var avaliable:Double,var withDrawQuota:Double,var reserve:Double,var tradingDay:String,var settlementID: Int,var credit:Double,var mortgage:Double,var exchangeMargin:Double,var deliveryMargin:Double,var exchangeDeliveryMargin:Double,var reserveBalance:Double,var currencyID: String,var preFundMortgageIn:Double,var preFoundMortgageOut:Double,var fundMortgageIn:Double,var fundMortgageOut:Double,var fundMortgageAvailable:Double,var mortgageableFund:Double,var specProductMargin:Double,var specProductFrozenMargin:Double,var specProductCommission:Double,var specProductFrozenCommission:Double,var specProductPositionProfit:Double,var specProductCloseProfit:Double,var specProductPositionProfitByAlg:Double,var specProductExchangeMargin:Double,var bizType:Char,var frozenSwap:Double,var remainSwap:Double){
    companion object{
        fun fromCPTAPI(rsp:CThostFtdcTradingAccountField?):RspTradingAccountField? =
            if(rsp == null) null
            else RspTradingAccountField(rsp.brokerID,rsp.accountID,rsp.preMortgage,rsp.preCredit,rsp.preDeposit,rsp.preBalance,rsp.preMargin,rsp.interestBase,rsp.interest,rsp.preDeposit,rsp.preDeposit,rsp.frozenMargin,rsp.frozenCash,rsp.frozenCommission,rsp.currMargin,rsp.cashIn,rsp.frozenCommission,rsp.closeProfit,rsp.positionProfit,rsp.preBalance,rsp.balance,rsp.withdrawQuota,rsp.reserve,rsp.tradingDay,rsp.settlementID,rsp.credit,rsp.mortgage,rsp.exchangeMargin,rsp.deliveryMargin,rsp.exchangeDeliveryMargin,rsp.reserveBalance,rsp.currencyID,rsp.preFundMortgageIn,rsp.preFundMortgageOut,rsp.fundMortgageIn,rsp.fundMortgageOut,rsp.fundMortgageAvailable,rsp.mortgageableFund,rsp.specProductMargin,rsp.specProductFrozenMargin,rsp.specProductCommission,rsp.specProductFrozenCommission,rsp.specProductPositionProfit,rsp.specProductCloseProfit,rsp.specProductPositionProfitByAlg,rsp.specProductExchangeMargin,rsp.bizType,rsp.frozenSwap,rsp.remainSwap)
    }
}

/**
 * 查询资金响应
 */
class RspTradingAccount(var rspField:RspTradingAccountField?,rspInfo: RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 委托记录数据
 */
class RspOrderField(var brokerID: String,var investorID: String,var instrumentID:String,var orderRef:String,var userID:String,var orderPriceType:Char,var direction:Char,var combOffsetFlag:String,var combHedgeFlag:String,var limitPrice:Double,var volumeTotalOriginal:Int,var timeCondition:Char,var GTDDate:String,var volumeCondition:Char,var minVolume:Int,var contingentCondition:Char,var stopPrice:Double,var forceCloseReason:Char,var isAutoSuspend:Int,var businessUnit:String,var requestID:Int,var orderLocalID:String,var exchangeID:String,var participantID:String,var clientID:String,var exchangeInstID:String,var traderID:String,var installID:Int,var orderSubmitStatus:Char,var notifySequence:Int,var tradingDay:String,var settlementID:Int,var orderSysID:String,var orderSource:Char,var orderStatus:Char,var orderType:Char,var volumeTraded:Int,var volumeTotal:Int,var insertDate:String,var insertTime:String,var activeTime:String,var suspendTime:String,var updateTime:String,var cancelTime:String,var activeTraderID:String,var clearingPartID:String,var sequenceNo:Int,var frontID:Int,var sessionID:Int,var userProductInfo:String,var statusMsg:String,var userForceClose:Int,var activeUserID:String,var brokerOrderSeq:Int,var relativeOrderSysID:String,var ZCETotalTradedVolume:Int,var isSwapOrder:Int,var branchID:String,var investUnitID:String,var accountID: String,var currencyID: String,var ipAddress:String,var macAddress:String)
    : DiffComparable<RspOrderField> {
    /**
     * 判断内容是否发生变化
     */
    override fun compare(obj : RspOrderField): Boolean {
        return (obj.investorID == investorID && obj.instrumentID == instrumentID && obj.frontID == frontID && obj.sessionID == sessionID && obj.orderRef == orderRef && obj.volumeTraded == volumeTraded && obj.volumeTotalOriginal == volumeTotalOriginal)
    }

    /**
     * 判断是不是同一个对象
     */
    override fun equals(other: Any?): Boolean {
        if(other == null) return  false
        if(other === this) return true
        if(other !is RspOrderField){
            return false
        }
        val o = other!!
        return (o.investorID == investorID && o.instrumentID == instrumentID && o.frontID == frontID && o.sessionID == sessionID && o.orderRef == orderRef)
    }

    companion object{
        fun fromCTPAPI(rsp:CThostFtdcOrderField?):RspOrderField? =
            if(rsp == null) null
            else RspOrderField(rsp.brokerID,rsp.investorID,rsp.instrumentID,rsp.orderRef,rsp.userID,rsp.orderPriceType,rsp.direction,rsp.combOffsetFlag,rsp.combHedgeFlag,rsp.limitPrice,rsp.volumeTotalOriginal,rsp.timeCondition,rsp.gtdDate,rsp.volumeCondition,rsp.minVolume,rsp.contingentCondition,rsp.stopPrice,rsp.forceCloseReason,rsp.isAutoSuspend,rsp.businessUnit,rsp.requestID,rsp.orderLocalID,rsp.exchangeID,rsp.participantID,rsp.clientID,rsp.exchangeInstID,rsp.traderID,rsp.installID,rsp.orderSubmitStatus,rsp.notifySequence,rsp.tradingDay,rsp.settlementID,rsp.orderSysID,rsp.orderSource,rsp.orderStatus,rsp.orderType,rsp.volumeTraded,rsp.volumeTotal,rsp.insertDate,rsp.insertTime,rsp.activeTime,rsp.suspendTime,rsp.updateTime,rsp.cancelTime,rsp.activeTraderID,rsp.clearingPartID,rsp.sequenceNo,rsp.frontID,rsp.sessionID,rsp.userProductInfo,rsp.statusMsg,rsp.userForceClose,rsp.activeUserID,rsp.brokerOrderSeq,rsp.relativeOrderSysID,rsp.zceTotalTradedVolume,rsp.isSwapOrder,rsp.branchID,rsp.investUnitID,rsp.accountID,rsp.currencyID,rsp.ipAddress,rsp.macAddress)
    }
}

/**
 * 查询委托响应
 */
class RspQryOrder(var rspField:RspOrderField?,rspInfo: RspInfoField,isLast: Boolean):APIRspTradeObj(rspInfo,isLast)

/**
 * 委托回报
 */
class RtnOrder(var rspField:RspOrderField)

/**
 * 报单响应数据
 */
data class RspOrderInsertField(var brokerID: String,var investorID: String,var instrumentID: String,var orderRef: String,var userID: String,var orderPriceType: Char,var direction: Char,var combOffsetFlag: String,var combHedgeFlag: String,var limitPrice: Double,var volumeTotalOriginal: Int,var timeCondition: Char,var GTDDate: String,var volumeCondition: Char,var contingentCondition: Char,var stopPrice: Double,var forceCloseReason: Char,var isAutoSuspend: Int,var businessUnit: String,var requestID: Int,var userForceClose: Int,var isSwapOrder: Int,var exchangeID: String,var investUnitID: String,var accountID: String,var currencyID: String,var clientID: String,var ipAddress: String,var macAddress: String){
    companion object{
        fun fromCTPAPI(rsp:CThostFtdcInputOrderField?):RspOrderInsertField? =
            if(rsp == null) null
            else RspOrderInsertField(rsp.brokerID,rsp.investorID,rsp.instrumentID,rsp.orderRef,rsp.userID,rsp.orderPriceType,rsp.direction,rsp.combOffsetFlag,rsp.combHedgeFlag,rsp.limitPrice,rsp.volumeTotalOriginal,rsp.timeCondition,rsp.gtdDate,rsp.volumeCondition,rsp.contingentCondition,rsp.stopPrice,rsp.forceCloseReason,rsp.isAutoSuspend,rsp.businessUnit,rsp.requestID,rsp.userForceClose,rsp.isSwapOrder,rsp.exchangeID,rsp.investUnitID,rsp.accountID,rsp.currencyID,rsp.clientID,rsp.ipAddress,rsp.macAddress)
    }
}

/**
 * 报单响应
 */
class RspOrderInsert(var rspField:RspOrderInsertField?,rspInfo: RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 撤单响应数据
 */
data class RspOrderActionField(var brokerID: String,var investorID: String,var orderActionRef:Int,var orderRef:String, var requestID: Int,var frontID: Int,var sessionID: Int,var exchangeID: String,var orderSysID: String,var actionFlag:Char,var limitPrice: Double,var volumeChange:Int,var userID:String,var instrumentID:String,var investUnitID:String,var ipAddress: String,var macAddress: String){
    companion object{
        fun fromCTPAPI(rsp:CThostFtdcInputOrderActionField?):RspOrderActionField? =
            if(rsp == null) null
            else RspOrderActionField(rsp.brokerID,rsp.investorID,rsp.orderActionRef,rsp.orderRef,rsp.requestID,rsp.frontID,rsp.sessionID,rsp.exchangeID,rsp.orderSysID,rsp.actionFlag,rsp.limitPrice,rsp.volumeChange,rsp.userID,rsp.instrumentID,rsp.investUnitID,rsp.ipAddress,rsp.macAddress)
    }
}

/**
 * 撤单响应
 */
class RspOrderAction(var rspField:RspOrderActionField?,rspInfo: RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 成交数据
 */
class RspTradeField(var brokerID: String,var investorID: String,var instrumentID: String,var orderRef: String,var userID: String,var exchangeID: String,var tradeID:String,var direction: Char,var orderSysID: String,var participantID: String,var clientID: String,var tradingRole:Char,var exchangeInstID:String,var offsetFlag:Char,var hedgeFlag:Char,var price:Double,var volume:Int,var tradeDate:String,var tradeTime:String,var tradeType:Char,var priceSource:Char,var traderID: String,var orderLocalID: String,var clearingPartID: String,var businessUnit: String,var sequenceNo: Int,var tradingDay: String,var settlementID: Int,var brokerOrderSeq: Int,var tradeSource: Char,var investUnitID: String)
    :DiffComparable<RspTradeField>{
    override fun compare(obj: RspTradeField): Boolean {
        return obj.investorID == investorID && obj.tradeID == tradeID && obj.instrumentID == obj.instrumentID
    }

    override fun equals(other: Any?): Boolean {
        if(other === this) return true
        if(other !is RspTradeField) return false
        return other.investorID == investorID && other.tradeID == tradeID && other.instrumentID == other.instrumentID
    }

    companion object{
        fun fromCTPAPI(rsp:CThostFtdcTradeField?):RspTradeField? =
            if(rsp == null) null
            else RspTradeField(rsp.brokerID,rsp.investorID,rsp.instrumentID,rsp.orderRef,rsp.userID,rsp.exchangeID,rsp.tradeID,rsp.direction,rsp.orderSysID,rsp.participantID,rsp.clientID,rsp.tradingRole,rsp.exchangeInstID,rsp.offsetFlag,rsp.hedgeFlag,rsp.price,rsp.volume,rsp.tradeDate,rsp.tradeTime,rsp.tradeType,rsp.priceSource,rsp.traderID,rsp.orderLocalID,rsp.clearingPartID,rsp.businessUnit,rsp.sequenceNo,rsp.tradingDay,rsp.settlementID,rsp.brokerOrderSeq,rsp.tradeSource,rsp.investUnitID)
    }
}

/**
 * 查询成交响应
 */
class RspQryTrade(var rspField:RspTradeField?,rspInfo: RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)

/**
 * 成交回报
 */
class RtnTrade(var rspField:RspTradeField)

/**
 * 持仓明细数据
 */
data class RspPositionDetailField(var instrumentID: String,var brokerID: String,var investorID: String,var hedgeFlag: Char,var direction: Char,var openDate:String,var tradeID:String,var volume:Int,var openPrice:Double,var tradingDay: String,var settlementID: Int,var tradeType: Char,var combInstrumentID:String,var exchangeID: String,var closeProfitByDate:Double,var closeProfitByTrade:Double,var positionProfitByDate:Double,var positionProfitByTrade: Double,var margin:Double,var exchangeMargin: Double,var marginRateByMoney:Double,var marginRateByVolume:Double,var lastSettlementPrice:Double,var settlementPrice:Double,var closeVolume:Int,var closeAmount:Double,var timeFirstVolume:Int,var investUnitID: String){
    companion object{
        fun fromCTPAPI(rsp:CThostFtdcInvestorPositionDetailField?):RspPositionDetailField? =
            if(rsp == null) null
            else RspPositionDetailField(rsp.instrumentID,rsp.brokerID,rsp.investorID,rsp.hedgeFlag,rsp.direction,rsp.openDate,rsp.tradeID,rsp.volume,rsp.openPrice,rsp.tradingDay,rsp.settlementID,rsp.tradeType,rsp.combInstrumentID,rsp.exchangeID,rsp.closeProfitByDate,rsp.closeProfitByTrade,rsp.positionProfitByDate,rsp.positionProfitByTrade,rsp.margin,rsp.exchMargin,rsp.marginRateByMoney,rsp.marginRateByVolume,rsp.lastSettlementPrice,rsp.settlementPrice,rsp.closeVolume,rsp.closeAmount,rsp.timeFirstVolume,rsp.investUnitID)
    }
}

/**
 * 查询持仓明细响应
 */
class RspQryPositionDetail(var rspField:RspPositionDetailField?,rspInfo: RspInfoField,bIsLast: Boolean):APIRspTradeObj(rspInfo,bIsLast)