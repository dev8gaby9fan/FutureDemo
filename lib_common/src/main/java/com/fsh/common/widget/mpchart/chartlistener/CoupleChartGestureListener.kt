package com.fsh.common.widget.mpchart.chartlistener

import android.graphics.Matrix
import android.view.MotionEvent
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

class CoupleChartGestureListener (private val srcChart:Chart<*>,private val dstCharts:List<Chart<*>>): OnChartGestureListener{

    override fun onChartGestureStart(
        me: MotionEvent,
        lastPerformedGesture: ChartTouchListener.ChartGesture
    ) {
        syncCharts()

    }

    override fun onChartGestureEnd(
        me: MotionEvent,
        lastPerformedGesture: ChartTouchListener.ChartGesture
    ) {
        syncCharts()
    }

    override fun onChartLongPressed(me: MotionEvent) {
        syncCharts()
    }

    override fun onChartDoubleTapped(me: MotionEvent) {
        syncCharts()
    }

    override fun onChartSingleTapped(me: MotionEvent) {
        syncCharts()
    }

    override fun onChartFling(
        me1: MotionEvent,
        me2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ) {
        syncCharts()
    }

    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) = syncCharts()

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) = syncCharts()

//    //以下5个方法仅为了：方便在外部根据需要自行重写
//    fun chartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) = syncCharts()
//
//    fun chartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) = syncCharts()
//
//    fun chartLongPressed(me: MotionEvent) = syncCharts()
//
//    fun chartDoubleTapped(me: MotionEvent) = syncCharts()
//
//    fun chartSingleTapped(me: MotionEvent) = syncCharts()
//
//    fun chartTranslate(me: MotionEvent, dX: Float, dY: Float) = syncCharts()

    private fun syncCharts() {
        val srcMatrix: Matrix = srcChart.viewPortHandler.matrixTouch
        val srcVals = FloatArray(9)
        var dstMatrix: Matrix
        val dstVals = FloatArray(9)
        // get src chart translation matrix:
        srcMatrix.getValues(srcVals)
        // apply X axis scaling and position to dst charts:
        for (dstChart in dstCharts) {
            dstMatrix = dstChart.viewPortHandler.matrixTouch
            dstMatrix.getValues(dstVals)

            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X]
            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X]
            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X]
            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y]
            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y]
            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y]
            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0]
            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1]
            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2]

            dstMatrix.setValues(dstVals)
            dstChart.viewPortHandler.refresh(dstMatrix, dstChart, true)
        }
    }
}