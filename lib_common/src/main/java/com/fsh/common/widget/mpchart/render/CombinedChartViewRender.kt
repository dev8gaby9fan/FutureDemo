package com.fsh.common.widget.mpchart.render

import com.fsh.common.widget.mpchart.CombinedChartView
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.renderer.BubbleChartRenderer
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.ScatterChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class CombinedChartViewRender(
    chart: CombinedChart?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : CombinedChartRenderer(chart, animator, viewPortHandler) {
    var mCandleStickChartRenderer: CandleStickChartViewRender? = null

    override fun createRenderers() {
        mRenderers.clear()
        val chart = mChart.get() ?: return
        val drawerOrders = (chart as CombinedChartView).drawOrder
        for (order in drawerOrders) {
            when (order) {
                CombinedChart.DrawOrder.BAR -> {
                    if (chart.barData != null) {
                        mRenderers.add(BarChartViewRender(chart, mAnimator, mViewPortHandler))
                    }
                }
                CombinedChart.DrawOrder.BUBBLE -> {
                    if (chart.bubbleData != null)
                        mRenderers.add(BubbleChartRenderer(chart, mAnimator, mViewPortHandler))
                }
                CombinedChart.DrawOrder.LINE -> {
                    if (chart.lineData != null)
                        mRenderers.add(LineChartViewRender(chart, mAnimator, mViewPortHandler))
                }
                CombinedChart.DrawOrder.CANDLE -> {
                    if (chart.candleData != null) {
                        mCandleStickChartRenderer = CandleStickChartViewRender(chart, mAnimator, mViewPortHandler)
                        mRenderers.add(mCandleStickChartRenderer)
                    }
                }
                CombinedChart.DrawOrder.SCATTER -> {
                    if (chart.scatterData != null)
                        mRenderers.add(ScatterChartRenderer(chart, mAnimator, mViewPortHandler))
                }
            }
        }
    }
}