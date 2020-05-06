package com.future.quote.ui.futureinfo.charts

import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import com.fsh.common.widget.mpchart.chartlistener.CoupleChartGestureListener
import com.future.quote.R
import com.future.quote.enums.FutureChartType
import com.future.quote.model.ChartEntity
import com.future.quote.model.DiffEntity
import com.future.quote.model.KLineEntity
import com.future.quote.service.QuoteParser
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

        val scaleX = 2.4F
        val topVals = FloatArray(9)
        val middleVals = FloatArray(9)
        val bottomVals = FloatArray(9)
        val topMatrix = firstChartView.viewPortHandler.matrixTouch
        val middleMatrix = secondChartView.viewPortHandler.matrixTouch
        topMatrix.getValues(topVals)
        middleMatrix.getValues(middleVals)

        topVals[Matrix.MSCALE_X] = scaleX
        middleVals[Matrix.MSCALE_X] = scaleX
        topMatrix.setValues(topVals)
        middleMatrix.setValues(middleVals)
        firstChartView.viewPortHandler.refresh(topMatrix, firstChartView, false)
        secondChartView.viewPortHandler.refresh(middleMatrix, secondChartView, false)
    }

    override fun drawChartLines(kLineEntity: KLineEntity) {
        val chartEntity = DiffEntity.getChartEntity(QuoteParser.JSON_CHART_ID)
        Log.d("KLineChartFragment","drawChartLines $instrumentId ${chartType.duration} ${kLineEntity.klineDuration} ${kLineEntity.data.size}")
        if(firstChartView.data == null || firstChartView.data.allData.isEmpty()){
            drawInitCharts(kLineEntity,chartEntity)
        }else{
            drawUpdateCharts(kLineEntity,chartEntity)
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
        volBarData = generateBarDataSet(volEntries,"volBarChart", arrayListOf(quoteGreen,quoteRed),true)
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
        refreshLegend(lastIndex)
        firstChartView.data = firstChartData//当前屏幕会显示所有的数据
        firstChartView.xAxis.axisMaximum = firstChartData.xMax + 2.5f
        firstChartView.xAxis.axisMinimum = firstChartData.xMin - 0.5f
        firstChartView.setVisibleXRangeMinimum(10F)
//        firstChartView.setVisibleXRangeMaximum(70F)


        secondChartView.data = secondChartData//当前屏幕会显示所有的数据
        secondChartView.xAxis.axisMaximum = secondChartData.xMax + 2.5f
        secondChartView.xAxis.axisMinimum = secondChartData.xMin - 0.5f
        secondChartView.setVisibleXRangeMinimum(10F)
//        secondChartView.setVisibleXRangeMaximum(70F)
        firstChartView.postDelayed({
            firstChartView.moveViewToX((lastIndex - leftIndex).toFloat())
            secondChartView.moveViewToX((lastIndex - leftIndex).toFloat())
        },300)
        Log.d("KLineChartFragment","chart view move to ${(lastIndex - leftIndex).toFloat()}")
    }

    private fun drawUpdateCharts(kLineEntity: KLineEntity, chartEntity: ChartEntity){
        val newLeftId = chartEntity.left_id
        val newRightId = chartEntity.right_id
        if(newLeftId == -1 || newRightId == -1) return
        val newLastId = kLineEntity.last_id
        if(newLastId == -1) return
        //新增数据
        if(newLeftId >= leftIndex && newRightId >= rightIndex){
            for(index in newLastId..newRightId){
                val dataEntity = kLineEntity.data[index.toString()]?:continue
                if(index == lastIndex){
                    sumVolume -= dataEntity.volume
                    sumPrice -= dataEntity.volume * dataEntity.close
                    sumVolume += dataEntity.volume
                    sumPrice += dataEntity.volume * dataEntity.close
                    val avgPrice = sumPrice / sumVolume
                    val candleEntry = candleData.getEntryForIndex(candleData.entryCount-1)
                    candleEntry.close = dataEntity.close
                    candleEntry.open = dataEntity.open
                    candleEntry.high = dataEntity.high
                    candleEntry.low = dataEntity.low
                    avgLineData.getEntryForIndex(avgLineData.entryCount-1).y = avgPrice
                    volBarData.getEntryForIndex(volBarData.entryCount-1).y = dataEntity.volume.toFloat()
                    volBarData.getEntryForIndex(volBarData.entryCount-1).data = dataEntity.open - dataEntity.close
                    oiLineData.getEntryForIndex(oiLineData.entryCount-1).y = dataEntity.close_oi.toFloat()
                }else{
                    val entries =
                        generateMultiDataEntries(index, newLeftId, dataEntity)
                    candleData.addEntry(entries[0] as CandleEntry)
                    avgLineData.addEntry(entries[1])
                    oiLineData.addEntry(entries[2])
                    volBarData.addEntry(entries[3] as BarEntry)
                }
            }
            leftIndex = newLeftId
            rightIndex = newRightId
            lastIndex = newLastId
            firstChartView.data.notifyDataChanged()
            firstChartView.notifyDataSetChanged()
            firstChartView.xAxis.axisMaximum = firstChartView.data.xMax + 2.5f
            firstChartView.xAxis.axisMinimum = firstChartView.data.xMin - 0.5f
            firstChartView.invalidate()

            secondChartView.data.notifyDataChanged()
            secondChartView.notifyDataSetChanged()
            secondChartView.xAxis.axisMaximum = secondChartView.data.xMax + 2.5f
            secondChartView.xAxis.axisMinimum = secondChartView.data.xMin - 0.5f
            secondChartView.invalidate()

        }else{//更多历史数据

        }
    }

    private fun generateMultiDataEntries(index:Int,leftIndex:Int,dataEntity:KLineEntity.DataEntity):List<Entry>{
        val entries = ArrayList<Entry>()
        sumVolume += dataEntity.volume
        sumPrice += dataEntity.volume * dataEntity.close
        val avg:Float = if(sumVolume == 0) 0F else sumPrice/sumVolume
        entries.add(CandleEntry(index.toFloat(),dataEntity.high,dataEntity.low,dataEntity.open,dataEntity.close))
        entries.add(Entry(index.toFloat(),avg))
        entries.add(Entry(index.toFloat(),dataEntity.close_oi.toFloat()))
        entries.add(BarEntry(index.toFloat(),dataEntity.volume.toFloat(),dataEntity.open - dataEntity.close))
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