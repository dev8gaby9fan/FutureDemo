package com.future.trade.repository.tradeapi

import com.future.trade.bean.*
import com.sfit.ctp.thosttraderapi.*

/**
 * 交易
 */
abstract class TradeEvent(var eventType:TradeEventType)

/** 初始化TradeAPI*/
data class InitApiEvent(var broker:BrokerConfig?):TradeEvent(TradeEventType.InitApi)

/** 柜台连接成功*/
data class FrontConnectedEvent(var broker:BrokerConfig?):TradeEvent(TradeEventType.FrontConnected)

/** 柜台断开连接*/
data class FrontDisconnectedEvent(var broker:BrokerConfig?):TradeEvent(TradeEventType.FrontDisconnected)

/** 请求认证*/
data class ReqAuthenEvent(var broker:BrokerConfig?):TradeEvent(TradeEventType.ReqAuthen)

/** 认证响应*/
data class RspAuthenEvent(var rsp:RspAuthencate):TradeEvent(TradeEventType.RspAuthen)

/** 请求登录*/
data class ReqUserLoginEvent(var account:TradeAccountConfig?,var broker: BrokerConfig?):TradeEvent(TradeEventType.ReqUserLogin)

/** 登录响应*/
data class RspUserLoginEvent(var rsp:RspUserLogin):TradeEvent(TradeEventType.RspUserLogin)

/** 登出响应*/
data class RspUserLogoutEvent(var rsp:RspUserLogout): TradeEvent(TradeEventType.RspUserLogout)

/** 查询结算单信息*/
data class ReqQrySettlementEvent(var account:TradeAccountConfig?,var broker: BrokerConfig?):TradeEvent(TradeEventType.ReqSettlementInfo)

/** 结算单信息响应*/
data class RspQrySettlementEvent(var rsp:RspQrySettlementInfo):TradeEvent(TradeEventType.RspSettlementInfo)

/** 请求确认结算单信息*/
data class ReqConfirmSettlementEvent(var account:TradeAccountConfig?,var broker: BrokerConfig?):TradeEvent(TradeEventType.ReqConfirmSettlement)

data class RspQryConfirmSettlementEvent(var rsp:RspQrySettlementInfoConfirm):TradeEvent(TradeEventType.RspQryConfirmSettlement)

/** 确认结算单响应*/
data class RspConfirmSettlementEvent(var rsp:RspConfirmSettlementInfo):TradeEvent(TradeEventType.RspConfirmSettlement)

/** 查询资金信息响应*/
data class RspQryTradingAccountEvent(var rsp:RspTradingAccount):TradeEvent(TradeEventType.RspTradingAccount)

/** 查询委托记录响应*/
data class RspQryOrderEvent(var rsp:RspQryOrder):TradeEvent(TradeEventType.RspQryOrder)

/** 委托回报*/
data class RtnOrderEvent(var rtn:RtnOrder):TradeEvent(TradeEventType.RtnOrder)

/** 报单响应*/
data class RspOrderInsertEvent(var rsp:RspOrderInsert):TradeEvent(TradeEventType.RspOrderInsert)

/** 撤单响应*/
data class RspOrderActionEvent(var rsp:RspOrderAction):TradeEvent(TradeEventType.RspOrderAction)

/** 查询成交记录响应*/
data class RspQryTradeEvent(var rsp:RspQryTrade):TradeEvent(TradeEventType.RspQryTrade)

/** 成交回报*/
data class RtnTradeEvent(var rtn:RtnTrade):TradeEvent(TradeEventType.RtnTrade)

/** 查询持仓明细响应*/
data class RspQryPositionDetailEvent(var rsp:RspQryPositionDetail):TradeEvent(TradeEventType.RspQryPositionDetail)

enum class TradeEventType{
    //初始化API
    InitApi,
    //柜台链接成功
    FrontConnected,
    //柜台断开
    FrontDisconnected,
    //请求认证
    ReqAuthen,
    //认证响应
    RspAuthen,
    //请求登录
    ReqUserLogin,
    //登录响应
    RspUserLogin,
    //登出响应
    RspUserLogout,
    //请求结算单信息
    ReqSettlementInfo,
    //结算单信息响应
    RspSettlementInfo,
    //请求确认结算单
    ReqConfirmSettlement,
    //确认结算单响应
    RspConfirmSettlement,
    //查询结算单确认记录响应
    RspQryConfirmSettlement,
    //资金响应
    RspTradingAccount,
    //持仓明细响应
    RspQryPositionDetail,
    //委托记录响应
    RspQryOrder,
    //委托回报
    RtnOrder,
    //报单响应
    RspOrderInsert,
    //撤单响应
    RspOrderAction,
    //成交响应
    RspQryTrade,
    //成交回报
    RtnTrade,
}