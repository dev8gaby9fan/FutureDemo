package com.future.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.future.trade.R
import com.future.trade.bean.RspTradeField
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType
import kotlinx.android.synthetic.main.layout_item_trade.view.*

/**
 * 成交列表Fragment
 */
class TradeRecordFragment : BaseRecordFragment<RspTradeField,CommonTradeRecordVH>(){


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

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): CommonTradeRecordVH =
       TradeRecordItemVH(layoutInflater.inflate(R.layout.layout_item_trade,parent,false))

    override fun createHeadViewHolder(parent: ViewGroup): CommonTradeRecordVH =
        TradeRecordHeadVH(layoutInflater.inflate(R.layout.layout_item_trade,parent,false))

    override fun onBindHeadViewHolder(holder: CommonTradeRecordVH) {

    }


    override fun onBindItemViewHolder(holder: CommonTradeRecordVH, position: Int) {
        val record = getItem(position)
        holder.itemView.tv_ins_name.text = record!!.instrumentID
        holder.itemView.tv_direct.text = CTPDirection.from(record.direction)?.text
        holder.itemView.tv_offset.text = CTPCombOffsetFlag.from(record.offsetFlag).text
        holder.itemView.tv_price.text = record.price.toString()
        holder.itemView.tv_volume.text = record.volume.toString()
        holder.itemView.tv_hedge.text = CTPHedgeType.from(record.hedgeFlag)?.text
        holder.itemView.tv_time.text = record.tradeTime
    }

}
abstract class CommonTradeRecordVH(itemView:View) : RecyclerView.ViewHolder(itemView)

class TradeRecordHeadVH(itemView:View) : CommonTradeRecordVH(itemView)

class TradeRecordItemVH(itemView:View) : CommonTradeRecordVH(itemView)