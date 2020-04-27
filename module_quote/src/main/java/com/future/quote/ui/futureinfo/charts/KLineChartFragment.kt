package com.future.quote.ui.futureinfo.charts

import android.os.Bundle
import com.future.quote.enums.FutureChartType

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

    override fun initViews() {

    }
}