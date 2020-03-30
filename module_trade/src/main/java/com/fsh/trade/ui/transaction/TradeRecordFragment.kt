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
import com.fsh.trade.enums.CTPCombOffsetFlag
import com.fsh.trade.enums.CTPDirection
import com.fsh.trade.enums.CTPHedge
import kotlinx.android.synthetic.main.layout_item_trade.view.*

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
            Log.d("TradeRecordFragment","received trade live data ${it.size}")
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): TradeRecordItemVH =
       TradeRecordItemVH(layoutInflater.inflate(R.layout.layout_item_trade,parent,false))


    override fun onBindItemViewHolder(holder: TradeRecordItemVH, position: Int) {
        val record = getItem(position)
        holder.itemView.tv_ins_name.text = record!!.instrumentID
        holder.itemView.tv_direct.text = CTPDirection.from(record.direction)?.text
        holder.itemView.tv_offset.text = CTPCombOffsetFlag.from(record.offsetFlag).text
        holder.itemView.tv_price.text = record.price.toString()
        holder.itemView.tv_volume.text = record.volume.toString()
        holder.itemView.tv_hedge.text = CTPHedge.from(record.hedgeFlag)?.text
        holder.itemView.tv_time.text = record.tradeTime
    }

}
class TradeRecordItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)