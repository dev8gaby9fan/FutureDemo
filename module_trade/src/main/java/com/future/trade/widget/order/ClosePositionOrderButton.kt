package com.future.trade.widget.order

import android.content.Context
import android.util.AttributeSet

class ClosePositionOrderButton : OrderButton{


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 检查下单手数是否正确
     */
    override fun checkOrderVolume(volume: String?) {
        super.checkOrderVolume(volume)
        //TODO 判断手数是否超过持仓手数
    }
}