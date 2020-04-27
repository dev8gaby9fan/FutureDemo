package com.future.quote.viewmodel

import com.fsh.common.base.BaseViewModel
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.Omits
import com.future.quote.repository.QuoteRepositoryProvider
import com.future.quote.repository.QuoteSocketRepository
import io.reactivex.Observable

class FutureQuoteViewModel : BaseViewModel<QuoteSocketRepository>(){
    val quoteData:Observable<QuoteEntity>
    private var instrumentId:String = Omits.OmitString
    init {
        repository = QuoteRepositoryProvider.providerSocketRepository()
        quoteData = repository!!.quoteData.filter{it.instrument_id == instrumentId}
    }

    fun subscribeQuote(instrumentId:String){
        this.instrumentId = instrumentId
        repository?.subscribeQuote(instrumentId)
    }
}