package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisKlineComponentRender(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?,chart: BarLineChartBase<*>) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    private val mChart:BarLineChartBase<*> = chart

    override fun renderAxisLabels(c: Canvas) {

        if (!mXAxis.isEnabled || !mXAxis.isDrawLabelsEnabled)
            return

        val yOffset = mXAxis.yOffset

        mAxisLabelPaint.typeface = mXAxis.typeface
        mAxisLabelPaint.textSize = mXAxis.textSize
        mAxisLabelPaint.color = mXAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)
        when {
            mXAxis.position === XAxis.XAxisPosition.TOP -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, mViewPortHandler.contentTop() - yOffset, pointF)

            }
            mXAxis.position === XAxis.XAxisPosition.TOP_INSIDE -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(
                    c,
                    mViewPortHandler.contentTop() + yOffset + mXAxis.mLabelRotatedHeight,
                    pointF
                )

            }
            mXAxis.position === XAxis.XAxisPosition.BOTTOM -> {
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, mViewPortHandler.contentBottom(), pointF)

            }
            mXAxis.position === XAxis.XAxisPosition.BOTTOM_INSIDE -> {
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(
                    c,
                    mViewPortHandler.contentBottom() - yOffset - mXAxis.mLabelRotatedHeight,
                    pointF
                )

            }
            else -> { // BOTH SIDED
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, mViewPortHandler.contentTop() - yOffset, pointF)
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, mViewPortHandler.contentBottom() + yOffset, pointF)
            }
        }
        MPPointF.recycleInstance(pointF)
    }


    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val positions = FloatArray(mXAxis.mEntryCount * 2)
        var labelHeight = 0
        var labelWidth: Int

        run {
            var i = 0
            while (i < positions.size) {
                positions[i] = mXAxis.mEntries[i / 2]
                i += 2
            }
        }

        mTrans.pointValuesToPixel(positions)
        val count = positions.size
        var i = 0
        while (i < count) {
            var x = positions[i]
            if (mViewPortHandler.isInBoundsX(x)) {
                var label = mXAxis.valueFormatter.getFormattedValue(mXAxis.mEntries[i / 2], mXAxis)

                if (label != null) {

                    if (i != 0) {
                        val index = label.indexOf("/")
                        label = label.substring(index + 1)
                    }

                    if (labelHeight == 0)
                        labelHeight = Utils.calcTextHeight(mAxisLabelPaint, label)
                    labelWidth = Utils.calcTextWidth(mAxisLabelPaint, label)

                    //右出界
                    if (labelWidth / 2 + x > mChart.viewPortHandler.contentRight()) {
                        x = mViewPortHandler.contentRight() - labelWidth / 2
                    } else if (x - labelWidth / 2 < mChart.viewPortHandler.contentLeft()) {
                        //左出界
                        x = mViewPortHandler.offsetLeft() + labelWidth / 2
                    }

                    c.drawText(
                        label, x,
                        pos + mChart.viewPortHandler.offsetBottom() / 2 + (labelHeight / 2).toFloat(),
                        mAxisLabelPaint
                    )
                }
            }
            i += 2

        }
    }
}