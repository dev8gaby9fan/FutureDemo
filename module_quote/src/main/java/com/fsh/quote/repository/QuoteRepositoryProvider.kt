package com.fsh.quote.repository

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
    private val quoteHttpReposity:QuoteHttpReposity by lazy {
        RetrofitUtils.createApi(QuoteHttpReposity::class.java)
    }

    private val quoteScoketReposity:QuoteSocketRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        QuoteSocketRepository()
    }

    fun providerHttpRepository():QuoteHttpReposity = quoteHttpReposity
    fun providerSocketRepository():QuoteSocketRepository = quoteScoketReposity
}