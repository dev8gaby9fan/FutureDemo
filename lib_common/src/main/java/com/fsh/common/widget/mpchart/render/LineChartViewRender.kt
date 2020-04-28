package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class LineChartViewRender : LineChartRenderer{
    constructor(
        chart: LineDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?
    ) : super(chart, animator, viewPortHandler)

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {

        val lineData = mChart.lineData

        for (high in indices) {
            val set = lineData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled)
                continue

            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set))
                continue

            val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(
                e.x,
                e.y * mAnimator.phaseY
            )
            val xp = pix.x.toFloat()

            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.strokeWidth = set.highlightLineWidth

            val xMax = mViewPortHandler.contentRight()
            //绘制竖线
            c.drawLine(xp, 1f, xp, mChart.height.toFloat(), mHighlightPaint)

            //判断是否画横线
            val y = high.drawY
            if (y >= 0 && y <= mViewPortHandler.contentBottom()) {//在区域内即绘制横线
                //绘制横线
                c.drawLine(0f, y, xMax, y, mHighlightPaint)
            }
        }
    }

    override fun drawValues(c: Canvas) {

        if (isDrawingValuesAllowed(mChart)) {

            val dataSets = mChart.lineData.dataSets

            for (i in dataSets.indices) {

                val dataSet = dataSets[i]

                //ma均线数据为空
                if (dataSet.entryCount == 0) return

                if (!shouldDrawValues(dataSet) || dataSet.entryCount < 1)
                    continue

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet)

                val trans = mChart.getTransformer(dataSet.axisDependency)

                // make sure the values do not interfear with the circles
                var valOffset = (dataSet.circleRadius * 1.75f).toInt()

                if (!dataSet.isDrawCirclesEnabled)
                    valOffset /= 2

                mXBounds.set(mChart, dataSet)

                val positions = trans.generateTransformedValuesLine(
                    dataSet, mAnimator.phaseX, mAnimator
                        .phaseY, mXBounds.min, mXBounds.max
                )
                val formatter = dataSet.valueFormatter

                val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

                var j = 0
                while (j < positions.size) {

                    val x = positions[j]
                    val y = positions[j + 1]

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) {
                        j += 2
                        continue
                    }

                    val entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min)

                    if (dataSet.isDrawValuesEnabled) {
                        drawValue(
                            c,
                            formatter.getPointLabel(entry),
                            x,
                            y - valOffset,
                            dataSet.getValueTextColor(j / 2)
                        )
                    }

                    if (entry.icon != null && dataSet.isDrawIconsEnabled) {

                        val icon = entry.icon

                        Utils.drawImage(
                            c,
                            icon,
                            (x + iconsOffset.x).toInt(),
                            (y + iconsOffset.y).toInt(),
                            icon.intrinsicWidth,
                            icon.intrinsicHeight
                        )
                    }
                    j += 2
                }

                MPPointF.recycleInstance(iconsOffset)
            }
        }
    }
}