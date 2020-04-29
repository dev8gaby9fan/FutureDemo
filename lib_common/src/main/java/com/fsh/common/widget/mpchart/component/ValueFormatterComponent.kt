package com.fsh.common.widget.mpchart.component

import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.ValueFormatter

open class ValueFormatterComponent : ValueFormatter() {
    fun getCandleLabelLow(candleEntry: CandleEntry): String = getFormattedValue(candleEntry.low)
}