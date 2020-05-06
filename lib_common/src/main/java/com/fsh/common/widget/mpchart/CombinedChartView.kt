package com.fsh.common.widget.mpchart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.fsh.common.R
import com.fsh.common.widget.mpchart.component.LegendComponent
import com.fsh.common.widget.mpchart.component.TransformerComponent
import com.fsh.common.widget.mpchart.component.XAxisComponent
import com.fsh.common.widget.mpchart.component.YAxisComponent
import com.fsh.common.widget.mpchart.render.*
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

class CombinedChartView : CombinedChart {
    companion object {
        const val CHART_TYPE_LINE = 0
        const val CHART_TYPE_CANDLE = 1
    }

    var chartType: Int = CHART_TYPE_LINE
        private set(value) {
            field = value
        }

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val ta =
            context?.obtainStyledAttributes(attrs, R.styleable.CombinedChartView)
        chartType =
            ta?.getInt(R.styleable.CombinedChartView_chartType, CHART_TYPE_LINE) ?: CHART_TYPE_LINE
        ta?.recycle()

        initAxis()
        initViews()
    }

    private fun initAxis() {

        mLeftAxisTransformer = TransformerComponent(mViewPortHandler)
        mRightAxisTransformer = TransformerComponent(mViewPortHandler)

        mXAxis = XAxisComponent()
        mXAxisRenderer = if (chartType == CHART_TYPE_LINE)
            XAxisCurrentDayLineComponentRender(mViewPortHandler, mXAxis, mLeftAxisTransformer, this)
        else
            XAxisKlineComponentRender(mViewPortHandler, mXAxis, mLeftAxisTransformer, this)

        mXAxis.setDrawGridLines(false)


        mAxisLeft = YAxisComponent(YAxis.AxisDependency.LEFT)
        mAxisRendererLeft = if (chartType == CHART_TYPE_LINE)
            YAxisCurrentDayLineComponentRender(mViewPortHandler, mAxisLeft, mLeftAxisTransformer)
        else
            YAxisKlineComponentRender(mViewPortHandler, mAxisLeft, mLeftAxisTransformer)

        mAxisRight = YAxisComponent(YAxis.AxisDependency.RIGHT)
        mAxisRendererRight = if (chartType == CHART_TYPE_LINE)
            YAxisCurrentDayLineComponentRender(mViewPortHandler, mAxisRight, mRightAxisTransformer)
        else
            YAxisKlineComponentRender(mViewPortHandler, mAxisRight, mRightAxisTransformer)

        mLegend = LegendComponent()
        mLegendRenderer = LegendComponentRender(viewPortHandler, mLegend)

        mRenderer = CombinedChartViewRender(this, mAnimator, mViewPortHandler)
    }

    private fun initViews() {
        description.isEnabled = false
        isDragDecelerationEnabled = true
        setBackgroundColor(resources.getColor(R.color.color_chart_background))
        setGridBackgroundColor(resources.getColor(R.color.color_chart_grid_background))
        setDrawValueAboveBar(false)
        setDrawBorders(false)
        setNoDataText("正在加载数据中")
        isAutoScaleMinMaxEnabled = true
        isDragEnabled = true
        isDoubleTapToZoomEnabled = false
        isHighlightPerDragEnabled = false
        isHighlightPerTapEnabled = false
    }

    override fun getXAxis(): XAxisComponent = mXAxis as XAxisComponent
    override fun getAxisLeft(): YAxisComponent = mAxisLeft as YAxisComponent
    override fun getAxisRight(): YAxisComponent = mAxisRight as YAxisComponent
    override fun getLegend(): Legend = mLegend as LegendComponent
    override fun getTransformer(which: YAxis.AxisDependency?): TransformerComponent {
        return super.getTransformer(which) as TransformerComponent
    }


    override fun drawMarkers(canvas: Canvas?) {
        if (mMarker == null || !isDrawMarkersEnabled || !valuesToHighlight())
            return

        for (i in mIndicesToHighlight.indices) {

            val highlight = mIndicesToHighlight[i]

            val set = mData.getDataSetByIndex(highlight.dataSetIndex)

            val e = mData.getEntryForHighlight(mIndicesToHighlight[i])
            val entryIndex = set.getEntryIndex(e as Nothing?)

            // make sure entry not null
            if (e == null || entryIndex > set.entryCount * mAnimator.phaseX)
                continue

            val pos = getMarkerPosition(highlight)

            // check bounds
//            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
//                continue

            // callbacks to update the content
            mMarker.refreshContent(e, highlight)

            // draw the marker
            mMarker.draw(canvas, pos[0], pos[1])
        }
    }

    fun getEntryForHighlight(highlight: Highlight): Entry? {
        val dataObjects = mData.allData

        if (highlight.dataIndex >= dataObjects.size)
            return null

        val data = dataObjects[highlight.dataIndex]

        return if (highlight.dataSetIndex >= data.dataSetCount)
            null
        else {
            // The value of the highlighted entry could be NaN -
            //   if we are not interested in highlighting a specific value.

            val entries: List<Entry> = data.getDataSetByIndex(highlight.dataSetIndex)
                .getEntriesForXValue(highlight.x) as List<Entry>

            entries[0]
        }
    }

}