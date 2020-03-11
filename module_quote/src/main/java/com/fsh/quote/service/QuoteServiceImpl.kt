package com.fsh.quote.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.model.ArouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.provider.QuoteService
import io.reactivex.Observable

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: TODO there need some info to descript current java file
 *
 */
@Route(path = ArouterPath.SERVICE_QUOTE)
class QuoteServiceImpl : QuoteService{

    override fun init(context: Context?) {

    }

    override fun searchInstruments(searchKey: String): Observable<List<InstrumentInfo>> {
        TODO()
    }



    override fun subscribeQuote(insList: List<String>?): Observable<QuoteEntity> {
        TODO()
    }

}