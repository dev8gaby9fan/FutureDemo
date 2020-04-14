package com.future.trade.bean.position

import com.future.trade.bean.*
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection

class SymblePosition(private var longPosition:Position?,private var shortPosition:Position?) : SimplePosition() {

    override fun onRspPositionDetail(rsp: RspPositionDetailField) {
        if(rsp.direction == CTPDirection.Buy.direction){
            if(longPosition == null){
                longPosition = DirectionPosition(null,null)
            }
            longPosition?.onRspPositionDetail(rsp)
        }else{
            if(shortPosition == null){
                shortPosition = DirectionPosition(null,null)
            }
            shortPosition?.onRspPositionDetail(rsp)
        }
    }

    override fun onRspQryOrder(rsp: RspQryOrder): Pair<RspQryOrder, Boolean> {
        //开仓不处理
        if(rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rsp,true)
        }
        //买操作，需要空仓来处理
        if(rsp.rspField?.direction == CTPDirection.Buy.text[0]){
            if(shortPosition != null){
                return shortPosition!!.onRspQryOrder(rsp)
            }
            return Pair(rsp,false)
        }
        //卖操作，多仓来处理
        if(longPosition != null){
            return longPosition!!.onRspQryOrder(rsp)
        }
        return Pair(rsp,false)
    }

    override fun onRtnOrder(rtn: RtnOrder): Pair<RtnOrder, Boolean> {
        //开仓不处理
        if(rtn.rspField.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rtn,true)
        }
        //买操作，需要空仓来处理
        if(rtn.rspField.direction == CTPDirection.Buy.text[0]){
            if(shortPosition != null){
                return shortPosition!!.onRtnOrder(rtn)
            }
            return Pair(rtn,false)
        }
        //卖操作，多仓来处理
        if(longPosition != null){
            return longPosition!!.onRtnOrder(rtn)
        }
        return Pair(rtn,false)
    }

    override fun onRspOrderInsert(rsp: RspOrderInsert): Pair<RspOrderInsert, Boolean> {
        //开仓不处理
        if(rsp.rspField?.combOffsetFlag == CTPCombOffsetFlag.Open.text){
            return Pair(rsp,true)
        }
        //买操作，需要空仓来处理
        if(rsp.rspField?.direction == CTPDirection.Buy.text[0]){
            if(shortPosition != null){
                return shortPosition!!.onRspOrderInsert(rsp)
            }
            return Pair(rsp,false)
        }
        //卖操作，多仓来处理
        if(longPosition != null){
            return longPosition!!.onRspOrderInsert(rsp)
        }
        return Pair(rsp,false)
    }

    override fun onRspOrderAction(rsp: RspOrderAction): Pair<RspOrderAction, Boolean> {
        //撤单操作响应里面找不到是不是开仓的委托，所以需要到持仓里面去处理
        if(longPosition != null){
            return longPosition!!.onRspOrderAction(rsp)
        }
        if(shortPosition != null){
            return shortPosition!!.onRspOrderAction(rsp)
        }
        return Pair(rsp,false)
    }


    override fun onRtnTrade(rtn: RtnTrade): Pair<RtnTrade, Boolean> {
        //开仓
        if(rtn.rspField.offsetFlag == CTPCombOffsetFlag.Open.offset){
            //买开仓，就是多仓，需要多仓处理
            if(rtn.rspField.direction == CTPDirection.Buy.direction){
                if(longPosition == null){
                    longPosition = DirectionPosition(null,null)
                }
                longPosition!!.onRtnTrade(rtn)
            }else{
                if(shortPosition == null){
                    shortPosition = DirectionPosition(null,null)
                }
                shortPosition!!.onRtnTrade(rtn)
            }
        }else{
            //平仓操作,买开是平空仓，卖开始平多仓
            if(rtn.rspField.direction == CTPDirection.Buy.direction){
                shortPosition?.onRtnTrade(rtn)
            }else{
                longPosition?.onRtnTrade(rtn)
            }
        }
        return Pair(rtn,true)
    }

}