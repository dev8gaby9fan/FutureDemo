package com.fsh.common.widget.mpchart.component

import com.github.mikephil.charting.components.YAxis

class YAxisComponent(axis:AxisDependency) : YAxis(axis){
    var baseValue:Float = Float.NaN
}