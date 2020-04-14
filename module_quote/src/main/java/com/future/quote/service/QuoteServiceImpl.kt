package com.future.quote.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.provider.QuoteService
import com.future.quote.data.SubscribeQuoteFrame
import com.future.quote.repository.QuoteRepositoryProvider
import io.reactivex.Observable

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 行情模块对外提供数据服务
 *
 */
@Route(path = ARouterPath.Service.SERVICE_QUOTE)
class QuoteServiceImpl : QuoteService{


    override fun getSubscribeQuoteObservable(): Observable<QuoteEntity> =
        QuoteRepositoryProvider.providerSocketRepository().quoteData


    override fun getQuoteByInstrument(instrumentId: String?):QuoteEntity? =
        QuoteInfoMgr.mgr.getQuoteEntity(instrumentId)

    override fun init(context: Context?) {

    }

    override fun searchInstruments(searchKey: String): Observable<List<InstrumentInfo>> {
        TODO()
    }

    override fun subscribeQuote(insList: String, isAppend: Boolean) {
        QuoteRepositoryProvider.providerSocketRepository().subscribeQuote(insList,isAppend)
    }
}