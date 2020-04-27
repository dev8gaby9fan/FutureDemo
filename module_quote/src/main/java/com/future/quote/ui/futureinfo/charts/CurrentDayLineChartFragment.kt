package com.future.quote.ui.futureinfo.charts

import android.os.Bundle

/**
 * 分时界面
 */
class CurrentDayLineChartFragment : BaseChartsFragment(){
    companion object{
        fun newInstance(instrumentId:String):CurrentDayLineChartFragment{
            return CurrentDayLineChartFragment().apply {
                arguments = Bundle().apply { putString(INSTRUMENT_ID,instrumentId) }
            }
        }
    }

    override fun initViews() {

    }
}