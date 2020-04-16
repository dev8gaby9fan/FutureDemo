package com.future.trade.repository.tradeapi

import com.sfit.ctp.thosttraderapi.*

abstract class CTPRequestFrame<T>(val frameType:CTPRequestFrameType,val frame:T?) {

}

enum class CTPRequestFrameType{
    UserLogout,
    ConfirmSettlement,
    QryOrder,
    QryTrade,
    QryTradingAccount,
    QryPositionDetail,
    OrderInsert,
    OrderAction
}

/**
 * 登出请求
 */
class CTPUserLogoutFrame(frame: CThostFtdcUserLogoutField?) : CTPRequestFrame<CThostFtdcUserLogoutField>(CTPRequestFrameType.UserLogout,frame)

/**
 * 确认结算单请求
 */
class CTPConfirmSettlement(frame: CThostFtdcSettlementInfoConfirmField) : CTPRequestFrame<CThostFtdcSettlementInfoConfirmField>(CTPRequestFrameType.ConfirmSettlement,frame)

/**
 * 查询委托
 */
class CTPQryOrder(frame: CThostFtdcQryOrderField) : CTPRequestFrame<CThostFtdcQryOrderField>(CTPRequestFrameType.QryOrder,frame)

/**
 * 查询成交
 */
class CTPQryTrade(frame: CThostFtdcQryTradeField) : CTPRequestFrame<CThostFtdcQryTradeField>(CTPRequestFrameType.QryTrade,frame)

/**
 * 查询资金
 */
class CTPQryTradingAccount(frame:CThostFtdcQryTradingAccountField) : CTPRequestFrame<CThostFtdcQryTradingAccountField>(CTPRequestFrameType.QryTradingAccount,frame)

/**
 * 查询持仓明细
 */
class CTPQryPositionDetail(frame:CThostFtdcQryInvestorPositionDetailField) : CTPRequestFrame<CThostFtdcQryInvestorPositionDetailField>(CTPRequestFrameType.QryPositionDetail,frame)

/**
 * 委托
 */
class CTPOrderInsert(frame:CThostFtdcInputOrderField) : CTPRequestFrame<CThostFtdcInputOrderField>(CTPRequestFrameType.OrderInsert,frame)

/**
 * 撤单
 */
class CTPOrderAction(frame:CThostFtdcInputOrderActionField) : CTPRequestFrame<CThostFtdcInputOrderActionField>(CTPRequestFrameType.OrderAction,frame)