package com.future.quote.ui.futureinfo.charts

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.ext.viewModelOf
import com.fsh.common.util.NumberUtils
import com.fsh.common.widget.mpchart.CombinedChartView
import com.fsh.common.widget.mpchart.component.ValueFormatterComponent
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.FutureChartViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.quote_fragment_current_day_line_chart.*

abstract class BaseChartsFragment : BaseLazyFragment(){
    private val disposables:CompositeDisposable = CompositeDisposable()
    protected var viewMode: FutureChartViewModel? = null
    protected var viewWidth:Int = 500
    protected lateinit var firstChartView:CombinedChartView
    protected lateinit var secondChartView: CombinedChartView
    //------------ 颜色 -------------------
    protected var quoteRed:Int = 0
    protected var quoteGreen:Int = 0
    protected var quoteWhite:Int = 0
    protected var highlightColor:Int = 0
    protected var preSettlementPrice:Float = Float.NaN
    protected var textColor:Int = 0

    protected var calendar:Calendar = Calendar.getInstance()
    protected var priceTick:String? = null
    protected val barColors:List<Int> = ArrayList()

    companion object{
        const val INSTRUMENT_ID = "INSTRUMENT_ID"
        const val CHARTTYPE = "CHARTTYPE"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstChartView = view.findViewById(R.id.chart_first)
        secondChartView = view.findViewById(R.id.chart_second)
    }


    override fun lazyLoading() {
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        priceTick = QuoteInfoMgr.mgr.getInstrument(instrument)?.priceTick
        initViews()
        initData()
    }

    protected open fun initViews(){
        quoteRed = resources.getColor(R.color.quote_red)
        quoteGreen = resources.getColor(R.color.quote_green)
        quoteWhite = quoteGreen
        highlightColor = resources.getColor(R.color.color_highlight)
        textColor = resources.getColor(R.color.color_text_gray)
    }

    private  fun initData(){
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        val chartType:FutureChartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        val quoteEntity = QuoteInfoMgr.mgr.getQuoteEntity(instrument)
        preSettlementPrice = quoteEntity?.pre_settlement?.toFloat() ?: Float.NaN
        viewMode = viewModelOf<FutureChartViewModel>().value.also { it ->
            Log.d(javaClass.simpleName,"initData setChart $instrument ${chartType.duration}")
            it.setChart(instrument,chartType,viewWidth)
            disposables.add(it.chartData.subscribe {drawChartLines(it)})
        }
    }

    abstract fun drawChartLines(kLineEntity: KLineEntity)

    protected fun generateBarDataSet(enties:List<BarEntry>,lable:String,colors:List<Int>,isHighlight:Boolean = false):IBarDataSet{
        val barDataSet = BarDataSet(enties,lable)
        barDataSet.colors = colors
        barDataSet.barBorderWidth = 0F
        barDataSet.setDrawValues(false)
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT
        barDataSet.isHighlightEnabled = isHighlight
        if(isHighlight){
            barDataSet.highLightColor = highlightColor
        }
        return barDataSet
    }

    protected fun refreshLegend(index:Int){
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        val chartType:FutureChartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        val klineEntity = DiffEntity.getInstrumentKLineEntity(instrument)
        val dataEntityMap = klineEntity[chartType.duration.toString()]?.data?:return
        val dataEntity = dataEntityMap[index.toString()] ?: return
        val secondLegendEntries = ArrayList<LegendEntry>(2)
        secondLegendEntries.add(LegendEntry("VOL:${dataEntity.volume}",Legend.LegendForm.NONE,
            Float.NaN, Float.NaN,null,resources.getColor(R.color.quote_white)))
        secondLegendEntries.add(LegendEntry("OI:${dataEntity.close_oi}",Legend.LegendForm.NONE,
            Float.NaN, Float.NaN,null,quoteRed))
        secondChartView.legend.setCustom(secondLegendEntries)
        secondChartView.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        secondChartView.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    }

    /**
     * Y轴Label格式化工具
     */
    class ChartViewYAxisValueFormatter(private val priceTick:String?) : ValueFormatterComponent(){
        override fun getFormattedValue(value: Float): String {
            return NumberUtils.formatNum(value.toString(),priceTick)
        }
    }
}