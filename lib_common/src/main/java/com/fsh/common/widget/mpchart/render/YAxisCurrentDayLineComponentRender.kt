package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.fsh.common.R
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.Omits
import com.fsh.common.widget.mpchart.component.YAxisComponent
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class YAxisCurrentDayLineComponentRender(viewPortHandler: ViewPortHandler?, yAxis: YAxis?, trans: Transformer?) :
    YAxisRenderer(viewPortHandler, yAxis, trans) {
    override fun computeAxisValues(min: Float, max: Float) {
        if (java.lang.Float.isNaN((mYAxis as YAxisComponent).baseValue)) {
            val labelCount = mYAxis.labelCount
            val interval = (max - min) / labelCount
            mYAxis.mEntryCount = 2
            mYAxis.mEntries = FloatArray(2)
            mYAxis.mEntries[0] = min + interval
            mYAxis.mEntries[1] = max - interval
            return
        }
        val base = (mYAxis as YAxisComponent).baseValue
        val labelCount = mYAxis.labelCount
        val interval = (base - min) / labelCount
        val n = labelCount * 2 + 1
        mYAxis.mEntryCount = n
        mYAxis.mEntries = FloatArray(n)
        var i = 0
        var f: Float = min
        while (i < n) {
            mYAxis.mEntries[i] = f
            f += interval
            i++
        }
    }

    override fun renderAxisLabels(c: Canvas) {
        if (!mYAxis.isEnabled || !mYAxis.isDrawLabelsEnabled)
            return

        val positions = transformedPositions

        mAxisLabelPaint.typeface = mYAxis.typeface
        mAxisLabelPaint.textSize = mYAxis.textSize
        mAxisLabelPaint.color = mYAxis.textColor

        val yoffset = Utils.calcTextHeight(mAxisLabelPaint, "A") / 2.5f + mYAxis.yOffset

        val dependency = mYAxis.axisDependency
        val labelPosition = mYAxis.labelPosition

        val xPos: Float

        if (dependency == YAxis.AxisDependency.LEFT) {

            if (labelPosition == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.textAlign = Paint.Align.RIGHT
            } else {
                mAxisLabelPaint.textAlign = Paint.Align.LEFT
            }
            xPos = mViewPortHandler.offsetLeft()


        } else {

            if (labelPosition == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                mAxisLabelPaint.textAlign = Paint.Align.LEFT
            } else {
                mAxisLabelPaint.textAlign = Paint.Align.RIGHT
            }
            xPos = mViewPortHandler.contentRight()
        }

        drawYLabels(c, xPos, positions, yoffset)
    }

    override fun drawYLabels(
        c: Canvas,
        fixedPosition: Float,
        positions: FloatArray,
        offset: Float
    ) {
        for (i in 0 until mYAxis.mEntryCount) {
            var text = mYAxis.getFormattedLabel(i)
            if (!mYAxis.isDrawTopYLabelEntryEnabled && i >= mYAxis.mEntryCount - 1) return
            val labelHeight = Utils.calcTextHeight(mAxisLabelPaint, text)

            var pos = positions[i * 2 + 1] + offset
            if (pos - labelHeight < mViewPortHandler.contentTop()) {
                pos = mViewPortHandler.contentTop() + offset * 2.5f + 3f
            } else if (pos + labelHeight / 2 > mViewPortHandler.contentBottom()) {
                pos = mViewPortHandler.contentBottom() - 3
            }

            try {
                val dataS = text.replace("[^\\d.-]".toRegex(), "")
                if(Omits.isOmit(dataS)){
                    continue
                }
                val data = dataS.toFloat()
                if (text.contains("%")) {
                    when {
                        data == 0f -> {
                            text = text.replace("-", "")
                            mAxisLabelPaint.color =
                                ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.color_text_gray)
                        }
                        data < 0 -> mAxisLabelPaint.color =
                            ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_green)
                        else -> {
                            text = "+$text"
                            mAxisLabelPaint.color =
                                ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_red)
                        }
                    }
                } else {
                    when {
                        data > (mYAxis as YAxisComponent).baseValue -> mAxisLabelPaint.color =
                            ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_red)
                        data < (mYAxis as YAxisComponent).baseValue -> mAxisLabelPaint.color =
                            ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_green)
                        else -> mAxisLabelPaint.color =
                            ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.color_text_gray)
                    }
                }
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
            }

            c.drawText(text, fixedPosition, pos, mAxisLabelPaint)
        }
    }

    override fun renderGridLines(c: Canvas) {

        if (!mYAxis.isEnabled)
            return

        if (mYAxis.isDrawGridLinesEnabled) {

            val clipRestoreCount = c.save()
            c.clipRect(gridClippingRect)

            val positions = transformedPositions

            mGridPaint.color = mYAxis.gridColor
            mGridPaint.strokeWidth = mYAxis.gridLineWidth

            val gridLinePath = mRenderGridLinesPath
            gridLinePath.reset()

            // draw the grid
            var i = 0
            while (i < positions.size) {
                if (!java.lang.Float.isNaN((mYAxis as YAxisComponent).baseValue) && positions.size != 2 && (i == 0 || i == positions.size - 2 || i == (positions.size - 1) / 2))
                    mGridPaint.pathEffect = null
                else
                    mGridPaint.pathEffect = mYAxis.gridDashPathEffect
                c.drawPath(linePath(gridLinePath, i, positions), mGridPaint)
                gridLinePath.reset()
                i += 2
            }

            c.restoreToCount(clipRestoreCount)
        }

        if (mYAxis.isDrawZeroLineEnabled) {
            drawZeroLine(c)
        }
    }


    /**
     * Draws the LimitLines associated with this axis to the screen.
     *
     * @param c
     */
    override fun renderLimitLines(c: Canvas) {

        val limitLines = mYAxis.limitLines

        if (limitLines == null || limitLines.size <= 0)
            return

        val pts = mRenderLimitLinesBuffer
        pts[0] = 0f
        pts[1] = 0f
        val limitLinePath = mRenderLimitLines
        limitLinePath.reset()


        for (i in limitLines.indices) {

            val l = limitLines[i]

            if (!l.isEnabled)
                continue

            val clipRestoreCount = c.save()
            mLimitLineClippingRect.set(mViewPortHandler.contentRect)
            mLimitLineClippingRect.inset(0f, -l.lineWidth)
            c.clipRect(mLimitLineClippingRect)

            mLimitLinePaint.style = Paint.Style.STROKE
            mLimitLinePaint.color = l.lineColor
            mLimitLinePaint.strokeWidth = l.lineWidth
            mLimitLinePaint.pathEffect = l.dashPathEffect

            pts[1] = l.limit

            mTrans.pointValuesToPixel(pts)

            limitLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1])
            limitLinePath.lineTo(mViewPortHandler.contentRight(), pts[1])

            c.drawPath(limitLinePath, mLimitLinePaint)

            limitLinePath.reset()
            // c.drawLines(pts, mLimitLinePaint);

            val label = l.label

            // if drawing the limit-value label is enabled
            if (label != null && label != "") {

                mLimitLinePaint.style = l.textStyle
                mLimitLinePaint.pathEffect = null
                mLimitLinePaint.color = l.textColor
                mLimitLinePaint.typeface = l.typeface
                mLimitLinePaint.strokeWidth = 0.5f
                mLimitLinePaint.textSize = l.textSize

                val labelLineHeight = Utils.calcTextHeight(mLimitLinePaint, label).toFloat()
                val xOffset = Utils.convertDpToPixel(4f) + l.xOffset
                val yOffset = l.lineWidth + labelLineHeight + l.yOffset

                when (l.labelPosition) {
                    LimitLine.LimitLabelPosition.RIGHT_TOP -> {

                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(
                            label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint
                        )

                    }
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM -> {

                        mLimitLinePaint.textAlign = Paint.Align.RIGHT
                        c.drawText(
                            label,
                            mViewPortHandler.contentRight() - xOffset,
                            pts[1] + yOffset, mLimitLinePaint
                        )

                    }
                    LimitLine.LimitLabelPosition.LEFT_TOP -> {

                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(
                            label,
                            mViewPortHandler.contentLeft() + xOffset,
                            pts[1] - yOffset + labelLineHeight, mLimitLinePaint
                        )

                    }
                    else -> {

                        mLimitLinePaint.textAlign = Paint.Align.LEFT
                        c.drawText(
                            label,
                            mViewPortHandler.offsetLeft() + xOffset,
                            pts[1] + yOffset, mLimitLinePaint
                        )
                    }
                }
            }

            c.restoreToCount(clipRestoreCount)
        }
    }
}