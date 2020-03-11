package com.fsh.quote.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseFragment
import com.fsh.common.ext.OnItemTouchEventListener
import com.fsh.common.ext.RecyclerViewItemClickListener
import com.fsh.common.model.InstrumentInfo
import com.fsh.quote.R
import com.fsh.quote.service.QuoteInfoMgr
import kotlinx.android.synthetic.main.fragment_quote_list.*
import kotlinx.android.synthetic.main.layout_item_quote.view.*

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: 行情列表界面
 *
 */

class QuoteListFragment : BaseFragment(),IContentFragment{

    private var adapter:QuoteItemAdapter = QuoteItemAdapter()
    private var insList:ArrayList<InstrumentInfo> = ArrayList()

    override fun layoutRes(): Int = R.layout.fragment_quote_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quote_list.itemAnimator = DefaultItemAnimator()
        quote_list.addOnItemTouchListener(RecyclerViewItemClickListener(quote_list,object : OnItemTouchEventListener{
            override fun onClick(position: Int) {

            }

            override fun onLongClick(position: Int) {}
        }))
        quote_list.adapter = adapter
    }

    override fun onSwitchExchange(id: String) {
        insList.clear()
        insList.addAll(QuoteInfoMgr.mgr.getExchange(id).getInstruments())
        adapter.notifyItemRangeChanged(0,insList.size)
    }

    inner class QuoteItemAdapter : RecyclerView.Adapter<QuoteItem>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteItem {
            return QuoteItem(LayoutInflater.from(requireContext()).inflate(R.layout.layout_item_quote,parent,false))
        }

        override fun getItemCount(): Int = insList.size

        override fun onBindViewHolder(holder: QuoteItem, position: Int) {
            val ins = insList[position]
            holder.item.tv_ins_name.text = ins.name
        }

        override fun onBindViewHolder(
            holder: QuoteItem,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)

        }

    }

    class QuoteItem(var item:View) : RecyclerView.ViewHolder(item)
}