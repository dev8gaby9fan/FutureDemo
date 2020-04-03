package com.future.trade.util

/**
 * 对比内容是否相同
 */
interface DiffComparable<T>{
    fun compare(obj:T):Boolean
}