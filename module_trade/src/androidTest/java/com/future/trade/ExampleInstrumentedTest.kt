package com.future.trade

import androidx.core.text.isDigitsOnly
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.future.trade.util.VerifyUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.regex.Pattern

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.future.trade.test", appContext.packageName)
    }

    @Test
    fun test(){
        println("=====>"+Pattern.matches(VerifyUtil.PATTERN_NUM,"1982."))
//        println("=====>"+Pattern.matches(VerifyUtil.PATTERN_NUM,"1982.12"))
//        println("=====>"+Pattern.matches(VerifyUtil.PATTERN_NUM,"1982.1"))
//        val s1 = "129.112"
//        val s2 = "129.000"
//        val s3 = "129."
//        println("=====>129.112.isDigitsOnly${s1.isDigitsOnly()}")
//        println("=====>129.000.isDigitsOnly${s2.isDigitsOnly()}")
//        println("=====>129..isDigitsOnly${s3.isDigitsOnly()}")
    }
}
