package com.future.trade.bean.position

import com.future.trade.bean.*
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType

interface PositionDataHandler{
    /**
     * 处理持仓明细响应数据
     */
    fun onRspPositionDetail(rsp:RspPositionDetailField)
    /**
     * 委托查询响应处理
     * RspQryOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRspQryOrder(rsp:RspQryOrder):Pair<RspQryOrder,Boolean>

    /**
     * 委托回报
     * RtnOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRtnOrder(rtn:RtnOrder):Pair<RtnOrder,Boolean>

    /**
     * 委托响应处理
     * RspOrderInsert 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRspOrderInsert(rsp:RspOrderInsert):Pair<RspOrderInsert,Boolean>
    /**
     * 撤单响应处理
     * RspOrderInsert 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRspOrderAction(rsp:RspOrderAction):Pair<RspOrderAction,Boolean>

    /**
     * 成交回报处理
     * RtnOrder 剩下需要处理的数据
     * true 处理完成，否则没有处理完成,需要其他类型持仓处理
     */
    fun onRtnTrade(rtn:RtnTrade):Pair<RtnTrade,Boolean>
}

/**
 * 持仓数据
 */
interface Position : PositionDataHandler{
    //持仓手数
    fun getPosition():Int
    //可用手数
    fun getAvailable():Int
    //开仓成本
    fun getOpenCost():Double
    //获取持仓成本
    fun getPositionCost():Double

    //获取持仓盈亏等同于逐日盈亏
    fun getPositionProfit():Double
    //获取开仓盈亏等同于逐笔盈亏
    fun getOpenPositionProfit():Double
    //持仓方向
    fun getDirection():CTPDirection
    //投机套保仓位类型
    fun getHedgeType():CTPHedgeType
}
abstract class SimplePosition : Position{



     override fun getPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAvailable(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionCost(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenPositionProfit(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDirection(): CTPDirection {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHedgeType(): CTPHedgeType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

     /**
      * =====================================数据处理===============================================
      */

     override fun onRspPositionDetail(rsp: RspPositionDetailField) {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRspOrderAction(rsp: RspOrderAction): Pair<RspOrderAction, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }
}







