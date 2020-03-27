package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.bean.RspOrderField

class WithDrawRecordFragment :BaseRecordFragment<RspOrderField,WithDrawItemVH>(){
    companion object{
        fun newInstance():WithDrawRecordFragment = WithDrawRecordFragment().apply {
                val args = Bundle()
                arguments = args
            }
    }
    override fun lazyLoading() {
        viewModel?.reqQryOrder()
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): WithDrawItemVH {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindItemViewHolder(holder: WithDrawItemVH, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class WithDrawItemVH(itemView: View) : RecyclerView.ViewHolder(itemView)