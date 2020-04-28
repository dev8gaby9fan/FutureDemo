package com.fsh.common.widget.mpchart.render

import android.graphics.Canvas
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.renderer.LegendRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class LegendComponentRender(viewPortHandler: ViewPortHandler?, legend: Legend?) :
    LegendRenderer(viewPortHandler, legend) {

    override fun renderLegend(c: Canvas) {

        if (!mLegend.isEnabled)
            return

        val tf = mLegend.typeface

        if (tf != null)
            mLegendLabelPaint.typeface = tf

        mLegendLabelPaint.textSize = mLegend.textSize
        mLegendLabelPaint.color = mLegend.textColor

        val labelLineHeight = Utils.getLineHeight(mLegendLabelPaint, legendFontMetrics)
        val labelLineSpacing =
            Utils.getLineSpacing(mLegendLabelPaint, legendFontMetrics) + Utils.convertDpToPixel(
                mLegend.yEntrySpace
            )
        val formYOffset = labelLineHeight - Utils.calcTextHeight(mLegendLabelPaint, "ABC") / 2f

        val entries = mLegend.entries

        val formToTextSpace = Utils.convertDpToPixel(mLegend.formToTextSpace)
        val xEntrySpace = Utils.convertDpToPixel(mLegend.xEntrySpace)
        val orientation = mLegend.orientation
        val horizontalAlignment = mLegend.horizontalAlignment
        val verticalAlignment = mLegend.verticalAlignment
        val direction = mLegend.direction
        val defaultFormSize = Utils.convertDpToPixel(mLegend.formSize)

        // space between the entries
        val stackSpace = Utils.convertDpToPixel(mLegend.stackSpace)

        val yoffset = mLegend.yOffset
        val xoffset = mLegend.xOffset
        var originPosX = 0f

        when (horizontalAlignment) {
            Legend.LegendHorizontalAlignment.LEFT -> {

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = xoffset
                else
                    originPosX = mViewPortHandler.contentLeft() + xoffset

                if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                    originPosX += mLegend.mNeededWidth
            }

            Legend.LegendHorizontalAlignment.RIGHT -> {

                originPosX = if (orientation == Legend.LegendOrientation.VERTICAL)
                    mViewPortHandler.chartWidth - xoffset
                else
                    mViewPortHandler.contentRight() - xoffset

                //直接从最左边开始画
                if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                    originPosX = 0f
            }

            Legend.LegendHorizontalAlignment.CENTER -> {

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = mViewPortHandler.chartWidth / 2f
                else
                    originPosX =
                        mViewPortHandler.contentLeft() + mViewPortHandler.contentWidth() / 2f

                originPosX += if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                    +xoffset
                else
                    -xoffset

                // Horizontally layed out legends do the center offset on a fragment_quote_recommend_line basis,
                // So here we offset the vertical ones only.
                if (orientation == Legend.LegendOrientation.VERTICAL) {
                    originPosX += if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                        -mLegend.mNeededWidth / 2.0F + xoffset
                    else
                        mLegend.mNeededWidth / 2.0F - xoffset
                }
            }
        }

        when (orientation) {
            Legend.LegendOrientation.HORIZONTAL -> {

                val calculatedLineSizes = mLegend.calculatedLineSizes
                val calculatedLabelSizes = mLegend.calculatedLabelSizes
                val calculatedLabelBreakPoints = mLegend.calculatedLabelBreakPoints

                var posX = originPosX
                var posY = 0f

                when (verticalAlignment) {
                    Legend.LegendVerticalAlignment.TOP -> posY = yoffset

                    Legend.LegendVerticalAlignment.BOTTOM -> posY =
                        mViewPortHandler.chartHeight - yoffset - mLegend.mNeededHeight

                    Legend.LegendVerticalAlignment.CENTER -> posY =
                        (mViewPortHandler.chartHeight - mLegend.mNeededHeight) / 2f + yoffset
                }

                var lineIndex = 0

                var i = 0
                val count = entries.size
                while (i < count) {

                    val e = entries[i]
                    val drawingForm = e.form != Legend.LegendForm.NONE
                    val formSize =
                        if (java.lang.Float.isNaN(e.formSize)) defaultFormSize else Utils.convertDpToPixel(
                            e.formSize
                        )

                    if (i < calculatedLabelBreakPoints.size && calculatedLabelBreakPoints[i]) {
                        posX = originPosX
                        posY += labelLineHeight + labelLineSpacing
                    }

                    if (posX == originPosX &&
                        horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER &&
                        lineIndex < calculatedLineSizes.size
                    ) {
                        posX += (if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            calculatedLineSizes[lineIndex].width
                        else
                            -calculatedLineSizes[lineIndex].width) / 2f
                        lineIndex++
                    }

                    val isStacked = e.label == null // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= formSize

                        drawForm(c, posX, posY + formYOffset, e, mLegend)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize
                    }

                    if (!isStacked) {
                        if (drawingForm)
                            posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                                -formToTextSpace
                            else
                                formToTextSpace

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= calculatedLabelSizes[i].width
                        val color = e.formColor
                        drawColoredLabel(c, posX, posY + labelLineHeight, e.label, color)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += calculatedLabelSizes[i].width

                        posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) -xEntrySpace else xEntrySpace

                        //换行
                        if (i != entries.size - 1) {
                            val pos = posX + calculatedLabelSizes[i + 1].width
                            if (pos > mViewPortHandler.contentWidth()) {
                                posX = originPosX
                                posY += labelLineHeight
                            }
                        }
                    } else
                        posX += if (direction == Legend.LegendDirection.RIGHT_TO_LEFT) -stackSpace else stackSpace
                    i++
                }
            }

            Legend.LegendOrientation.VERTICAL -> {
                // contains the stacked legend size in pixels
                var stack = 0f
                var wasStacked = false
                var posY = 0f

                when (verticalAlignment) {
                    Legend.LegendVerticalAlignment.TOP -> {
                        posY = if (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER)
                            0f
                        else
                            mViewPortHandler.contentTop()
                        posY += yoffset
                    }

                    Legend.LegendVerticalAlignment.BOTTOM -> {
                        posY = if (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER)
                            mViewPortHandler.chartHeight
                        else
                            mViewPortHandler.contentBottom()
                        posY -= mLegend.mNeededHeight + yoffset
                    }

                    Legend.LegendVerticalAlignment.CENTER -> posY =
                        mViewPortHandler.chartHeight / 2f - mLegend.mNeededHeight / 2f + mLegend.yOffset
                }

                for (e in entries) {
                    val drawingForm = e.form != Legend.LegendForm.NONE
                    val formSize =
                        if (java.lang.Float.isNaN(e.formSize)) defaultFormSize else Utils.convertDpToPixel(
                            e.formSize
                        )

                    var posX = originPosX

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += stack
                        else
                            posX -= formSize - stack

                        drawForm(c, posX, posY + formYOffset, e, mLegend)

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize
                    }

                    if (e.label != null) {

                        if (drawingForm && !wasStacked)
                            posX += if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                                formToTextSpace
                            else
                                -formToTextSpace
                        else if (wasStacked)
                            posX = originPosX

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= Utils.calcTextWidth(mLegendLabelPaint, e.label).toFloat()
                        val color = e.formColor
                        if (!wasStacked) {
                            drawColoredLabel(c, posX, posY + labelLineHeight, e.label, color)
                        } else {
                            posY += labelLineHeight + labelLineSpacing
                            drawColoredLabel(c, posX, posY + labelLineHeight, e.label, color)
                        }

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing
                        stack = 0f
                    } else {
                        stack += formSize + stackSpace
                        wasStacked = true
                    }
                }

            }
        }
    }

    private fun drawColoredLabel(c: Canvas, x: Float, y: Float, label: String, color: Int) {
        mLegendLabelPaint.color = color
        drawLabel(c, x, y, label)

    }
}