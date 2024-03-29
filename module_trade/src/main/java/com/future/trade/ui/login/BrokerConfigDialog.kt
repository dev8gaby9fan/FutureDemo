package com.future.trade.ui.login

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fsh.common.base.BaseDialog
import com.fsh.common.util.Constants
import com.future.trade.R
import com.future.trade.bean.BrokerConfig
import com.future.trade.model.BrokerConfigViewModel
import com.future.trade.util.VerifyUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_broker_config.*
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class BrokerConfigDialog(viewModel: BrokerConfigViewModel) : BaseDialog() {

    override fun getLayoutRes(): Int = R.layout.dialog_broker_config
    private val viewModelRef: WeakReference<BrokerConfigViewModel> = WeakReference(viewModel)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_save.apply {
            setOnClickListener {
                try {
                    VerifyUtil.instance
                        .isBlank(et_broker_name.text, "请输入经纪公司名字")
                        .isBrokerID(et_broker_id.text)
                        .isFrontIP(et_broker_front_ip.text)
                        .isBlank(et_app_id.text, "请输入APPID")
                        .isBlank(et_auth_code.text, "请输入认证码")
                    viewModelRef.get()!!.insertConfig(
                        BrokerConfig(
                            et_broker_name.text.toString(),
                            et_app_id.text.toString(),
                            et_auth_code.text.toString(),
                            Constants.TCP_PROCO + et_broker_front_ip.text.toString(),
                            et_broker_id.text.toString(),
                            "future_demo"
                        )
                    )
                } catch (e: IllegalArgumentException) {
                    Snackbar.make(it, e.message!!, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}