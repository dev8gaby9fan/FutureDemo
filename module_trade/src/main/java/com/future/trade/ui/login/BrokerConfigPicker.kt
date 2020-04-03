package com.future.trade.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseBottomSheetDialog
import com.future.trade.R
import com.future.trade.bean.BrokerConfig
import kotlinx.android.synthetic.main.dialog_broker_config_picker.*
import kotlinx.android.synthetic.main.layout_item_broker.view.*
import java.lang.ref.WeakReference

/**
 * 经纪公司选择对话框
 */
class BrokerConfigPicker : BaseBottomSheetDialog(){
    override fun getLayoutRes(): Int = R.layout.dialog_broker_config_picker
    private var itemClickListener:WeakReference<OnBrokerItemClickListener>? = null
    private val brokerList:ArrayList<BrokerConfig> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        broker_list.layoutManager = LinearLayoutManager(context)
        broker_list.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        broker_list.adapter = BrokerItemAdapter()
    }

    fun setBrokers(list:List<BrokerConfig>){
        brokerList.clear()
        brokerList.addAll(list)
    }

    fun setOnBrokerItemClickListener(listener:OnBrokerItemClickListener){
        itemClickListener?.clear()
        itemClickListener = WeakReference(listener)
    }

    private inner class BrokerItemAdapter : RecyclerView.Adapter<BrokerItem>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrokerItem {
            return BrokerItem(LayoutInflater.from(context).inflate(R.layout.layout_item_broker,parent,false))
        }

        override fun getItemCount(): Int = brokerList.size

        override fun onBindViewHolder(holder: BrokerItem, position: Int) {
            holder.item.broker_name.text = brokerList[position].brokerName
            holder.item.setOnClickListener {
                itemClickListener?.get()?.onItemClick(position,holder,brokerList[position])
                dismiss()
            }
        }
    }

    class BrokerItem(var item:View) : RecyclerView.ViewHolder(item)

    interface OnBrokerItemClickListener {
        fun onItemClick(pos:Int,itemView:BrokerItem,item:BrokerConfig)
    }
}