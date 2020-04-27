package com.future.quote.viewmodel

import com.fsh.common.base.BaseViewModel
import com.fsh.common.util.Omits
import com.future.quote.enums.FutureChartType
import com.future.quote.repository.QuoteRepositoryProvider
import com.future.quote.repository.QuoteSocketRepository

class FutureChartViewModel : BaseViewModel<QuoteSocketRepository>(){
    init {
        repository = QuoteRepositoryProvider.providerSocketRepository()
    }
    private var instrumentId:String? = null
    private var type:FutureChartType? = null
    fun setChart(instrumentId:String,type:FutureChartType,viewWidth:Int= 0){
        repository?.setChart(instrumentId,type,viewWidth)
        this.instrumentId = instrumentId
        this.type = type
    }
}