package com.fsh.common.model

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: ARouter路由地址
 *
 */

object ArouterPath {
    //市场界面
    const val PAGE_QUOTE_MAIN = "/quote/main"
    //K线 TODO 暂时没有
    const val PAGE_QUOTE_KLINE = "/quote/kline"
    //搜索合约
    const val PAGE_INS_SEARCH = "/quote/search"
    //交易
    const val PAGE_TRADE_MAIN = "/trade/main"
    //持仓
    const val PAGE_TRADE_POSITION = "/trade/position"
    //委托
    const val PAGE_TRADE_ORDER = "/trade/order"
    //挂单
    const val PAGE_TRADE_WITH_DRAW = "/trade/withdraw"
    //成交
    const val PAGE_TRADE_TRADE = "/trade/trade"
    //登录界面
    const val PAGE_TRADE_LOGIN = "/trade/login"

    const val SERVICE_QUOTE = "/quote/service"

    const val SERVICE_TRADe = "/trade/service"
}