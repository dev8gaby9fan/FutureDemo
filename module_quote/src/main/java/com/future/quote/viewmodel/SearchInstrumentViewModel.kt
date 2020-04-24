package com.future.quote.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fsh.common.base.BaseViewModel
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.repository.BaseRepository
import com.fsh.common.util.Omits
import com.future.quote.service.QuoteInfoMgr
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SearchInstrumentViewModel : BaseViewModel<BaseRepository>(){
    private var searchJob:Job? = null
    private val _searchInsLiveData:MutableLiveData<List<InstrumentInfo>> = MutableLiveData()
    val searchInsLiveData = _searchInsLiveData

    fun searchInstrument(key:String?){
        //空字符串就不查询
        if(Omits.isOmit(key)){
            _searchInsLiveData.value = emptyList()
            return
        }
        if(searchJob != null && !searchJob!!.isCancelled && !searchJob!!.isCompleted){
            searchJob?.cancel()
        }
        searchJob = GlobalScope.launch {
            val list = QuoteInfoMgr.mgr.searchIns(key!!)
            _searchInsLiveData.postValue(list)
        }
    }

    override fun onDestroy() {
        if(searchJob != null && !searchJob!!.isCancelled && !searchJob!!.isCompleted){
            searchJob?.cancel()
            searchJob = null
        }
        super.onDestroy()
    }

}