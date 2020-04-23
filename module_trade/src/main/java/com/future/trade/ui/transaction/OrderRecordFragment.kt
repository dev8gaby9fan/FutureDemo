package com.future.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.future.trade.R
import com.future.trade.bean.RspOrderField
import com.future.trade.enums.CTPCombOffsetFlag
import com.future.trade.enums.CTPDirection
import com.future.trade.enums.CTPHedgeType
import com.future.trade.enums.CTPOrderStatusType
import kotlinx.android.synthetic.main.layout_item_order.view.*
import kotlinx.android.synthetic.main.layout_item_with_draw.view.tv_ins_name

/**
 * 委托记录fragment
 *
 */
class OrderRecordFragment : BaseRecordFragment<RspOrderField,CommonOrderItemVH>(){
    private val scrollViewList:MutableList<HorizontalScrollView> = ArrayList()
    private var scrollViewScrollX:Int = 0
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

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): CommonOrderItemVH =
        OrderRecordItemVH(layoutInflater.inflate(R.layout.layout_item_order,parent,false))


    override fun createHeadViewHolder(parent: ViewGroup): CommonOrderItemVH =
        OrderRecordHeadVH(layoutInflater.inflate(R.layout.layout_item_order,parent,false))

    override fun onBindHeadViewHolder(holder: CommonOrderItemVH) {
        holder.itemView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            scrollViewScrollX  = scrollX
        }
        holder.itemView.setOnTouchListener { v, event ->
            for(scrollView in scrollViewList){
                if(scrollView != holder.itemView){
                    scrollView.onTouchEvent(MotionEvent.obtain(event))
                }
            }
            holder.itemView.onTouchEvent(event)
        }
        holder.itemView.scrollTo(scrollViewScrollX,0)
        scrollViewList.add(holder.itemView as HorizontalScrollView)
    }

    override fun onBindItemViewHolder(holder: CommonOrderItemVH, position: Int) {
        val itemRec = getItem(position)
        holder.itemView.tv_ins_name.text = itemRec?.instrumentID
        holder.itemView.tv_direct.text = CTPDirection.from(itemRec!!.direction)?.text
        holder.itemView.tv_offset.text = CTPCombOffsetFlag.from(itemRec.combOffsetFlag[0]).text
        holder.itemView.tv_status.text = CTPOrderStatusType.from(itemRec.orderStatus).notion
        holder.itemView.tv_price.text = itemRec.limitPrice.toString()
        holder.itemView.tv_volume.text = itemRec.volumeTotalOriginal.toString()
        holder.itemView.tv_trade_volume.text = itemRec.volumeTraded.toString()
        holder.itemView.tv_with_draw.text = (itemRec.volumeTotalOriginal - itemRec.volumeTraded).toString()
        holder.itemView.tv_time.text = itemRec.insertTime
        holder.itemView.tv_hedge.text = CTPHedgeType.from(itemRec.combHedgeFlag[0])?.text
        holder.itemView.tv_smsg.text = itemRec.statusMsg
        holder.itemView.tv_smsg.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        holder.itemView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            scrollViewScrollX  = scrollX
        }
        holder.itemView.setOnTouchListener { _, event ->
            for(scrollView in scrollViewList){
                if(scrollView != holder.itemView){
                    scrollView.onTouchEvent(MotionEvent.obtain(event))
                }
            }
            holder.itemView.onTouchEvent(event)
        }
        holder.itemView.scrollTo(scrollViewScrollX,0)
        scrollViewList.add(holder.itemView as HorizontalScrollView)
    }

    override fun onItemViewHolderDetachedFromWindow(holder: CommonOrderItemVH) {
        scrollViewList.remove(holder.itemView)
    }

}

abstract class CommonOrderItemVH(itemView:View) : RecyclerView.ViewHolder(itemView)

class OrderRecordHeadVH(itemView:View) : CommonOrderItemVH(itemView)

class OrderRecordItemVH(itemView:View) : CommonOrderItemVH(itemView)