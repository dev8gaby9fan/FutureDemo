package com.future.quote.repository

import com.fsh.common.retrofit.RetrofitUtils
import com.future.quote.util.Constants
import okhttp3.Request
import okhttp3.Response

class ShinnyHTTPRepository : InstrumentHttpRepository {
    override fun loadInstruments(): Response {
        val httpClient = RetrofitUtils.okHttpClient
        val request = Request.Builder()
            .get()
            .url(Constants.BASE_URL_SHINNYTECH+Constants.URL_LAST_INSTRUMENT)
            .header("Accept","application/json")
            .build()
        return httpClient.newCall(request).execute()
    }
}