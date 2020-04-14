package com.future.trade.bean.position

import com.future.trade.bean.RspPositionDetailField
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType

/**
 * 投机套保仓
 */
class HedgePosition : SimplePosition(){
    private val positionDetailMap:MutableMap<String, RspPositionDetailField> = HashMap(10)

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

}