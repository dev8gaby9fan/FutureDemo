package com.future.quote.ui.futureinfo.charts

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import com.fsh.common.util.DateUtils
import com.fsh.common.util.Omits
import com.fsh.common.widget.mpchart.CombinedChartView
import com.future.quote.R
import com.future.quote.enums.FutureChartDuration
import com.future.quote.model.KLineEntity
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.abs

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
    //均线和分时曲线颜色
    private var colorDayLine:Int = 0
    private var colorAvgLine:Int = 0
    private var xAxisLables:SparseArray<String> = SparseArray()
    private lateinit var dayLineData:ILineDataSet
    private lateinit var avgLineData:ILineDataSet
    private lateinit var oiLineData:ILineDataSet
    private lateinit var volBarData:IBarDataSet
    //当前交易日数据的开始点和结束点以及服务器返回数据的最后一个数据的索引
    private var tradingDayStartId:Int = -1
    private var tradingDayEndId:Int = -1
    private var lastIndex:Int = -1
    //成交总量
    private var sumVolume:Int = 0
    //总成交额
    private var sumPrice:Float = 0F


    override fun layoutRes(): Int = R.layout.quote_fragment_current_day_line_chart

    override fun initViews() {
        super.initViews()
        //分时界面，图表不允许缩放
        firstChartView.setScaleEnabled(false)
        secondChartView.setScaleEnabled(false)
        colorDayLine = resources.getColor(R.color.color_day_line)
        colorAvgLine = resources.getColor(R.color.color_avg_line)
    }
    override fun drawChartLines(kLineEntity: KLineEntity) {
        Log.d("CurrentDayLineChartFragment","drawChartLines ${kLineEntity.instrumentId} ${kLineEntity.klineDuration} ${kLineEntity.data.size} isInit${(firstChartView.data == null || firstChartView.data.allData.isEmpty())}")
        if(firstChartView.data == null || firstChartView.data.allData.isEmpty()){
            drawInitChartLines(kLineEntity)
        }else{
            drawUpdateChartLines(kLineEntity)
        }
    }
    //初始化数据
    private fun drawInitChartLines(kLineEntity: KLineEntity){
        tradingDayStartId = kLineEntity.trading_day_start_id
        tradingDayEndId = kLineEntity.trading_day_end_id
        lastIndex = kLineEntity.last_id
        if(tradingDayStartId == -1 || tradingDayEndId == -1 || lastIndex == -1 || Omits.isOmit(tradingDayStartId) || Omits.isOmit(tradingDayEndId) || Omits.isOmit(lastIndex)){
            return
        }
        //设置最结算价的位置
        firstChartView.axisLeft.baseValue = preSettlementPrice.toFloat()
        secondChartView.axisRight.baseValue = preSettlementPrice.toFloat()
        //分时线、均线、持仓量、成交量数据
        val dayLineEntries:MutableList<Entry> = ArrayList()
        val avgLineEntries:MutableList<Entry> = ArrayList()
        val oiLineEntries:MutableList<Entry> = ArrayList()
        val volBarEntries:MutableList<BarEntry> = ArrayList()
        for(index in tradingDayStartId .. tradingDayEndId){
            val dataEntry = kLineEntity.data[index.toString()] ?: continue
            val entries = generateMutableEntries(index, dataEntry)
            dayLineEntries.add(entries[0])
            avgLineEntries.add(entries[1])
            oiLineEntries.add(entries[2])
            volBarEntries.add(entries[3] as BarEntry)
        }

        dayLineData = generateLineDataSet(dayLineEntries,colorDayLine,"dayLineChart",YAxis.AxisDependency.LEFT,true)
        avgLineData = generateLineDataSet(avgLineEntries,colorAvgLine,"avgLineChart",YAxis.AxisDependency.LEFT)
        val lineData = LineData(dayLineData,avgLineData)
        val firstCombinedData = CombinedData()
        firstCombinedData.setData(lineData)

        volBarData = generateBarDataSet(volBarEntries,"volBarChart", arrayListOf(colorAvgLine),true)
        oiLineData = generateLineDataSet(oiLineEntries,colorAvgLine,"oiLineChart",YAxis.AxisDependency.RIGHT)

        val secondLineData = LineData(oiLineData)
        val secondBarData = BarData(volBarData)
        secondBarData.barWidth = 0.01F
        val secondCombinedData = CombinedData()
        secondCombinedData.setData(secondLineData)
        secondCombinedData.setData(secondBarData)

        refreshChartLegend(lastIndex)

        firstChartView.data = firstCombinedData
        firstChartView.setVisibleXRangeMinimum((tradingDayEndId-tradingDayStartId).toFloat())
        firstChartView.xAxis.axisMaximum = firstCombinedData.xMax + 0.35F
        firstChartView.xAxis.axisMinimum = firstCombinedData.xMin - 0.35F
        firstChartView.xAxis.xLabels = xAxisLables
        firstChartView.invalidate()

        secondChartView.data = secondCombinedData
        secondChartView.setVisibleXRangeMinimum((tradingDayEndId-tradingDayStartId).toFloat())
        secondChartView.xAxis.axisMaximum = firstCombinedData.xMax + 0.35F
        secondChartView.xAxis.axisMinimum = firstCombinedData.xMin - 0.35F
        secondChartView.xAxis.xLabels = xAxisLables
        secondChartView.invalidate()
    }
    //更新数据
    private fun drawUpdateChartLines(kLineEntity: KLineEntity){

    }

    private fun generateMutableEntries(index:Int,data:KLineEntity.DataEntity):List<Entry>{
        val entries:MutableList<Entry> = ArrayList()
        calendar.timeInMillis = data.datetime / FutureChartDuration._1MILLIS
        xAxisLables.put(index,DateUtils.formatDate(DateUtils.PATTERN_HHMM,calendar.time))

        sumVolume += data.volume
        sumPrice += data.close * data.volume
        val avg:Float = if(sumVolume == 0) 0F else sumPrice/sumVolume
        entries.add(Entry(index.toFloat(),data.close))
        entries.add(Entry(index.toFloat(),avg))
        entries.add(Entry(index.toFloat(),data.close_oi.toFloat()))
        entries.add(BarEntry(index.toFloat(),data.volume.toFloat()))
        return entries
    }

    private fun generateLineDataSet(entries:List<Entry>,lineColor:Int,lable:String,dependency: YAxis.AxisDependency,isHighlight:Boolean = false):ILineDataSet{
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

    private fun refreshYAxisRange(lineDataSet:ILineDataSet){
        val lowValue = abs(lineDataSet.yMin - preSettlementPrice)
        val highValue = abs(lineDataSet.yMax - preSettlementPrice)
        if(lowValue > highValue){
            firstChartView.axisLeft.axisMinimum = (preSettlementPrice - lowValue).toFloat()
            firstChartView.axisRight.axisMinimum = (preSettlementPrice - lowValue).toFloat()
            firstChartView.axisLeft.axisMaximum = (preSettlementPrice - highValue).toFloat()
            firstChartView.axisRight.axisMaximum = (preSettlementPrice - highValue).toFloat()
        }else{
            firstChartView.axisLeft.axisMinimum = (preSettlementPrice - highValue).toFloat()
            firstChartView.axisRight.axisMinimum = (preSettlementPrice - highValue).toFloat()
            firstChartView.axisLeft.axisMaximum = (preSettlementPrice - lowValue).toFloat()
            firstChartView.axisRight.axisMaximum = (preSettlementPrice - lowValue).toFloat()
        }
    }

    private fun refreshChartLegend(index:Int){

    }
}