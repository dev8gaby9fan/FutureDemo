package com.future.trade.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.util.Omits
import com.future.trade.R
import com.future.trade.bean.position.DirectionPosition
import com.future.trade.bean.position.Position
import com.future.trade.enums.CTPDirection
import kotlinx.android.synthetic.main.layout_item_position.view.*

/**
 * 持仓列表
 */
class PositionRecordFragment : BaseRecordFragment<Position,PositionItemViewHolder>(){

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
            Log.d("PositionRecordFragment","position list ${it.size}")
            updateDataList(it)
        })
    }

    override fun createItemViewHolder(parent: ViewGroup, viewType: Int): PositionItemViewHolder =
        PositionItemViewHolder(layoutInflater.inflate(R.layout.layout_item_position,parent,false))


    override fun onBindItemViewHolder(holder: PositionItemViewHolder, position: Int) {
        val posItem = getItem(position)
        holder.itemView.tv_ins_name.text = posItem?.getInstrumentId() ?: Omits.OmitPrice
        holder.itemView.tv_direction.text = posItem?.getDirection()?.text ?: Omits.OmitPrice
        holder.itemView.tv_pos_volume.text = posItem?.getPosition()?.toString() ?: Omits.OmitPrice
    }

}

class PositionItemViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)