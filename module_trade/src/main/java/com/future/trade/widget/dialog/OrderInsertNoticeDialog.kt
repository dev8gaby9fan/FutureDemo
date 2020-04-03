package com.future.trade.widget.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fsh.common.base.BaseDialog
import com.fsh.common.util.Omits
import com.future.trade.R
import kotlinx.android.synthetic.main.alt_order_insert_notice.*
import kotlinx.android.synthetic.main.alt_order_insert_notice.btn_cancel
import kotlinx.android.synthetic.main.alt_order_insert_notice.tv_message

class OrderInsertNoticeDialog : BaseDialog(){
    private var message:String = Omits.OmitString
    var listener:OrderInsertNoticeViewListener? = null
    override fun getLayoutRes(): Int = R.layout.alt_order_insert_notice

    fun setMessage(msg:String){
        message = msg
        if(isLoading){
            tv_message.text = message
        }

    }

    fun showDialog(fmg:FragmentManager,msg:String){
        show(fmg,null)
        message = msg
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_message.text = message
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_insert.setOnClickListener {
            listener?.onInsertClick()
        }
    }

    interface OrderInsertNoticeViewListener{
        fun onInsertClick()
    }
}