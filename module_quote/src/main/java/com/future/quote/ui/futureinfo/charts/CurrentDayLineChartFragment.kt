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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.abs

/**
 * 分时界面
 */
class CurrentDayLineChartFragment : BaseChartsFragment() {
    companion object {
        fun newInstance(instrumentId: String): CurrentDayLineChartFragment {
            return CurrentDayLineChartFragment().apply {
                arguments = Bundle().apply { putString(INSTRUMENT_ID, instrumentId) }
            }
        }
    }

    //均线和分时曲线颜色
    private var colorDayLine: Int = 0

    private var xAxisLables: SparseArray<String> = SparseArray()

    private lateinit var dayLineData: ILineDataSet
    private lateinit var avgLineData: ILineDataSet
    private lateinit var oiLineData: ILineDataSet
    private lateinit var volBarData: IBarDataSet
    //当前交易日数据的开始点和结束点以及服务器返回数据的最后一个数据的索引
    private var tradingDayStartId: Int = -1
    private var tradingDayEndId: Int = -1
    private var lastIndex: Int = -1
    //成交总量
    private var sumVolume: Int = 0
    //总成交额
    private var sumPrice: Float = 0F


    override fun layoutRes(): Int = R.layout.quote_fragment_current_day_line_chart

    override fun initViews() {
        super.initViews()
        //分时界面，图表不允许缩放
        firstChartView.setScaleEnabled(false)
        secondChartView.setScaleEnabled(false)
        colorDayLine = resources.getColor(R.color.color_day_line)

        initChartView()
    }

    private fun initChartView() {
        firstChartView.setViewPortOffsets(0F, 0F, 0F, 1F)
        val fXAxis = firstChartView.xAxis
        fXAxis.position = XAxis.XAxisPosition.BOTTOM
        fXAxis.setDrawLabels(false)
        fXAxis.setDrawGridLines(false)
        fXAxis.setDrawAxisLine(false)

        val fAxisLeft = firstChartView.axisLeft
        fAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        fAxisLeft.setDrawGridLines(false)
        fAxisLeft.setDrawAxisLine(false)
        fAxisLeft.setDrawLabels(true)
        fAxisLeft.setLabelCount(3, true)
//        fAxisLeft.enableGridDashedLine(3F, 6F, 0F)
        fAxisLeft.valueFormatter = ChartViewYAxisValueFormatter(priceTick)
        fAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)


        val fAxisRight = firstChartView.axisRight
        fAxisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        fAxisRight.setDrawGridLines(false)
        fAxisRight.setLabelCount(3, true)
        fAxisRight.setDrawLabels(true)
//        fAxisRight.enableGridDashedLine(3F,6F,0F)
        fAxisRight.valueFormatter = ChartViewYAxisValueFormatter("0.01")

        firstChartView.legend.isEnabled = false

        secondChartView.setViewPortOffsets(0f, 0f, 0f, 30f)
        val sXAxis = secondChartView.xAxis
        sXAxis.position = XAxis.XAxisPosition.BOTTOM
        sXAxis.setDrawLabels(true)
        sXAxis.setDrawGridLines(false)
        sXAxis.setDrawAxisLine(true)
        sXAxis.textColor = textColor

        val sAxisLeft = secondChartView.axisLeft
        sAxisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        sAxisLeft.setDrawGridLines(false)
        sAxisLeft.setLabelCount(2, true)
        sAxisLeft.setDrawLabels(true)
        sAxisLeft.enableGridDashedLine(3F, 6F, 0F)
        sAxisLeft.valueFormatter = ChartViewYAxisValueFormatter("1")

