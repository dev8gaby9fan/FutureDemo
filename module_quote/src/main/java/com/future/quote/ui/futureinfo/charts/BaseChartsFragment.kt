package com.future.quote.ui.futureinfo.charts

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.ext.viewModelOf
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.fsh.common.widget.mpchart.CombinedChartView
import com.fsh.common.widget.mpchart.component.ValueFormatterComponent
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.FutureChartViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.reactivex.disposables.CompositeDisposable

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
    protected var colorAvgLine:Int = 0

    protected var calendar:Calendar = Calendar.getInstance()
    protected val xValues: SparseArray<String> = SparseArray()
    protected var priceTick:String? = null
    protected val barColors:List<Int> = ArrayList()
    protected lateinit var instrumentId:String
    protected lateinit var chartType: FutureChartType
    companion object{
        const val INSTRUMENT_ID = "INSTRUMENT_ID"
        const val CHARTTYPE = "CHARTTYPE"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstChartView = view.findViewById(R.id.chart_first)
        secondChartView = view.findViewById(R.id.chart_second)
        firstChartView.setOnChartValueSelectedListener(ChartValueSelectedListener(firstChartView,secondChartView))
        secondChartView.setOnChartValueSelectedListener(ChartValueSelectedListener(firstChartView,secondChartView))
        initViews()
    }

    inner class ChartValueSelectedListener(var source: BarLineChartBase<*>, var combinedChart:BarLineChartBase<*>) : OnChartValueSelectedListener{
        override fun onNothingSelected() {
            combinedChart.highlightValue(null)
        }

        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if(h == null) return
           val y = if(source == firstChartView){
                h.drawY - firstChartView.height
            }else{
                h.drawY + firstChartView.height
            }
            val h1 = combinedChart.getHighlightByTouchPoint(h.xPx,h.yPx)
            h1?.setDraw(h.x,y)
            combinedChart.highlightValue(h1)
//            val h2 = secondChartView.getHighlightByTouchPoint(h.xPx,h.yPx)
//            val yBottom = if(source == firstChartView){
//
//            }
//            h2?.setDraw(h.x,h.drawY)
        }

    }


    override fun lazyLoading() {
        instrumentId = arguments?.getString(INSTRUMENT_ID)!!
        chartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        priceTick = QuoteInfoMgr.mgr.getInstrument(instrumentId)?.priceTick
        initData()
    }



    protected open fun initViews(){
        quoteRed = resources.getColor(R.color.quote_red)
        quoteGreen = resources.getColor(R.color.quote_green)
        quoteWhite = quoteGreen
        highlightColor = resources.getColor(R.color.color_highlight)
        textColor = resources.getColor(R.color.color_text_gray)
        colorAvgLine = resources.getColor(R.color.color_avg_line)
    }

    private  fun initData(){
        val quoteEntity = QuoteInfoMgr.mgr.getQuoteEntity(instrumentId)
        preSettlementPrice = quoteEntity?.pre_settlement?.toFloat() ?: Float.NaN
        viewMode = viewModelOf<FutureChartViewModel>().value.also { it ->
            disposables.add(it.chartData.subscribe({drawChartLines(it)},{
                Log.e("${this@BaseChartsFragment.javaClass.simpleName}","Chart data received error message",it)
            }))
        }
    }

    fun unSetChart(){
        viewMode?.setChart("IF8888",chartType,viewWidth)
        DiffEntity.clearInstrumentKLineEntity(instrumentId)
    }

    override fun onVisible() {
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        val chartType:FutureChartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        Log.d(javaClass.simpleName,"initData setChart $instrument ${chartType.duration}")
        viewMode?.setChart(instrument,chartType,viewWidth)
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
            Float.NaN, Float.NaN,null,CommonUtil.getColorRes(R.color.quote_white)))
        secondLegendEntries.add(LegendEntry("OI:${dataEntity.close_oi}",Legend.LegendForm.NONE,
            Float.NaN, Float.NaN,null,quoteRed))
        secondChartView.legend.setCustom(secondLegendEntries)
        secondChartView.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        secondChartView.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        if(firstChartView.chartType == CombinedChartView.CHART_TYPE_CANDLE){
            refreshCandleChartLegend(dataEntity)
        }
    }

    private fun refreshCandleChartLegend(dataEntity:KLineEntity.DataEntity){
        val chartLegend = ArrayList<LegendEntry>(4)
        chartLegend.add(LegendEntry("Open:${dataEntity.open}",Legend.LegendForm.NONE, Float.NaN,
            Float.NaN,null,quoteWhite))
        chartLegend.add(LegendEntry("Close:${dataEntity.close}",Legend.LegendForm.NONE, Float.NaN,
            Float.NaN,null,quoteWhite))
        chartLegend.add(LegendEntry("High:${dataEntity.high}",Legend.LegendForm.NONE, Float.NaN,
            Float.NaN,null,quoteWhite))
        chartLegend.add(LegendEntry("Low:${dataEntity.low}",Legend.LegendForm.NONE, Float.NaN,
            Float.NaN,null,quoteWhite))
        firstChartView.legend.setCustom(chartLegend)
        firstChartView.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        firstChartView.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    }

    protected fun generateLineDataSet(entries:List<Entry>, lineColor:Int, lable:String, dependency: YAxis.AxisDependency, isHighlight:Boolean = false): ILineDataSet {
        val lineDataSet = LineDataSet(entries,lable)
        lineDataSet.color = lineColor
        lineDataSet.lineWidth = 0.8F
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.axisDependency = dependency
        lineDataSet.isHighlightEnabled = isHighlight
        if(isHighlight){
//            refreshYAxisRange(lineDataSet)
            lineDataSet.highlightLineWidth = 0.8F
        }
        return lineDataSet
    }

    override fun onDestroyView() {
        firstChartView.data?.clearValues()
        secondChartView.data?.clearValues()
        super.onDestroyView()
    }

    /**
     * Y轴Label格式化工具
     */
    class ChartViewYAxisValueFormatter(private val priceTick:String?) : ValueFormatterComponent(){
        override fun getFormattedValue(value: Float): String {
            return NumberUtils.formatNum(value.toString(),priceTick)
        }
    }

    class CandleChartViewXAxisValueFormatter(private val lables:SparseArray<String>) : ValueFormatterComponent(){
        override fun getFormattedValue(value: Float): String {
            return lables[value.toInt()] ?: Omits.OmitString
        }
    }
}