package com.fsh.common.widget.mpchart.component

import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.utils.Utils

class LegendComponent : Legend(){
    init {
        this.mTextSize = Utils.convertDpToPixel(10f)
        this.mXOffset = Utils.convertDpToPixel(5f)
        this.mYOffset = Utils.convertDpToPixel(-1f)
    }
}