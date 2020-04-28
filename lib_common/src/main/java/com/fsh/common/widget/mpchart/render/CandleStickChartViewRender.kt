package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.fsh.common.R
import com.fsh.common.util.CommonUtil
import com.fsh.common.widget.mpchart.CombinedChartView
import com.fsh.common.widget.mpchart.component.ValueFormatterComponent
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.renderer.CandleStickChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class CandleStickChartViewRender(
    chart: CandleDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : CandleStickChartRenderer(chart, animator, viewPortHandler) {
    private val mShadowBuffers = FloatArray(8)
    private val mBodyBuffers = FloatArray(4)
    private val mRangeBuffers = FloatArray(4)
    private val mOpenBuffers = FloatArray(4)
    private val mCloseBuffers = FloatArray(4)


    override fun drawDataSet(c: Canvas, dataSet: ICandleDataSet) {

        val trans = mChart.getTransformer(dataSet.axisDependency)

        val phaseY = mAnimator.phaseY
        val barSpace = dataSet.barSpace
        val showCandleBar = dataSet.showCandleBar

        mXBounds.set(mChart, dataSet)

        mRenderPaint.strokeWidth = dataSet.shadowWidth

        // draw the body
        for (j in mXBounds.min..mXBounds.range + mXBounds.min) {

            // get the entry
            val e = dataSet.getEntryForIndex(j) ?: continue

            val xPos = e.x

            val open = e.open
            val close = e.close
            val high = e.high
            val low = e.low

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos
                mShadowBuffers[2] = xPos
                mShadowBuffers[4] = xPos
                mShadowBuffers[6] = xPos

                when {
                    open > close -> {
                        mShadowBuffers[1] = high * phaseY
                        mShadowBuffers[3] = open * phaseY
                        mShadowBuffers[5] = low * phaseY
                        mShadowBuffers[7] = close * phaseY
                    }
                    open < close -> {
                        mShadowBuffers[1] = high * phaseY
                        mShadowBuffers[3] = close * phaseY
                        mShadowBuffers[5] = low * phaseY
                        mShadowBuffers[7] = open * phaseY
                    }
                    else -> {
                        mShadowBuffers[1] = high * phaseY
                        mShadowBuffers[3] = open * phaseY
                        mShadowBuffers[5] = low * phaseY
                        mShadowBuffers[7] = mShadowBuffers[3]
                    }
                }

                trans.pointValuesToPixel(mShadowBuffers)

                // draw the shadows

                if (dataSet.shadowColorSameAsCandle) {

                    if (open > close)
                        mRenderPaint.color =
                            if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE)
                                dataSet.getColor(j)
                            else
                                dataSet.decreasingColor
                    else if (open < close)
                        mRenderPaint.color =
                            if (dataSet.increasingColor == ColorTemplate.COLOR_NONE)
                                dataSet.getColor(j)
                            else
                                dataSet.increasingColor
                    else
                        mRenderPaint.color = if (dataSet.neutralColor == ColorTemplate.COLOR_NONE)
                            dataSet.getColor(j)
                        else
                            dataSet.neutralColor

                } else {
                    mRenderPaint.color = if (dataSet.shadowColor == ColorTemplate.COLOR_NONE)
                        dataSet.getColor(j)
                    else
                        dataSet.shadowColor
                }

                mRenderPaint.style = Paint.Style.STROKE

                c.drawLines(mShadowBuffers, mRenderPaint)

                // calculate the body
                mBodyBuffers[0] = xPos - 0.5f + barSpace
                mBodyBuffers[1] = close * phaseY
                mBodyBuffers[2] = xPos + 0.5f - barSpace
                mBodyBuffers[3] = open * phaseY

                trans.pointValuesToPixel(mBodyBuffers)

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing

                    if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.decreasingColor
                    }

                    mRenderPaint.style = dataSet.decreasingPaintStyle

                    c.drawRect(
                        mBodyBuffers[0], mBodyBuffers[3],
                        mBodyBuffers[2], mBodyBuffers[1],
                        mRenderPaint
                    )

                } else if (open < close) {

                    if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.increasingColor
                    }

                    mRenderPaint.style = dataSet.increasingPaintStyle

                    c.drawRect(
                        mBodyBuffers[0], mBodyBuffers[1],
                        mBodyBuffers[2], mBodyBuffers[3],
                        mRenderPaint
                    )
                } else { // equal values

                    if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.neutralColor
                    }

                    c.drawLine(
                        mBodyBuffers[0], mBodyBuffers[1],
                        mBodyBuffers[2], mBodyBuffers[3],
                        mRenderPaint
                    )
                }
            } else {

                mRangeBuffers[0] = xPos
                mRangeBuffers[1] = high * phaseY
                mRangeBuffers[2] = xPos
                mRangeBuffers[3] = low * phaseY

                mOpenBuffers[0] = xPos - 0.5f + barSpace
                mOpenBuffers[1] = open * phaseY
                mOpenBuffers[2] = xPos
                mOpenBuffers[3] = open * phaseY

                mCloseBuffers[0] = xPos + 0.5f - barSpace
                mCloseBuffers[1] = close * phaseY
                mCloseBuffers[2] = xPos
                mCloseBuffers[3] = close * phaseY

                trans.pointValuesToPixel(mRangeBuffers)
                trans.pointValuesToPixel(mOpenBuffers)
                trans.pointValuesToPixel(mCloseBuffers)

                // draw the ranges
                val barColor: Int

                if (open > close)
                    barColor = if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE)
                        dataSet.getColor(j)
                    else
                        dataSet.decreasingColor
                else if (open < close)
                    barColor = if (dataSet.increasingColor == ColorTemplate.COLOR_NONE)
                        dataSet.getColor(j)
                    else
                        dataSet.increasingColor
                else
                    barColor = if (dataSet.neutralColor == ColorTemplate.COLOR_NONE)
                        dataSet.getColor(j)
                    else
                        dataSet.neutralColor

                mRenderPaint.color = barColor
                c.drawLine(
                    mRangeBuffers[0], mRangeBuffers[1],
                    mRangeBuffers[2], mRangeBuffers[3],
                    mRenderPaint
                )
                c.drawLine(
                    mOpenBuffers[0], mOpenBuffers[1],
                    mOpenBuffers[2], mOpenBuffers[3],
                    mRenderPaint
                )
                c.drawLine(
                    mCloseBuffers[0], mCloseBuffers[1],
                    mCloseBuffers[2], mCloseBuffers[3],
                    mRenderPaint
                )
            }
        }
    }

    override fun drawValues(c: Canvas) {

        val dataSets = mChart.candleData.dataSets

        for (i in dataSets.indices) {

            val dataSet = dataSets[i]

            if (!shouldDrawValues(dataSet))
                continue

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet)

            val trans = (mChart as CombinedChartView).getTransformer(dataSet.axisDependency)

            mXBounds.set(mChart, dataSet)

            val positions = trans.generateTransformedValuesCandle(
                dataSet, mAnimator.phaseX, mAnimator.phaseY, mXBounds.min, mXBounds.max
            )

            val positionsLow = trans.generateTransformedValuesCandleLow(
                dataSet, mAnimator.phaseX, mAnimator.phaseY, mXBounds.min, mXBounds.max
            )

            val yOffset = Utils.convertDpToPixel(5f)

            val formatter = dataSet.valueFormatter as ValueFormatterComponent

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            var entryMin = dataSet.getEntryForIndex(mXBounds.min)
            var xMin = positionsLow[0]
            var yMin = positionsLow[1]

            var entryMax = dataSet.getEntryForIndex(mXBounds.min)
            var xMax = positions[0]
            var yMax = positions[1]
            var j = 0

            while ( j < positions.size) {

                val xHigh = positions[j]
                val yHigh = positions[j + 1]

                val xLow = positionsLow[j]
                val yLow = positionsLow[j + 1]

                if (!mViewPortHandler.isInBoundsRight(xHigh))
                    break

                if (!mViewPortHandler.isInBoundsLeft(xHigh) || !mViewPortHandler.isInBoundsY(yHigh)) {
                    j += 2
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(xLow))
                    break

                if (!mViewPortHandler.isInBoundsLeft(xLow) || !mViewPortHandler.isInBoundsY(yLow)) {
                    j += 2
                    continue
                }

                val entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min)

                if (entry.high > entryMax.high) {
                    entryMax = entry
                    xMax = xHigh
                    yMax = yHigh
                }

                if (entry.low < entryMin.low) {
                    entryMin = entry
                    xMin = xLow
                    yMin = yLow + yOffset
                }
                j += 2

            }

            if (dataSet.isDrawValuesEnabled) {

                drawValue(
                    c,
                    formatter.getCandleLabel(entryMax),
                    xMax,
                    yMax,
                    ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_red)
                )

                drawValue(
                    c,
                    formatter.getCandleLabelLow(entryMin),
                    xMin,
                    yMin,
                    ContextCompat.getColor(CommonUtil.application!!.applicationContext, R.color.quote_green)
                )

            }

            MPPointF.recycleInstance(iconsOffset)
        }
    }

    override fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        mValuePaint.color = color
        val textLeft = "$valueText--->"
        val offset = Utils.calcTextWidth(mValuePaint, textLeft)
        if (x - offset < mViewPortHandler.offsetLeft()) {
            val textRight = "<---$valueText"
            val xNew = x + offset / 2
            c.drawText(textRight, xNew, y, mValuePaint)
        } else {
            val xNew = x - offset / 2
            c.drawText(textLeft, xNew, y, mValuePaint)
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {

        val candleData = mChart.candleData

        for (high in indices) {

            val set = candleData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled)
                continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set))
                continue


            val lowValue = e.low * mAnimator.phaseY
            val highValue = e.high * mAnimator.phaseY
            val pix = mChart.getTransformer(set.axisDependency)
                .getPixelForValues(e.x, (lowValue + highValue) / 2f)
            val xp = pix.x.toFloat()

            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.strokeWidth = set.highlightLineWidth

            val xMax = mViewPortHandler.contentRight()
            val contentBottom = mViewPortHandler.contentBottom()
            //绘制竖线
            c.drawLine(xp, 1f, xp, mChart.height.toFloat(), mHighlightPaint)

            //判断是否画横线
            val y = high.drawY
            if (y in 0F..contentBottom) {//在区域内即绘制横线
                //绘制横线
                c.drawLine(0f, y, xMax, y, mHighlightPaint)
            }
        }
    }
}