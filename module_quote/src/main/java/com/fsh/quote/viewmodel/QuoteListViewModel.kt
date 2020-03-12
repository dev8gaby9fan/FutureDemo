package com.fsh.quote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.model.QuoteEntity
import com.fsh.quote.data.SubscribeQuoteFrame
import com.fsh.quote.repository.QuoteRepositoryProvider
import com.fsh.quote.repository.QuoteSocketRepository
import io.reactivex.subjects.Subject

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/12
 * description: 行情列表界面的ViewModel
 *
 */

class QuoteListViewModel : BaseViewModel<QuoteSocketRepository>(){
    val socketStatusEvent: LiveData<Int>
    val quoteDataEvent:Subject<QuoteEntity>
    init {
        repository = QuoteRepositoryProvider.providerSocketRepository()
        socketStatusEvent = repository!!.statusData
        quoteDataEvent = repository!!.quoteData
    }

    fun subscribeQuote(insId:String){
        repository?.sendMessage(SubscribeQuoteFrame(insId))
    }

    fun connectSocket(){
        Log.d("QuoteListViewModel","connectSocket ${repository?.isSocketConnected()!!}")
        if(!repository?.isSocketConnected()!!){
            repository?.connectSocket()
        }
    }
}