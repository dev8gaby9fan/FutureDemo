package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.R
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
        viewModel?.tradeLiveData?.observe(this, Observer {
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): TradeRecordItemVH =
       TradeRecordItemVH(layoutInflater.inflate(R.layout.layout_item_trade,parent,false))


    override fun onBindItemViewHolder(holder: TradeRecordItemVH, position: Int) {

    }

}
class TradeRecordItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)