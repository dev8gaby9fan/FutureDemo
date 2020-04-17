package com.future.trade.widget.order

import android.content.Context
import android.util.AttributeSet
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.Omits
import com.future.trade.R
import com.future.trade.bean.CTPOrderInsertField
import com.future.trade.bean.IOrderInsertField
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.InstrumentPosition
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPOrderPriceType
import com.future.trade.model.SupportTransactionOrderPrice
import com.future.trade.repository.TradeApiProvider

class ClosePositionOrderButton : OrderButton{
    private var position: Position? = null
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setTradePosition(pos:Position?,pType:SupportTransactionOrderPrice){
        val instrument = ARouterUtils.getQuoteService().getInstrumentById(pos?.getExchangeId() + "." + pos?.getInstrumentId())
        if(pos == null){
            isEnabled = false
            setTransactionInfo(instrument,pType,null,CTPCombOffsetFlag.Close)
            return
        }
        this.position = pos
        var orderDir:CTPDirection? = null
        if(pos !is InstrumentPosition && pos.getDirection() == CTPDirection.Sell){
            orderDir = CTPDirection.Buy
        }else if(pos !is InstrumentPosition && pos.getDirection() == CTPDirection.Buy){
            orderDir = CTPDirection.Sell
        }
        setTransactionInfo(instrument,pType,orderDir,CTPCombOffsetFlag.Close)
    }

    /**
     * 检查下单手数是否正确
     */
    override fun checkOrderVolume(volume: String?) {
        super.checkOrderVolume(volume)
        //判断输入的手数是否大于可用手数了
        require(volume!!.toInt() <= position!!.getAvailable()){resources.getString(R.string.notice_volume_gt_available).format(volume.toInt(),position?.getAvailable())}
    }

    override fun createOrderField(volume: Int): List<IOrderInsertField> {
        return position!!.getCloseOrderFields(volume,priceType,textOrderPrice.toDouble())
    }

}