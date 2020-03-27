package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.bean.InstrumentPosition

/**
 * 持仓列表
 */
class PositionRecordFragment : BaseRecordFragment<InstrumentPosition,PositionItemViewHodler>(){

    companion object{
        @JvmStatic
        fun newInstance():PositionRecordFragment = PositionRecordFragment().apply {
                val args = Bundle()
                arguments = args
            }
    }

    override fun lazyLoading() {
        viewModel?.reqQryPositionDetail()
        Log.d("PositionRecordFragment","lazyLoading ")
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): PositionItemViewHodler {
        TODO("Not implement method error")
    }

    override fun onBindItemViewHolder(holder: PositionItemViewHodler, position: Int) {

    }

}

class PositionItemViewHodler(itemView:View) : RecyclerView.ViewHolder(itemView)