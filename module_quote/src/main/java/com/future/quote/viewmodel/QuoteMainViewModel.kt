package com.future.quote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.retrofit.RetrofitUtils
import com.future.quote.event.BaseEvent
import com.future.quote.repository.QuoteHttpReposity
import com.future.quote.repository.QuoteRepositoryProvider
import com.future.quote.repository.QuoteSocketRepository
import com.future.quote.service.InstrumentParser
import com.future.quote.service.QuoteInfoMgr
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.function.Consumer

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 市场界面ViewModel
 */

class QuoteMainViewModel : BaseViewModel<QuoteHttpReposity>(){
    private var _insEvent: MutableLiveData<BaseEvent> = MutableLiveData()
    val insEvent = _insEvent
    private val disposables:CompositeDisposable by lazy{
        CompositeDisposable()
    }

    override fun onCreate() {
        Log.d("QuoteMainViewModel","onCreate")
        repository = QuoteRepositoryProvider.providerHttpRepository()
        //TODO showloading
        loadInstrument()
    }

    private fun loadInstrument(){
        if(repository == null){
            return
        }
        Log.e("QuoteMainViewModel","start down load instrument file")
        disposables.add(repository!!.loadInstruments()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Log.e("QuoteMainViewModel","down load instrument file over,start to parse")
                InstrumentParser().parse(it)
                Log.e("QuoteMainViewModel","parse instrument file over")
                //合约加载完成后，开始连接行情服务
                _insEvent.postValue(BaseEvent(BaseEvent.ACTION_LOAD_INS_OK))
            },{
                Log.e("QuoteMainViewModel","load ins fail",it)
                _insEvent.postValue(BaseEvent(BaseEvent.ACTION_LOAD_INS_FAIL))
            })
        )
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}