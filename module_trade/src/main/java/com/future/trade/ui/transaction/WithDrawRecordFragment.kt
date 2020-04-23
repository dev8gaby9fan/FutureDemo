package com.future.trade.ui.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.ext.viewModelOf
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
import com.future.trade.R
import com.future.trade.bean.RspOrderField
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
import kotlinx.android.synthetic.main.layout_item_with_draw.view.*

/**
 * 挂单列表界面
 */
class WithDrawRecordFragment :BaseRecordFragment<RspOrderField,CommonWithDrawItemVH>(){
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

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): CommonWithDrawItemVH =
        WithDrawItemVH(layoutInflater.inflate(R.layout.layout_item_with_draw,parent,false))

    override fun createHeadViewHolder(parent: ViewGroup): CommonWithDrawItemVH =
        WithDrawHeadVH(layoutInflater.inflate(R.layout.layout_item_with_draw,parent,false))


    override fun onBindHeadViewHolder(holder: CommonWithDrawItemVH) {}

    @SuppressLint("SetTextI18n")
    override fun onBindItemViewHolder(holder: CommonWithDrawItemVH, position: Int) {
        val item = getItem(position)!!
        val ins = ARouterUtils.getQuoteService().getInstrumentById(item.exchangeID+"."+item.instrumentID)
        holder.itemView.tv_ins_name.text = item.instrumentID
        holder.itemView.tv_dir.text = CTPDirection.from(item.direction)?.text
        holder.itemView.tv_offset.text =   CTPCombOffsetFlag.from(item.combOffsetFlag[0]).text
        holder.itemView.tv_order_price.text = NumberUtils.formatNum(item.limitPrice.toString(),ins?.priceTick)
        holder.itemView.tv_order_volume.text = item.volumeTotalOriginal.toString()
        holder.itemView.tv_with_draw.text = (item.volumeTotalOriginal - item.volumeTraded).toString()
        holder.itemView.tv_order_hedge.text = CTPHedgeType.from(item.combHedgeFlag[0])?.text
        holder.itemView.setOnClickListener {
            withDrawItem = item
            val instrument = ARouterUtils.getQuoteService().getInstrumentById(item.exchangeID+"."+item.instrumentID)
            withDrawDialog?.showDialog(childFragmentManager,"${instrument?.name} ${item.volumeTotal}手${CTPDirection.from(item.direction)?.text}${CTPCombOffsetFlag.from(item.combOffsetFlag[0]).text}价格:${item.limitPrice}",
                getString(R.string.tv_order_action_notice))
        }
    }

}

abstract class CommonWithDrawItemVH(itemView: View) : RecyclerView.ViewHolder(itemView)

class WithDrawHeadVH(itemView: View) : CommonWithDrawItemVH(itemView)

class WithDrawItemVH(itemView: View) : CommonWithDrawItemVH(itemView)