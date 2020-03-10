package com.fsh.quote.service

import com.fsh.common.retrofit.BaseRetrofitApi
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.quote.util.Constants
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 行情服务Http接口
 *
 */

interface QuoteReposity : BaseRetrofitApi {
    /**
     * 加载合约
     */
    @GET(Constants.URL_LAST_INSTRUMENT)
    fun loadInstruments():Observable<JsonObject>
}