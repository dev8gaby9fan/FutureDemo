package com.fsh.trade.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.ViewModelFactory
import com.fsh.common.ext.viewModelOf
import com.fsh.trade.R
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import com.fsh.trade.ui.config.BrokerConfigActivity
import kotlinx.android.synthetic.main.activity_trade_login.*

class TradeLoginActivity : BaseActivity() {

    private lateinit var loginViewModel:LoginViewModel
    private var account:TradeAccountConfig = TradeAccountConfig("085739","123456")
    private var brokerConfig:BrokerConfig = BrokerConfig("simnow_client_test","0000000000000000",
        "tcp://180.168.146.187:10130","9999","future_demo")

    override fun layoutRes(): Int = R.layout.activity_trade_login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = viewModelOf<LoginViewModel>().value

        login.apply {
            setOnClickListener {
//                loginViewModel.reqUserLogin(account,brokerConfig)
                startActivity(Intent(this@TradeLoginActivity,BrokerConfigActivity::class.java))
            }
        }
    }
}
