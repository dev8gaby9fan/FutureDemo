package com.fsh.trade.model

import com.fsh.trade.repository.tradeapi.TradeEvent

/**
 * 交易登录事件流
 */
enum class TradeLoginFlowType(val flowName:String,val isSuccess:Boolean,val isFinalFlow:Boolean) {
    FrontConnected("柜台连接成功",true,false),
    FrontDisconnected("柜台断开连接",false,true),
    ReqAuthen("请求认证",true,true),
    AuthenSuccess("认证通过",true,false),
    AuthenFail("认证失败",false,true),
    ReqUserLogin("请求登录",true,false),
    RspUserLogin("登录响应",true,true)
}

data class TradeLoginFlowEvent(val flowType:TradeLoginFlowType,val event:TradeEvent)

