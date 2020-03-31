package com.fsh.common

import com.fsh.common.util.NumberUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testNumberUtils(){
        println(NumberUtils.formatNum(298.0001,0.001))
        println(NumberUtils.formatNum(298.0001,0.001))
        println(NumberUtils.formatNum(298.0001,0.01))
        println(NumberUtils.formatNum(298.0001,0.1))
        println(NumberUtils.formatNum(298.0001,1.0))
    }
}
