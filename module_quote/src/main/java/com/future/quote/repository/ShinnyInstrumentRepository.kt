package com.future.quote.repository

import android.util.Log
import com.future.quote.event.BaseEvent
import com.future.quote.service.ShinnyHttpInstrumentParser
import com.google.gson.stream.JsonReader
import io.reactivex.Single
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2023/6/9
 * description: 合约数据
 *
 */
class ShinnyInstrumentRepository(
    private val httpRepository: InstrumentHttpRepository
) : InstrumentRepository {

    companion object {
        private const val TAG = "ShinnyInstrumentRepository"
    }

    override fun loadLastInstrument(): Single<Int> {
        return Single.create {
            val response = httpRepository.loadInstruments()
            val contentLength = response.body?.contentLength() ?: 0
            Log.i(TAG, "http load instrument httpCode:${response.code},contentLength:${contentLength / 1024f / 1024f}Mb")
            if (!response.isSuccessful) {
                it.onSuccess(BaseEvent.ACTION_LOAD_INS_FAIL)
                return@create
            }
            val result = response.body?.byteStream()?.use { stream -> handleResponse(stream) } ?: BaseEvent.ACTION_LOAD_INS_FAIL
            Log.i(TAG, "handle http response code:$result")
            it.onSuccess(result)
        }
    }

    private fun handleResponse(safeStream: InputStream): Int {
        JsonReader(InputStreamReader(safeStream, "UTF-8")).use {jsonReader ->
            ShinnyHttpInstrumentParser().doParse(jsonReader)
        }
        Log.i(TAG,"parse done.")
        return BaseEvent.ACTION_LOAD_INS_OK
    }
}