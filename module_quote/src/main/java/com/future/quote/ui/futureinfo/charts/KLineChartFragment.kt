package com.future.quote.ui.futureinfo.charts

import android.graphics.Paint
import android.os.Bundle
import com.fsh.common.widget.mpchart.chartlistener.CoupleChartGestureListener
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.model.ChartEntity
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

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
    private var sumVolume:Int = 0
    private var sumPrice:Float = 0F
    private var lastIndex:Int = -1
    private var leftIndex:Int = -1
    private var rightIndex:Int =-1
    private lateinit var candleData:ICandleDataSet
    private lateinit var avgLineData:ILineDataSet
    private lateinit var volBarData:IBarDataSet
    private lateinit var oiLineData:ILineDataSet

    override fun layoutRes(): Int = R.layout.quote_fragment_candle_chart

    override fun initViews() {
        super.initViews()
        initChartViews()
    }
    
    private fun initChartViews(){
        firstChartView.isScaleYEnabled = false
        firstChartView.drawOrder = arrayOf(
            CombinedChart.DrawOrder.CANDLE,
            CombinedChart.DrawOrder.LINE
        )
//        mKlineMarkerView = KlineMarkerView(activity)
//        mKlineMarkerView.setChartView(firstChartView)
//        firstChartView.setMarker(mKlineMarkerView)

        firstChartView.setDrawBorders(false)
//        firstChartView.setBorderColor(mColorGrid)

        val bottomAxis = firstChartView.xAxis
        bottomAxis.position = XAxis.XAxisPosition.BOTTOM
        bottomAxis.setDrawGridLines(false)
        bottomAxis.setDrawAxisLine(true)
        bottomAxis.setDrawLabels(false)
        bottomAxis.enableGridDashedLine(3F, 6F, 0F)
        bottomAxis.axisLineColor = highlightColor
//        bottomAxis.setGridColor(mColorGrid)

        val leftAxis = firstChartView.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.setDrawGridLines(true)
        leftAxis.setDrawAxisLine(false)
        leftAxis.spaceBottom = 3f
        leftAxis.spaceTop = 3f
        leftAxis.enableGridDashedLine(3F, 6F, 0F)
        leftAxis.gridColor = highlightColor
        leftAxis.textColor = textColor
        leftAxis.setLabelCount(6, true)
        leftAxis.valueFormatter = ChartViewYAxisValueFormatter(priceTick)

        val rightAxis = firstChartView.axisRight
        rightAxis.isEnabled = false

        secondChartView.isScaleYEnabled = false
        secondChartView.setDrawBorders(false)

        val bottomBottomAxis = secondChartView.xAxis
        bottomBottomAxis.position = XAxis.XAxisPosition.BOTTOM
        bottomBottomAxis.setDrawGridLines(true)
        bottomBottomAxis.setDrawAxisLine(true)
        bottomBottomAxis.axisLineWidth = 0.7f
        bottomBottomAxis.setDrawLabels(true)
        bottomBottomAxis.enableGridDashedLine(3F, 6F, 0F)
        bottomBottomAxis.gridColor = highlightColor
        bottomBottomAxis.axisLineColor = highlightColor
        bottomBottomAxis.textColor = textColor
        bottomBottomAxis.valueFormatter = ChartViewYAxisValueFormatter(priceTick)

        val bottomLeftAxis = secondChartView.axisLeft
        bottomLeftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        bottomLeftAxis.setDrawGridLines(true)
        bottomLeftAxis.setDrawAxisLine(false)
        bottomLeftAxis.enableGridDashedLine(3F, 6F, 0F)
        bottomLeftAxis.gridColor = highlightColor
        bottomLeftAxis.textColor = textColor
        bottomLeftAxis.setLabelCount(4, true)
        bottomLeftAxis.valueFormatter = ChartViewYAxisValueFormatter(priceTick)

        val bottomRightAxis = secondChartView.axisRight
        bottomRightAxis.setDrawLabels(false)
        bottomRightAxis.setDrawAxisLine(false)
        bottomRightAxis.setDrawGridLines(false)

        // 将K线控的滑动事件传递给交易量控件
        firstChartView.onChartGestureListener = CoupleChartGestureListener(
            firstChartView,
            arrayListOf(firstChartView, secondChartView)
        )
        secondChartView.onChartGestureListener = CoupleChartGestureListener(
            secondChartView,
            arrayListOf(firstChartView, secondChartView)
        )
    }

    override fun drawChartLines(kLineEntity: KLineEntity) {
        val instrument = arguments?.getString(INSTRUMENT_ID)!!
        val chartType:FutureChartType = (arguments?.getSerializable(CHARTTYPE) ?: FutureChartType.L_1MIN) as FutureChartType
        val chartEntity = DiffEntity.getChartEntity("$instrument:${chartType.duration}")
        if(firstChartView.data == null || firstChartView.data.allData.isEmpty()){
            drawInitCharts(kLineEntity,chartEntity)
        }
    }

    private fun drawInitCharts(kLineEntity: KLineEntity,chartEntity: ChartEntity){
        leftIndex = chartEntity.left_id
        rightIndex = chartEntity.right_id
        if(leftIndex == -1 || rightIndex == -1) return
        lastIndex = kLineEntity.last_id
        if(lastIndex == -1) return

        val candleEnties = ArrayList<CandleEntry>()
        val volEntries = ArrayList<BarEntry>()
        val oiEntries = ArrayList<Entry>()
        val avgEntries = ArrayList<Entry>()
        for(index in leftIndex .. lastIndex){
            val dataEntity = kLineEntity.data[index.toString()] ?: continue
            val entries = generateMultiDataEntries(index,leftIndex,dataEntity)
            candleEnties.add(entries[0] as CandleEntry)
            avgEntries.add(entries[1])
            oiEntries.add(entries[2])
            volEntries.add(entries[3] as BarEntry)
        }

        candleData = generateCandleData(candleEnties)
        avgLineData = generateLineDataSet(avgEntries,colorAvgLine,"avgLineChart",YAxis.AxisDependency.LEFT)
        volBarData = generateBarDataSet(volEntries,"volBarChart", arrayListOf(quoteGreen),true)
        oiLineData = generateLineDataSet(oiEntries,colorAvgLine,"oiLineChart",YAxis.AxisDependency.LEFT)

        val firstChartData = CombinedData().apply {
            setData(CandleData(this@KLineChartFragment.candleData))
            setData(LineData(avgLineData))
        }

        val secondChartData = CombinedData().apply {
            setData(BarData(volBarData).apply {
                barWidth = 0.01F
            })
            setData(LineData(oiLineData))
        }
        firstChartView.data = firstChartData//当前屏幕会显示所有的数据
        firstChartView.xAxis.axisMaximum = firstChartData.xMax + 2.5f
        firstChartView.xAxis.axisMinimum = firstChartData.xMin - 0.5f
        firstChartView.setVisibleXRangeMinimum(10F)
        firstChartView.moveViewToX((lastIndex - leftIndex).toFloat())
    }

    private fun generateMultiDataEntries(index:Int,leftIndex:Int,dataEntity:KLineEntity.DataEntity):List<Entry>{
        val entries = ArrayList<Entry>()
        sumVolume += dataEntity.volume
        sumPrice += dataEntity.volume * dataEntity.close
        val avg:Float = if(sumVolume == 0) 0F else sumPrice/sumVolume
        entries.add(CandleEntry(index.toFloat(),dataEntity.high,dataEntity.low,dataEntity.open,dataEntity.close))
        entries.add(Entry(index.toFloat(),avg))
        entries.add(Entry(index.toFloat(),dataEntity.close_oi.toFloat()))
        entries.add(BarEntry(index.toFloat(),dataEntity.volume.toFloat()))
        return entries
    }

    private fun generateCandleData(entries:List<CandleEntry>):ICandleDataSet{
        val dataSet = CandleDataSet(entries,"")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.shadowWidth = 0.7F
        dataSet.decreasingColor = quoteGreen
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = quoteRed
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.neutralColor = quoteWhite
        dataSet.shadowColorSameAsCandle = true
        dataSet.highlightLineWidth = 0.8F
        dataSet.highLightColor = highlightColor
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 9F
        dataSet.valueTextColor = quoteRed
        dataSet.setDrawIcons(false)
        dataSet.valueFormatter = ChartViewYAxisValueFormatter(priceTick)
        return dataSet
    }
}