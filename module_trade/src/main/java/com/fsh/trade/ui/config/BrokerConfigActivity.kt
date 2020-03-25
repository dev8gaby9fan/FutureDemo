package com.fsh.trade.ui.config

import android.os.Bundle
import com.fsh.common.base.BaseActivity
import com.fsh.common.ext.viewModelOf
import com.fsh.trade.R
import com.fsh.trade.bean.BrokerConfig
import com.fsh.trade.util.VerifyUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_broker_config.*
import java.lang.IllegalArgumentException

class BrokerConfigActivity : BaseActivity() {
    private lateinit var viewModel:BrokerConfigViewModel
    override fun layoutRes(): Int = R.layout.activity_broker_config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelOf<BrokerConfigViewModel>().value
        btn_save.apply{
            setOnClickListener {
                try{
                    VerifyUtil.instance
                        .isBlank(et_broker_name.text,"请输入经纪公司名字")
                        .isBrokerID(et_broker_id.text)
                        .isFrontIP(et_broker_front_ip.text)
                        .isBlank(et_app_id.text,"请输入APPID")
                        .isBlank(et_auth_code.text,"请输入认证码")
                    viewModel.insertConfig(BrokerConfig(et_broker_name.text.toString(),et_app_id.text.toString(),
                        et_auth_code.text.toString(),et_broker_front_ip.text.toString(),
                        et_broker_id.text.toString(),"future_demo"))
                    finish()
                }catch (e:IllegalArgumentException){
                    Snackbar.make(it,e.message!!,Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
