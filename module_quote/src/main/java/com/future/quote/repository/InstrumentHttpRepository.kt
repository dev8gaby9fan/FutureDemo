package com.future.quote.repository

import com.fsh.common.repository.BaseRepository
import com.fsh.common.retrofit.BaseRetrofitApi
import com.future.quote.util.Constants
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 行情服务Http接口
 *
 */

interface InstrumentHttpRepository : BaseRepository {
    /**
     * 加载合约
     */
//    @Headers("Accept:application/json")
//    @GET(Constants.URL_LAST_INSTRUMENT)
    fun loadInstruments(): Response
}