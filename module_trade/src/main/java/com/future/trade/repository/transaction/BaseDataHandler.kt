package com.future.trade.repository.transaction

import androidx.lifecycle.LiveData

interface BaseDataHandler<T>{
    fun getLiveData():LiveData<List<T>>
}