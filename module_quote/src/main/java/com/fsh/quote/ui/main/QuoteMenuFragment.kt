package com.fsh.quote.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseFragment
import com.fsh.common.ext.OnItemTouchEventListener
import com.fsh.common.ext.RecyclerViewItemClickListener
import com.fsh.common.model.ExchangeInfo
import com.fsh.quote.R
import com.fsh.quote.service.QuoteInfoMgr
import kotlinx.android.synthetic.main.fragment_quote_menu.*
import kotlinx.android.synthetic.main.layout_exchange_item.view.*

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: 市场界面Menu Fragment
 */

class QuoteMenuFragment : BaseFragment(),IDrawerMenuFragment{
    private lateinit var exchangeList:List<ExchangeInfo>

    var menuEventListener: MenuEventListener? = null

    override fun layoutRes(): Int = R.layout.fragment_quote_menu

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    /**
     * 合约加载完成，初始化交易所列表
     */
    override fun onLoadInsOk() {
        exchangeList = QuoteInfoMgr.mgr.getExchangeList()
        Log.d("QuoteMenuFragment","onLoadInsOk ${exchangeList.size}")
        initExchangeList()
    }

    private fun initExchangeList(){
        exchange_list.layoutManager = GridLayoutManager(requireContext(),2)
        exchange_list.adapter = ItemAdapter()
        exchange_list.addOnItemTouchListener(RecyclerViewItemClickListener(exchange_list,
            object : OnItemTouchEventListener{
                override fun onClick(pos:Int) {
                    Log.d("QuoteMenuFragment","exchange item click event $pos")
                    menuEventListener?.switchExchange(exchangeList[pos].id)
                }

                override fun onLongClick(pos:Int) {}
            }))
        menuEventListener?.switchExchange(exchangeList[0].id)
    }

    inner class ItemAdapter : RecyclerView.Adapter<ExchangeItem>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeItem {
            return ExchangeItem(LayoutInflater.from(requireContext()).inflate(R.layout.layout_exchange_item,parent,false))
        }

        override fun getItemCount(): Int = exchangeList.size

        override fun onBindViewHolder(holder: ExchangeItem, position: Int) {
            holder.item.tv_exchange_name.text = exchangeList[position].name
        }

    }

}

class ExchangeItem(var item:View) : RecyclerView.ViewHolder(item)

interface MenuEventListener{
    /**
     * 切换交易所
     */
    fun switchExchange(exchangeId:String)
}