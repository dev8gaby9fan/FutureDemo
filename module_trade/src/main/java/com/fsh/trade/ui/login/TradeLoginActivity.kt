package com.fsh.trade.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.fsh.common.base.BaseActivity
import com.fsh.common.base.ViewModelFactory
import com.fsh.common.ext.viewModelOf
import com.fsh.trade.R
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.bean.TradeAccountConfig
import com.fsh.trade.ui.config.BrokerConfigActivity
import com.fsh.trade.ui.config.BrokerConfigViewModel
import com.fsh.trade.util.VerifyUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_trade_login.*
import java.lang.Exception

class TradeLoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var brokerConfigViewModel:BrokerConfigViewModel
    private var account: TradeAccountConfig = TradeAccountConfig("085739", "123456")
    private var selectedBroker:BrokerConfig? = null
    private lateinit var brokerDialog: BrokerConfigDialog
    private lateinit var brokerConfigPicker: BrokerConfigPicker
    override fun layoutRes(): Int = R.layout.activity_trade_login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = viewModelOf<LoginViewModel>().value
        brokerConfigViewModel = viewModelOf<BrokerConfigViewModel>().value
        brokerConfigViewModel.allBrokerLiveData.observe(this, Observer {
            Log.d("TradeLoginActivity", "received broker list from db ${it.size}")
            if(it.isNotEmpty()){
                updateSeletedBroker(it[0])
            }
            brokerConfigPicker.setBrokers(it)
        })
        brokerDialog = BrokerConfigDialog(brokerConfigViewModel)
        brokerConfigPicker = BrokerConfigPicker()
        brokerConfigPicker.setOnBrokerItemClickListener(object : BrokerConfigPicker.OnBrokerItemClickListener{
            override fun onItemClick(
                pos: Int,
                itemView: BrokerConfigPicker.BrokerItem,
                item: BrokerConfig
            ) {
                updateSeletedBroker(item)
            }

        })
        //选择弹窗关闭的时候，将drawableRight更新
        brokerConfigPicker.dismissListener = DialogInterface.OnDismissListener {
            updateBrokerSpinnerStatus(false)
        }
        iv_add_spinner.setOnClickListener {
            brokerDialog.show(supportFragmentManager, TAG_ADD)
        }

        broker_spinner.setOnClickListener {
            brokerConfigPicker.show(supportFragmentManager, TAG_PICKER)
            //选择弹窗打开的时候，将drawableRight更新
            updateBrokerSpinnerStatus(true)
        }

        login.apply {
            setOnClickListener {
                try{
                    VerifyUtil.instance
                        .isNotNull(selectedBroker,"请选择经纪公司柜台")
                        .isTradeAccount(username.text)
                        .isPassword(password.text)
                }catch (e:Exception){
                    Snackbar.make(it,e.message!!,Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                loginViewModel.reqUserLogin(account, selectedBroker!!)
            }
        }
    }

    fun updateBrokerSpinnerStatus(isShowing:Boolean){
        val drawable = resources.getDrawable(if(isShowing) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
        broker_spinner.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null)
    }

    fun updateSeletedBroker(broker:BrokerConfig){
        selectedBroker = broker
        broker_spinner.text = selectedBroker!!.brokerName
    }

    companion object {
        const val TAG_ADD = "brokerAdd"
        const val TAG_PICKER = "brokerPicker"
    }
}
