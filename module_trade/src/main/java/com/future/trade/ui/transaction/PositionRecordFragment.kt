package com.future.trade.ui.transaction

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.util.Omits
import com.future.trade.R
import com.future.trade.bean.position.Position
import kotlinx.android.synthetic.main.layout_item_position.view.*
import kotlinx.android.synthetic.main.layout_item_position.view.scroller

/**
 * 持仓列表
 */
class PositionRecordFragment : BaseRecordFragment<Position, CommonPositionItemViewHolder>() {
    private val scrollViewList: MutableList<HorizontalScrollView> = ArrayList()
    private var scrollViewScrollX: Int = 0

    companion object {
        @JvmStatic
        fun newInstance(): PositionRecordFragment = PositionRecordFragment().apply {
            val args = Bundle()
            arguments = args
        }
    }

    override fun lazyLoading() {
        viewModel?.reqQryPositionDetail()
        viewModel?.positionLiveData?.observe(this, Observer {
            if ((parentFragment as TransactionFragment).needSetInitPosition() && it.isNotEmpty()) {
                it[0].setSelected(true)
                (parentFragment as TransactionFragment).onPositionItemClick(it[0])
            }
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): PositionItemViewHolder =
        PositionItemViewHolder(layoutInflater.inflate(R.layout.layout_item_position, parent, false))

    override fun createHeadViewHolder(parent: ViewGroup): CommonPositionItemViewHolder =
        PositionHeadViewHolder(layoutInflater.inflate(R.layout.layout_item_position, parent, false))

    override fun onBindHeadViewHolder(holder: CommonPositionItemViewHolder) {
        setupScrollView(holder)
    }

    override fun onBindItemViewHolder(holder: CommonPositionItemViewHolder, position: Int) {
        val posItem = getItem(position)
        holder.itemView.tv_ins_name.text = posItem?.getInstrumentId() ?: Omits.OmitPrice
        holder.itemView.tv_direction.text = posItem?.getDirection()?.text ?: Omits.OmitPrice
        holder.itemView.tv_pos_volume.text = posItem?.getPosition()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_available.text =
            posItem?.getAvailable()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_pos_profit.text =
            posItem?.getPositionProfit()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_pos_cost.text =
            posItem?.getPositionCost()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_open_profit.text =
            posItem?.getOpenPositionProfit()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_open_cost.text =
            posItem?.getOpenCost()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_today.text =
            posItem?.getTodayPosition()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_yesterday.text =
            posItem?.getYeterdayPosition()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_spec.text = posItem?.getSpecPosition()?.toString() ?: Omits.OmitPrice
        holder.itemView.tv_pos_hedge.text =
            posItem?.getHedgePosition()?.toString() ?: Omits.OmitPrice
        val clickListener = { _:View?->
            if (!holder.itemView.isSelected) {
                (parentFragment as TransactionFragment).onPositionItemClick(posItem!!)
                selectItem(position)
            }
        }
        holder.itemView.setOnClickListener(clickListener)
        holder.itemView.container.setOnClickListener(clickListener)
        /* {
            if (holder.itemView.isSelected) {
                return@setOnClickListener
            }
            (parentFragment as TransactionFragment).onPositionItemClick(posItem!!)
            selectItem(position)
        }*/
        setupScrollView(holder)
        holder.itemView.isSelected = posItem?.isSelected() ?: false
        val foregroundColorRes =
            if (holder.itemView.isSelected) R.color.color_pressed else R.color.white
        holder.itemView.background =
            ColorDrawable(holder.itemView.resources.getColor(foregroundColorRes))
        posItem?.dataChanged(false)
    }

    override fun onItemViewHodlerAttachedToWindow(holder: CommonPositionItemViewHolder) {
        scrollViewList.add(holder.itemView.scroller)
        holder.itemView.scroller.scrollTo(scrollViewScrollX, 0)
    }

    override fun onItemViewHolderDetachedFromWindow(holder: CommonPositionItemViewHolder) {
        scrollViewList.remove(holder.itemView.scroller)
    }

    private fun setupScrollView(holder: CommonPositionItemViewHolder) {
        holder.itemView.scroller.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            for (scrollView in scrollViewList) {
                scrollView.scrollTo(scrollX,scrollY)
            }
            scrollViewScrollX = scrollX
        }
        holder.itemView.scroller.scrollTo(scrollViewScrollX,0)
    }

    private fun selectItem(index: Int) {
        recordList.forEachIndexed { itemIndex, item ->
            item.setSelected(itemIndex == index)
        }
        recordAdapter.notifyItemRangeChanged(1, recordList.size + 1)
    }

    fun onSwitchTransactionInstrument(instrument:InstrumentInfo){
        val pos = recordList.find { pos -> pos.getInstrumentId() == instrument.ctpInstrumentId }
        if(pos == null){
            selectItem(-1)
            return
        }
        val index = recordList.indexOf(pos)
        selectItem(index)
    }
}

abstract class CommonPositionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class PositionHeadViewHolder(itemView: View) : CommonPositionItemViewHolder(itemView)

class PositionItemViewHolder(itemView: View) : CommonPositionItemViewHolder(itemView)