        val sAxisRight = secondChartView.axisRight
        sAxisRight.setDrawGridLines(false)
        sAxisRight.setDrawLabels(false)
        sAxisRight.setDrawAxisLine(false)
    }

    override fun drawChartLines(kLineEntity: KLineEntity) {
        Log.d(
            "CurrentDayLineChartFragment",
            "drawChartLines ${kLineEntity.instrumentId} ${kLineEntity.klineDuration} ${kLineEntity.data.size} isInit${(firstChartView.data == null || firstChartView.data.allData.isEmpty())}"
        )
        if (firstChartView.data == null || firstChartView.data.allData.isEmpty()) {
            drawInitChartLines(kLineEntity)
            Log.d(
                "CurrentDayLineChartFragment",
                "drawChartLines firstChartView left Axis draw grid line  ${firstChartView.axisLeft.labelCount} ${firstChartView.axisLeft.isDrawGridLinesEnabled} ${firstChartView.axisLeft.isDrawAxisLineEnabled}"
            )
        } else {
            drawUpdateChartLines(kLineEntity)
        }
    }

    //初始化数据
    private fun drawInitChartLines(kLineEntity: KLineEntity) {
        tradingDayStartId = kLineEntity.trading_day_start_id
        tradingDayEndId = kLineEntity.trading_day_end_id
        lastIndex = kLineEntity.last_id
        if (tradingDayStartId == -1 || tradingDayEndId == -1 || lastIndex == -1 || Omits.isOmit(
                tradingDayStartId
            ) || Omits.isOmit(tradingDayEndId) || Omits.isOmit(lastIndex)
        ) {
            return
        }
        //设置最结算价的位置
        firstChartView.axisLeft.baseValue = preSettlementPrice.toFloat()
        secondChartView.axisRight.baseValue = preSettlementPrice.toFloat()
        //分时线、均线、持仓量、成交量数据
        val dayLineEntries: MutableList<Entry> = ArrayList()
        val avgLineEntries: MutableList<Entry> = ArrayList()
        val oiLineEntries: MutableList<Entry> = ArrayList()
        val volBarEntries: MutableList<BarEntry> = ArrayList()
        for (index in tradingDayStartId..tradingDayEndId) {
            val dataEntry = kLineEntity.data[index.toString()] ?: continue
            val entries = generateMutableEntries(index, dataEntry)
            dayLineEntries.add(entries[0])
            avgLineEntries.add(entries[1])
            oiLineEntries.add(entries[2])
            volBarEntries.add(entries[3] as BarEntry)
        }

        dayLineData = generateLineDataSet(
            dayLineEntries,
            colorDayLine,
            "dayLineChart",
            YAxis.AxisDependency.LEFT,
            true
        )
        avgLineData = generateLineDataSet(
            avgLineEntries,
            colorAvgLine,
            "avgLineChart",
            YAxis.AxisDependency.LEFT
        )
        val lineData = LineData(dayLineData, avgLineData)
        val firstCombinedData = CombinedData()
        firstCombinedData.setData(lineData)

        volBarData =
            generateBarDataSet(volBarEntries, "volBarChart", arrayListOf(quoteGreen,quoteRed), true)
        oiLineData = generateLineDataSet(
            oiLineEntries,
            colorAvgLine,
            "oiLineChart",
            YAxis.AxisDependency.RIGHT
        )

        val secondLineData = LineData(oiLineData)
        val secondBarData = BarData(volBarData)
        secondBarData.barWidth = 0.01F
        val secondCombinedData = CombinedData()
        secondCombinedData.setData(secondLineData)
        secondCombinedData.setData(secondBarData)

//        refreshYAxisRange(dayLineData)
        refreshLegend(lastIndex)
        firstChartView.data = firstCombinedData
        firstChartView.setVisibleXRangeMinimum((tradingDayEndId - tradingDayStartId).toFloat())
        firstChartView.xAxis.axisMaximum = firstCombinedData.xMax + 0.35F
        firstChartView.xAxis.axisMinimum = firstCombinedData.xMin - 0.35F
        firstChartView.xAxis.xLabels = xAxisLables
        firstChartView.invalidate()

        secondChartView.data = secondCombinedData
        secondChartView.setVisibleXRangeMinimum((tradingDayEndId - tradingDayStartId).toFloat())
        secondChartView.xAxis.axisMaximum = firstCombinedData.xMax + 0.35F
        secondChartView.xAxis.axisMinimum = firstCombinedData.xMin - 0.35F
        secondChartView.xAxis.xLabels = xAxisLables
        secondChartView.invalidate()
    }

    //更新数据
    private fun drawUpdateChartLines(kLineEntity: KLineEntity) {
        val newLastId = kLineEntity.last_id
        for (index in lastIndex..newLastId) {
            val dataEntity = kLineEntity.data[index.toString()] ?: continue
            if(index == lastIndex){
                sumVolume -= dataEntity.volume
                sumPrice -= dataEntity.volume * dataEntity.close
                sumVolume += dataEntity.volume
                sumPrice += dataEntity.volume * dataEntity.close
                val avgPrice = sumPrice / sumVolume
                dayLineData.getEntryForIndex(dayLineData.entryCount-1).y = dataEntity.close
                avgLineData.getEntryForIndex(avgLineData.entryCount-1).y = avgPrice
                oiLineData.getEntryForIndex(oiLineData.entryCount-1).y = dataEntity.close_oi.toFloat()
                volBarData.getEntryForIndex(volBarData.entryCount-1).y = dataEntity.volume.toFloat()
                volBarData.getEntryForIndex(volBarData.entryCount-1).data = dataEntity.open - dataEntity.close
            } else {
                val mutableList = generateMutableEntries(index, dataEntity)
                dayLineData.addEntry(mutableList[0])
                avgLineData.addEntry(mutableList[1])
                oiLineData.addEntry(mutableList[2])
                volBarData.addEntry(mutableList[3] as BarEntry)
            }
        }
        lastIndex = newLastId
        refreshLegend(lastIndex)
        firstChartView.data.notifyDataChanged()
        firstChartView.notifyDataSetChanged()
        firstChartView.setVisibleXRangeMinimum((tradingDayEndId-tradingDayStartId).toFloat())
        firstChartView.xAxis.axisMaximum = firstChartView.data.xMax + 0.35F
        firstChartView.xAxis.axisMinimum = firstChartView.data.xMin - 0.35F
        firstChartView.xAxis.xLabels = xAxisLables
        firstChartView.invalidate()
////        refreshYAxisRange(dayLineData)

        secondChartView.data.notifyDataChanged()
        secondChartView.notifyDataSetChanged()
        secondChartView.setVisibleXRangeMinimum((tradingDayEndId-tradingDayStartId).toFloat())
        secondChartView.xAxis.axisMaximum = secondChartView.data.xMax + 0.35F
        secondChartView.xAxis.axisMinimum = secondChartView.data.xMin - 0.35F
        secondChartView.xAxis.xLabels = xAxisLables
        secondChartView.invalidate()
    }

    private fun generateMutableEntries(index: Int, data: KLineEntity.DataEntity): List<Entry> {
        val entries: MutableList<Entry> = ArrayList()
        sumVolume += data.volume
        sumPrice += data.close * data.volume
        val avg: Float = if (sumVolume == 0) 0F else sumPrice / sumVolume
        entries.add(Entry(index.toFloat(), data.close))
        entries.add(Entry(index.toFloat(), avg))
        entries.add(Entry(index.toFloat(), data.close_oi.toFloat()))
        entries.add(BarEntry(index.toFloat(), data.volume.toFloat(),data.open - data.close))
        generateXAxisLabels(index, data.datetime)
        return entries
    }

    private fun generateXAxisLabels(index: Int, time: Long) {
        calendar.timeInMillis = time / FutureChartDuration._1MILLIS
        val formatTime = DateUtils.formatDate(DateUtils.PATTERN_HHMM, calendar.time)
        xValues.put(index, formatTime)
        if (index == tradingDayStartId) {
            xAxisLables.put(index, formatTime)
        } else if (index == tradingDayEndId) {
            //最后一个数据点时间为14:59:59或者02:59:59等，显示整点时间，需要+1分钟
            calendar.timeInMillis += 60000L
            xAxisLables.put(index, DateUtils.formatDate(DateUtils.PATTERN_HHMM, calendar.time))
        } else {
            val preTimeS = xValues[index - 1] ?: return
            val preTime = DateUtils.parseDate(DateUtils.PATTERN_HHMM, preTimeS) ?: return
            val currentTime = DateUtils.parseDate(DateUtils.PATTERN_HHMM, formatTime) ?: return
            //这中间出现中午收盘或者是盘中小憩的时间点，加上
            if (currentTime.time - preTime.time != 60000L) {
                xAxisLables.put(index, formatTime)
            }
        }

    }

    private fun refreshYAxisRange(lineDataSet: ILineDataSet) {
        val lowValue = abs(lineDataSet.yMin - preSettlementPrice)
        val highValue = abs(lineDataSet.yMax - preSettlementPrice)
        if (lowValue > highValue) {
            firstChartView.axisLeft.axisMinimum = (preSettlementPrice - lowValue)
            firstChartView.axisRight.axisMinimum = (preSettlementPrice - lowValue)
            firstChartView.axisLeft.axisMaximum = (preSettlementPrice - highValue)
            firstChartView.axisRight.axisMaximum = (preSettlementPrice - highValue)
        } else {
            firstChartView.axisLeft.axisMinimum = (preSettlementPrice - highValue)
            firstChartView.axisRight.axisMinimum = (preSettlementPrice - highValue)
            firstChartView.axisLeft.axisMaximum = (preSettlementPrice - lowValue)
            firstChartView.axisRight.axisMaximum = (preSettlementPrice - lowValue)
        }
    }
}