package com.future.quote.repository

import com.fsh.common.repository.BaseRepository
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Response

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2023/6/9
 * description: 合约数据仓库
 *
 */
interface InstrumentRepository : BaseRepository {

    fun loadLastInstrument():Single<Int>
}