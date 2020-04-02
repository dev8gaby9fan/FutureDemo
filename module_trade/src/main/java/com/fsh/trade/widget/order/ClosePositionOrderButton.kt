package com.fsh.trade.widget.order

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

    override fun checkOrderParams() {

    }
}