package com.future.trade.ui.login

import android.os.Bundle
import android.view.View
import com.fsh.common.base.BaseBottomSheetDialog
import com.fsh.common.util.Omits
import com.future.trade.R
import kotlinx.android.synthetic.main.dialog_settlement_info.*

/**
 * 结算单信息弹出
 */
class SettlementInfoDialog : BaseBottomSheetDialog(){
    private var settlementInfo:String = Omits.OmitString
    var callBack:Callback? = null
    override fun getLayoutRes(): Int = R.layout.dialog_settlement_info

    override fun getBehaviorPeekHeight(): Float = 1f

    override fun enableBottomSheetBehavior(): Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_message.text = settlementInfo
        btn_cancel.setOnClickListener {
            callBack?.onCancelClick()
            dismiss()
        }
        btn_ensure.setOnClickListener {
            callBack?.onEnsureClick()
        }
    }

    fun setSettlementInfo(info:String){
        settlementInfo = info
        if(isShowing){
            tv_message.text = settlementInfo
        }
    }

    interface Callback{
        fun onEnsureClick()

        fun onCancelClick()
    }
}