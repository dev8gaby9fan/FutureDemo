package com.fsh.common.widget.mpchart.component

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class TransformerComponent(viewPortHandler: ViewPortHandler?) : Transformer(viewPortHandler) {
    private var valuePointsForGenerateTransformedValuesCandleLow = floatArrayOf()

    fun generateTransformedValuesCandleLow(data:ICandleDataSet,phaseX:Float,phaseY:Float,from:Int,to:Int):FloatArray{
        val count = (((to-from) * phaseX + 1)*2).toInt()
        if(valuePointsForGenerateTransformedValuesCandleLow.size != count){
            valuePointsForGenerateTransformedValuesCandleLow = FloatArray(count)
        }
        val valuesPoints = valuePointsForGenerateTransformedValuesCandleLow
        for(index in 0 until count step 2){
            val candleEntry = data.getEntryForIndex(index/2+from)
            if(candleEntry != null){
                valuesPoints[index] = candleEntry.x
                valuesPoints[index+1] = candleEntry.low * phaseY
            }else{
                valuesPoints[index] = 0F
                valuesPoints[index+1] = 0F
            }
        }
        valueToPixelMatrix.mapPoints(valuesPoints)
        return valuesPoints
    }
}