package com.fsh.trade.ui.config

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fsh.common.base.BaseViewModel
import com.fsh.common.util.SPUtils
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.repository.TradeApiProvider
import com.fsh.trade.repository.config.BrokerConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BrokerConfigViewModel : BaseViewModel<BrokerConfigRepository>(){

    init {
        repository = TradeApiProvider.providerConfigRepository()
    }

    fun insertConfig(brokerConfig: BrokerConfig){
        viewModelScope.launch(Dispatchers.IO) {
            repository!!.saveBroker(brokerConfig)
            loadDbConfigs()
        }
    }

    fun loadDbConfigs(): LiveData<List<BrokerConfig>> = repository!!.getBrokers()
}