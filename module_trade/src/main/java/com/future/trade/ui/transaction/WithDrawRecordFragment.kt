package com.future.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.ext.viewModelOf
import com.fsh.common.util.ARouterUtils
import com.future.trade.R
import com.future.trade.bean.RspOrderField
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
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
    private var withDrawDialog:OrderInsertNoticeDialog?= null
    private var withDrawItem:RspOrderField? = null
    override fun lazyLoading() {
        withDrawDialog = OrderInsertNoticeDialog()
        withDrawDialog?.listener = object :  OrderInsertNoticeDialog.OrderInsertNoticeViewListener{
            override fun onInsertClick() {
                val reqField = withDrawItem!!.toActionOrderReq()
                viewModel?.reqOrderAction(reqField)
            }
        }
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
        holder.itemView.setOnClickListener {
            withDrawItem = item
            val instrument = ARouterUtils.getQuoteService().getInstrumentById(item.exchangeID+"."+item.instrumentID)
            withDrawDialog?.showDialog(childFragmentManager,"${instrument?.name} ${item.volumeTotal}手${CTPDirection.from(item.direction)?.text}${CTPCombOffsetFlag.from(item.combOffsetFlag[0]).text}价格:${item.limitPrice}",
                getString(R.string.tv_order_action_notice))
        }
    }

}

class WithDrawItemVH(itemView: View) : RecyclerView.ViewHolder(itemView)