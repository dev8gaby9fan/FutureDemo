package com.future.quote.ui.futureinfo.charts

import android.util.Log
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.ext.viewModelOf
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.viewmodel.FutureChartViewModel

abstract class BaseChartsFragment : BaseLazyFragment(){
    protected var viewMode: FutureChartViewModel? = null
    protected var viewWidth:Int = 500
    companion object{
        const val INSTRUMENT_ID = "INSTRUMENT_ID"
        const val CHARTTYPE = "CHARTTYPE"
    }

    override fun layoutRes(): Int = R.layout.quote_fragment_charts


    override fun lazyLoading() {
        initViews()
        initData()
    }

    abstract fun initViews()

    private  fun initData(){
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        val chartType:FutureChartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        viewMode = viewModelOf<FutureChartViewModel>().value.also {
            Log.d(javaClass.simpleName,"initData setChart $instrument ${chartType.duration}")
            it.setChart(instrument,chartType,viewWidth)
        }
    }
}