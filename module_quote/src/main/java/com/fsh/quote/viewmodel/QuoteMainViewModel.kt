package com.fsh.quote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.quote.event.BaseEvent
import com.fsh.quote.repository.QuoteHttpReposity
import com.fsh.quote.service.InstrumentParser
import com.fsh.quote.service.QuoteInfoMgr
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
        repository = RetrofitUtils.createApi(QuoteHttpReposity::class.java)
        //TODO showloading
        loadInstrument()
    }

    fun loadInstrument(){
        if(repository == null){
            return
        }
        disposables.add(repository!!.loadInstruments()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                InstrumentParser().parse(it)
                _insEvent.postValue(BaseEvent(BaseEvent.ACTION_LOAD_INS_OK))
            },{
                //TODO dismissLoading
                _insEvent.postValue(BaseEvent(BaseEvent.ACTION_LOAD_INS_FAIL))
            })
        )
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}