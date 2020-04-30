package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

class BarChartViewRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val mBarShadowRectBuffer = RectF()
    //画线型成交量图
    private val mLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mLinePaint.style = Paint.Style.STROKE
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {

        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        mLinePaint.strokeWidth = Utils.convertDpToPixel(0.7f)
        mRenderPaint.strokeWidth = Utils.convertDpToPixel(0.7f)

        mHighlightPaint.strokeWidth = Utils.convertDpToPixel(0.7f)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val barData = mChart.barData
        val barWidth = barData.barWidth

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor

            val barWidthHalf = barWidth / 2.0f
            var x: Float

            var i = 0
            val count = min(ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt(), dataSet.entryCount)
            while (i < count) {

                val e = dataSet.getEntryForIndex(i)

                x = e.x

                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf

                trans.rectValueToPixel(mBarShadowRectBuffer)

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                c.drawRect(mBarShadowRectBuffer, mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
            mLinePaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break

            if (!isSingleColor) {
                val e = dataSet.getEntryForIndex(j / 4)
                val sub = if (e != null) e.data as Float else 0.0f

                //线和柱子的颜色
                if (barWidth == 0.01f) {
                    if (sub > 0) {
                        mLinePaint.color = dataSet.getColor(0)
                    } else {
                        mLinePaint.color = dataSet.getColor(1)
                    }
                } else {
                    if (sub > 0) {
                        mRenderPaint.color = dataSet.getColor(0)
                        mRenderPaint.style = Paint.Style.FILL
                    } else {
                        mRenderPaint.color = dataSet.getColor(1)
                        mRenderPaint.style = Paint.Style.STROKE
                    }
                }
            }

            //成交量是画线还是画柱子
            if (barWidth == 0.01f) {
                val x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2
                val startY = buffer.buffer[j + 3]
                val stopY = buffer.buffer[j + 1]
                c.drawLine(x, startY, x, stopY, mLinePaint)
            } else {
                val height = buffer.buffer[j + 1] - buffer.buffer[j + 3]
                if (height == 0f) {
                    c.drawLine(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                } else {
                    c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                }

            }

            if (drawBorder) {
                c.drawRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mBarBorderPaint
                )
            }
            j += 4
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        val barData = mChart.barData

        for (high in indices) {

            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled)
                continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set))
                continue

            mHighlightPaint.color = set.highLightColor
            //保持和顶层图的十字光标一致
            //mHighlightPaint.setAlpha(set.getHighLightAlpha());

            val barWidth = barData.barWidth
            val trans = mChart.getTransformer(set.axisDependency)
            prepareBarHighlight(e.x, 0f, 0f, barWidth / 2, trans)

            //画竖线
            val xp = mBarRect.centerX()
            c.drawLine(xp, mViewPortHandler.contentRect.bottom, xp, 0f, mHighlightPaint)

            //判断是否画横线
            val y = high.drawY
            val yMax = mChart.height.toFloat()
            val xMax = mChart.width.toFloat()
            if (y in 0F..yMax) {//在区域内即绘制横线
                //绘制横线
                c.drawLine(0f, y, xMax, y, mHighlightPaint)
            }
        }
    }
}