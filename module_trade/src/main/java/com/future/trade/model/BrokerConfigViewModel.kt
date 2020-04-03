package com.future.trade.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fsh.common.base.BaseViewModel
import com.fsh.common.util.SPUtils
import com.future.trade.bean.BrokerConfig
import com.future.trade.repository.TradeApiProvider
import com.future.trade.repository.config.BrokerConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BrokerConfigViewModel : BaseViewModel<BrokerConfigRepository>(){
    val allBrokerLiveData:LiveData<List<BrokerConfig>>
    init {
        repository = TradeApiProvider.providerConfigRepository()
        allBrokerLiveData = repository!!.getBrokers()
    }

    fun insertConfig(brokerConfig: BrokerConfig){
        viewModelScope.launch(Dispatchers.IO) {
            repository!!.saveBroker(brokerConfig)
        }
    }
}