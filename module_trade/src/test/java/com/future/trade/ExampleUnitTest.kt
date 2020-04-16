package com.future.trade

import androidx.core.text.isDigitsOnly
import com.future.trade.util.VerifyUtil
import org.junit.Test

import org.junit.Assert.*
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val map = HashMap<Int,Int>()
        for(index in 0 until 100){
            map[index] = index
        }
//        map.values.asIterable().forEach {
//            if(it %3 ==0){
//                map.remove(it)
//            }
//        }
        println("map size ${map.size}")
//        for(entity in map.entries){
//            if(entity.key %3 ==0){
//                map.remove(entity.key)
//            }
//        }
        val iterator = map.iterator()
        while (iterator.hasNext()){
            val next = iterator.next()
            if(next.key %3 ==0){
                iterator.remove()
            }
        }
        println("map size ${map.size}")
    }
}
