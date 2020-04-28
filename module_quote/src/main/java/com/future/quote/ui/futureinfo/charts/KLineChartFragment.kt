package com.future.quote.ui.futureinfo.charts

import android.os.Bundle
import com.fsh.common.widget.mpchart.CombinedChartView
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.model.KLineEntity

/**
 * K线界面
 */
class KLineChartFragment : BaseChartsFragment(){
    companion object{
        fun newInstance(instrumentId:String,type: FutureChartType):KLineChartFragment{
            return KLineChartFragment().apply {
                arguments = Bundle().apply {
                    putString(INSTRUMENT_ID,instrumentId)
                    putSerializable(CHARTTYPE,type)
                }
            }
        }
    }

    override fun layoutRes(): Int = R.layout.quote_fragment_candle_chart

    override fun initViews() {
        super.initViews()
    }

    override fun drawChartLines(kLineEntity: KLineEntity) {
    }
}