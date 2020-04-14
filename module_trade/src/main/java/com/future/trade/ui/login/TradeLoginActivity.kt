package com.future.trade.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.base.BaseActivity
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.future.trade.R
import com.future.trade.bean.BrokerConfig
import com.future.trade.bean.TradeAccountConfig
import com.future.trade.model.LoginViewModel
import com.future.trade.model.BrokerConfigViewModel
import com.future.trade.model.TradeLoginFlowEvent
import com.future.trade.model.TradeLoginFlowType
import com.future.trade.repository.tradeapi.RspConfirmSettlementEvent
import com.future.trade.repository.tradeapi.RspQrySettlementEvent
import com.future.trade.repository.tradeapi.RspUserLoginEvent
import com.future.trade.ui.transaction.TransactionActivity
import com.future.trade.util.VerifyUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_trade_login.*
import java.lang.Exception

@Route(path = ARouterPath.Page.PAGE_TRADE_LOGIN)
class TradeLoginActivity : BaseActivity(), BrokerConfigPicker.OnBrokerItemClickListener,SettlementInfoDialog.Callback {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var brokerConfigViewModel: BrokerConfigViewModel
    private var selectedBroker: BrokerConfig? = null
    private lateinit var brokerDialog: BrokerConfigDialog
    private lateinit var brokerConfigPicker: BrokerConfigPicker
    private lateinit var settlementInfoDialog: SettlementInfoDialog
    override fun layoutRes(): Int = R.layout.activity_trade_login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initViews()
    }

    private fun initData() {
        loginViewModel = viewModelOf<LoginViewModel>().value
        brokerConfigViewModel = viewModelOf<BrokerConfigViewModel>().value
        brokerConfigViewModel.allBrokerLiveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                updateSeletedBroker(it[0])
            }
            brokerConfigPicker.setBrokers(it)
        })
        loginViewModel.loginLiveData.observe(this, Observer {
            handleTradeLoginFlowEvent(it)
        })
    }

    private fun handleTradeLoginFlowEvent(event:TradeLoginFlowEvent){
        if(!event.flowType.isSuccess){
            dismissLoading()
            if(event.flowType == TradeLoginFlowType.RspUserLoginFail){
                val failRsp = (event.event as RspUserLoginEvent).rsp
                Snackbar.make(container,failRsp.rspInfo.errorMsg,Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(container,event.flowType.flowName,Snackbar.LENGTH_SHORT).show()
            }
            return
        }

        when(event.flowType){
            TradeLoginFlowType.FrontConnected ->{}
            TradeLoginFlowType.AuthenSuccess ->{}
            TradeLoginFlowType.RspUserLoginSuccess ->{
                //登录成功
                dismissLoading()
            }
            TradeLoginFlowType.RspQryConfirmSettlementData->{
                //已经确认过了结算单，登录成功
                Snackbar.make(container,"登录成功",Snackbar.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            TradeLoginFlowType.RspSettlementInfo->{
                val tradeEvent = event.event as RspQrySettlementEvent
                //结算单响应数据
                settlementInfoDialog.setSettlementInfo(tradeEvent.rsp.rspField?.content?:"没有数据")
                settlementInfoDialog.show(supportFragmentManager,TAG_SETTLEMENT)
            }
            //确认结算单响应
            TradeLoginFlowType.RspConfirmSettlementInfo->{
                settlementInfoDialog.dismiss()
//                val tradeEvent = event.event as RspConfirmSettlementEvent
//                Snackbar.make(container,tradeEvent.rsp.rspInfoField.errorMsg,Snackbar.LENGTH_SHORT).show()
//                //确认成功
//                if(tradeEvent.rsp.rspInfoField.errorID ==0){
//                    onLoginSuccess()
//                }
                onLoginSuccess()
            }
        }
    }
    private fun onLoginSuccess(){
        finish()
    }

    private fun initViews() {
        brokerDialog = BrokerConfigDialog(brokerConfigViewModel)
        brokerConfigPicker = BrokerConfigPicker()
        //选择弹窗关闭的时候，将drawableRight更新
        brokerConfigPicker.dismissListener = DialogInterface.OnDismissListener {
            updateBrokerSpinnerStatus(false)
        }
        iv_add_spinner.setOnClickListener {
            brokerDialog.show(supportFragmentManager, TAG_ADD)
        }

        broker_spinner.setOnClickListener {
            brokerConfigPicker.show(supportFragmentManager, TAG_PICKER)
            brokerConfigPicker.setOnBrokerItemClickListener(this@TradeLoginActivity)
            //选择弹窗打开的时候，将drawableRight更新
            updateBrokerSpinnerStatus(true)
        }
        settlementInfoDialog = SettlementInfoDialog()
        settlementInfoDialog.callBack = this
        login.apply {
            setOnClickListener {
                try {
                    VerifyUtil.instance
                        .isNotNull(selectedBroker, "请选择经纪公司柜台")
                        .isTradeAccount(username.text)
                        .isPassword(password.text)
                } catch (e: Exception) {
                    Snackbar.make(it, e.message!!, Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                loginViewModel.reqUserLogin(
                    TradeAccountConfig(
                        username.text.toString(),
                        password.text.toString()
                    ), selectedBroker!!
                )
                setLoadingDialogMessage("正在登陆交易(1/4)")
                showLoadingDialog()
            }
        }
    }
    override fun onCancelClick() {
        //取消之后，就把账号退出登录
        loginViewModel.reqUserLogout()
    }

    override fun onEnsureClick() {
        //确认的时候，就显示确认提示窗
       loginViewModel.reqConfirmSettlementInfo()
    }
    override fun onItemClick(
        pos: Int,
        itemView: BrokerConfigPicker.BrokerItem,
        item: BrokerConfig
    ) {
        updateSeletedBroker(item)
    }

    private fun updateBrokerSpinnerStatus(isShowing: Boolean) {
        val drawable =
            resources.getDrawable(if (isShowing) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
        broker_spinner.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

    private fun updateSeletedBroker(broker: BrokerConfig) {
        selectedBroker = broker
        broker_spinner.text = selectedBroker!!.brokerName
    }

//    override fun onBackPressed() {
//        //Nothing to do,不允许返回，只有登录成功才能返回
//    }

    companion object {
        const val TAG_ADD = "brokerAdd"
        const val TAG_PICKER = "brokerPicker"
        const val TAG_SETTLEMENT = "settlementInfo"
    }
}
