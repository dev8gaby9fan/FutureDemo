package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import com.fsh.common.base.BaseFragment
import com.fsh.trade.R
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment :BaseFragment(){
    override fun layoutRes(): Int = R.layout.fragment_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_sell.setOnClickListener {
            Log.d("TransactionFragment","sell button clicked")
        }
    }
}