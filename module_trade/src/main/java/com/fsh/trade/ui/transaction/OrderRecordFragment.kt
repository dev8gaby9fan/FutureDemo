package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsh.trade.bean.RspOrderField

/**
 * 委托记录fragment
 *
 */
class OrderRecordFragment : BaseRecordFragment<RspOrderField,OrderRecordItemVH>(){
    companion object{
        @JvmStatic
        fun newInstance():OrderRecordFragment = OrderRecordFragment().apply {
            val args = Bundle()
            arguments = args
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.repository
    }

    override fun lazyLoading() {
        viewModel?.reqQryOrder()
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): OrderRecordItemVH {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindItemViewHolder(holder: OrderRecordItemVH, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class OrderRecordItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)