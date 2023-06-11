package com.future.quote.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fsh.common.base.BaseViewModel
import com.future.quote.event.BaseEvent
import com.future.quote.repository.InstrumentRepository
import com.future.quote.repository.QuoteRepositoryProvider
import com.google.gson.stream.JsonReader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 市场界面ViewModel
 */

class QuoteMainViewModel : BaseViewModel<InstrumentRepository>() {
    private var _insEvent: MutableLiveData<BaseEvent> = MutableLiveData()
    val insEvent = _insEvent
    private val disposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate() {
        Log.d("QuoteMainViewModel", "onCreate")
        repository = QuoteRepositoryProvider.providerHttpRepository()
        //TODO showloading
        loadInstrument()
    }

    private fun loadInstrument() {
        if (repository == null) {
            return
        }
        Log.e("QuoteMainViewModel", "start down load instrument file")
        val disposable = repository?.loadLastInstrument()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.subscribe({
                Log.i("QuoteMainViewModel", "load instrument result $it")
//                InstrumentParser().parse(string)
//                Log.e("QuoteMainViewModel","parse instrument file over")
//                //合约加载完成后，开始连接行情服务
                _insEvent.postValue(BaseEvent(it))
            }, {
                it.printStackTrace()
                Log.e("QuoteMainViewModel", "load ins fail")
                _insEvent.postValue(BaseEvent(BaseEvent.ACTION_LOAD_INS_FAIL))
            })
        disposable?.let(disposables::add)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}