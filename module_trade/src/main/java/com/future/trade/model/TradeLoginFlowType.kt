package com.future.trade.model

import com.future.trade.repository.tradeapi.TradeEvent

/**
 * 交易登录事件流
 */
enum class TradeLoginFlowType(val flowName:String,val isSuccess:Boolean) {
    FrontConnected("柜台连接成功",true),
    FrontDisconnected("柜台断开连接",false),
    AuthenSuccess("认证通过",true),
    AuthenFail("认证失败",false),
    ReqUserLogin("请求登录",true),
    RspUserLoginSuccess("登录响应",true),
    RspUserLoginFail("登录失败",false),
    RspQryConfirmSettlementNoData("没有确认结算单",true),
    RspQryConfirmSettlementData("今日有确认结算单",true),
    RspSettlementInfo("结算单响应数据",true),
    RspConfirmSettlementInfo("确认结算单响应",true),
}

data class TradeLoginFlowEvent(val flowType:TradeLoginFlowType,val event:TradeEvent)

