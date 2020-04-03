package com.future.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.ext.viewModelOf
import com.future.trade.R
import com.future.trade.bean.RspOrderField
import kotlinx.android.synthetic.main.layout_item_with_draw.view.*

/**
 * 挂单列表界面
 */
class WithDrawRecordFragment :BaseRecordFragment<RspOrderField,WithDrawItemVH>(){
    companion object{
        fun newInstance():WithDrawRecordFragment = WithDrawRecordFragment().apply {
                val args = Bundle()
                arguments = args
            }
    }
    override fun lazyLoading() {
        viewModel?.reqQryOrder()
        viewModel?.withDrawOrderLiveData?.observe(this, Observer{
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): WithDrawItemVH =
        WithDrawItemVH(layoutInflater.inflate(R.layout.layout_item_with_draw,parent,false))


    override fun onBindItemViewHolder(holder: WithDrawItemVH, position: Int) {
        val item = getItem(position)
        holder.itemView.tv_ins_name.text = item?.instrumentID
        holder.itemView.tv_offset.text = item?.combOffsetFlag
        holder.itemView.tv_order_price.text = item?.limitPrice.toString()
        holder.itemView.tv_order_volume.text = item?.volumeTotalOriginal.toString()
        holder.itemView.tv_with_draw.text = (item?.volumeTotalOriginal!! - item.volumeTraded).toString()
    }

}

class WithDrawItemVH(itemView: View) : RecyclerView.ViewHolder(itemView)