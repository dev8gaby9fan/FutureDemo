package com.future.quote.repository

import com.fsh.common.retrofit.RetrofitUtils

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

object QuoteRepositoryProvider {
    private val instrumentRepository: InstrumentRepository by lazy {
        ShinnyInstrumentRepository(ShinnyHTTPRepository())
    }

    private val quoteScoketReposity: QuoteSocketRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        QuoteSocketRepository()
    }

    fun providerHttpRepository(): InstrumentRepository = instrumentRepository
    fun providerSocketRepository(): QuoteSocketRepository = quoteScoketReposity
}