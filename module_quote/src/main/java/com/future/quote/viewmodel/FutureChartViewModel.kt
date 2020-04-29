package com.future.quote.viewmodel

import androidx.core.util.Consumer
import com.fsh.common.base.BaseViewModel
import com.fsh.common.util.Omits
import com.future.quote.enums.FutureChartType
import com.future.quote.model.KLineEntity
import com.future.quote.repository.QuoteRepositoryProvider
import com.future.quote.repository.QuoteSocketRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FutureChartViewModel : BaseViewModel<QuoteSocketRepository>(){
    val chartData:Observable<KLineEntity>
    init {
        repository = QuoteRepositoryProvider.providerSocketRepository()
        chartData = repository!!.chartData.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.find {klineEntity->
                    klineEntity.instrumentId == instrumentId && klineEntity.klineDuration == type?.duration }
            }
    }

    private var instrumentId:String? = null
    private var type:FutureChartType? = null
    fun setChart(instrumentId:String,type:FutureChartType,viewWidth:Int= 0){
        repository?.setChart(instrumentId,type,viewWidth)
        this.instrumentId = instrumentId
        this.type = type
    }
}