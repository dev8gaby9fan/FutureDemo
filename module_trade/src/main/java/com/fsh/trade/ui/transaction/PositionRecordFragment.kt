package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.R
import com.fsh.trade.bean.InstrumentPosition

/**
 * 持仓列表
 */
class PositionRecordFragment : BaseRecordFragment<InstrumentPosition,PositionItemViewHolder>(){

    companion object{
        @JvmStatic
        fun newInstance():PositionRecordFragment = PositionRecordFragment().apply {
                val args = Bundle()
                arguments = args
            }
    }

    override fun lazyLoading() {
        viewModel?.reqQryPositionDetail()
        viewModel?.positionLiveData?.observe(this, Observer {
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): PositionItemViewHolder =
        PositionItemViewHolder(layoutInflater.inflate(R.layout.layout_item_position,parent,false))


    override fun onBindItemViewHolder(holder: PositionItemViewHolder, position: Int) {

    }

}

class PositionItemViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)