package com.future.trade

import androidx.core.text.isDigitsOnly
import com.future.trade.util.VerifyUtil
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val num1 = BigDecimal(1.0).toString()
        val num2 = BigDecimal("1").toString()
        println("num1=$num1,num2=$num2")
    }
}
