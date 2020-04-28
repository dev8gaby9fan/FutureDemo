package com.fsh.common.widget.mpchart.component

import android.util.SparseArray
import com.github.mikephil.charting.components.XAxis

class XAxisComponent : XAxis() {
    var xLabels:SparseArray<String> = SparseArray()
}