package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import com.fsh.common.widget.mpchart.component.XAxisComponent
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisCurrentDayLineComponentRender(viewPortHandler: ViewPortHandler?, xAxis: XAxis, trans: Transformer?,chart:BarLineChartBase<*>) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    private var mChart: BarLineChartBase<*>

    init {
        mXAxis = xAxis as XAxisComponent?
        mChart = chart
    }


    override fun renderAxisLabels(c: Canvas) {

        if (!mXAxis.isEnabled || !mXAxis.isDrawLabelsEnabled)
            return

        val yoffset = mXAxis.yOffset

        mAxisLabelPaint.typeface = mXAxis.typeface
        mAxisLabelPaint.textSize = mXAxis.textSize
        mAxisLabelPaint.color = mXAxis.textColor

        val pointF = MPPointF.getInstance(0f, 0f)
        when {
            mXAxis.position === XAxis.XAxisPosition.TOP -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, mViewPortHandler.contentTop() - yoffset, pointF)

            }
            mXAxis.position === XAxis.XAxisPosition.TOP_INSIDE -> {
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(
                    c,
                    mViewPortHandler.contentTop() + yoffset + mXAxis.mLabelRotatedHeight,
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
                    mViewPortHandler.contentBottom() - yoffset - mXAxis.mLabelRotatedHeight,
                    pointF
                )

            }
            else -> { // BOTH SIDED
                pointF.x = 0.5f
                pointF.y = 1.0f
                drawLabels(c, mViewPortHandler.contentTop() - yoffset, pointF)
                pointF.x = 0.5f
                pointF.y = 0.0f
                drawLabels(c, mViewPortHandler.contentBottom() + yoffset, pointF)
            }
        }
        MPPointF.recycleInstance(pointF)
    }


    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val position = floatArrayOf(0f, 0f)
        var labelWidth = 0
        var labelHeight = 0
        val count = (mXAxis as XAxisComponent).xLabels.size()
        Log.d("XAxisCurrentDayLineRender","drawLabels lables count $count")
        for (i in 0 until count) {
            /*获取label对应key值，也就是x轴坐标0,60,121,182,242*/
            val ix = (mXAxis as XAxisComponent).xLabels.keyAt(i)

            position[0] = ix.toFloat()

            /*在图表中的x轴转为像素，方便绘制text*/
            mTrans.pointValuesToPixel(position)

            /*x轴越界*/
            if (mViewPortHandler.isInBoundsX(position[0])) {

                val label = (mXAxis as XAxisComponent).xLabels.valueAt(i)

                if (label != null) {

                    if (labelWidth == 0) labelWidth = Utils.calcTextWidth(mAxisLabelPaint, label)
                    if (labelHeight == 0)
                        labelHeight = Utils.calcTextHeight(mAxisLabelPaint, label!!)

                    //右出界
                    if (labelWidth / 2 + position[0] > mChart.viewPortHandler.contentRight()) {
                        position[0] = mViewPortHandler.contentRight() - labelWidth / 2
                    } else if (position[0] - labelWidth / 2 < mChart.viewPortHandler.contentLeft()) {
                        //左出界
                        position[0] = mViewPortHandler.offsetLeft() + labelWidth / 2
                    }

                    c.drawText(
                        label, position[0],
                        pos + mChart.viewPortHandler.offsetBottom() / 2 + (labelHeight / 2).toFloat(),
                        mAxisLabelPaint
                    )
                }

            }

        }
    }

    /*x轴垂直线*/
    override fun renderGridLines(c: Canvas) {
        if (!mXAxis.isDrawGridLinesEnabled || !mXAxis.isEnabled)
            return
        val position = floatArrayOf(0f, 0f)

        mGridPaint.color = mXAxis.gridColor
        mGridPaint.strokeWidth = mXAxis.gridLineWidth
        mGridPaint.pathEffect = mXAxis.gridDashPathEffect
        val gridLinePath = mRenderGridLinesPath
        gridLinePath.reset()
        var count = (mXAxis as XAxisComponent).xLabels.size()
        if (!mChart.isScaleXEnabled) {
            count -= 1
        }

        //首尾标签不画
        for (i in 1 until count) {

            val ix = (mXAxis as XAxisComponent).xLabels.keyAt(i)

            position[0] = ix.toFloat()

            mTrans.pointValuesToPixel(position)

            gridLinePath.moveTo(position[0], mViewPortHandler.contentBottom())
            gridLinePath.lineTo(position[0], mViewPortHandler.contentTop())

            // draw a path because lines don't support dashing on lower android versions
            c.drawPath(gridLinePath, mGridPaint)

            gridLinePath.reset()

        }

    }
}