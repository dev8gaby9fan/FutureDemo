package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.ext.viewModelOf
import com.fsh.trade.R
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
        viewModel?.orderLiveData?.observe(this, Observer {
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): OrderRecordItemVH =
        OrderRecordItemVH(layoutInflater.inflate(R.layout.layout_item_order,parent,false))


    override fun onBindItemViewHolder(holder: OrderRecordItemVH, position: Int) {

    }

}

class OrderRecordItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)