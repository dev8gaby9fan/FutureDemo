package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.bean.RspQryTrade
import com.fsh.trade.bean.RspTradeField

/**
 * 成交列表Fragment
 */
class TradeRecordFragment : BaseRecordFragment<RspTradeField,TradeRecordItemVH>(){
    companion object{
        @JvmStatic
        fun newInstance():TradeRecordFragment = TradeRecordFragment().apply {
            val args = Bundle()
            arguments = args
        }
    }
    override fun lazyLoading() {
        viewModel?.reqQryTrade()
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): TradeRecordItemVH {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindItemViewHolder(holder: TradeRecordItemVH, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
class TradeRecordItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)