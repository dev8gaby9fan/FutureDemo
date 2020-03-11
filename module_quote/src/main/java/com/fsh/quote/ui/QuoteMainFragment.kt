package com.fsh.quote.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.fsh.common.base.BaseFragment
import com.fsh.common.ext.viewModelOf
import com.fsh.common.retrofit.RetrofitUtils
import com.fsh.quote.R
import com.fsh.quote.event.BaseEvent
import com.fsh.quote.repository.QuoteHttpReposity
import com.fsh.quote.viewmodel.QuoteMainViewModel

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: TODO there need some info to descript current java file
 *
 */

class QuoteMainFragment : BaseFragment(){
    override fun layoutRes(): Int = R.layout.fragment_quote
    private lateinit var quoteMainViewModel:QuoteMainViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val beginTransaction = childFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.fl_quote_menu,QuoteMenuFragment()).commitNow()
        quoteMainViewModel = viewModelOf<QuoteMainViewModel>().value
        quoteMainViewModel.insEvent.observe(this, Observer<BaseEvent> {
                handleLoadInsResp(it)
            })
    }

    private fun handleLoadInsResp(event:BaseEvent){
        Log.d("QuoteMainFragment","${Thread.currentThread().name} load ins resp ${event.action}")
    }
}