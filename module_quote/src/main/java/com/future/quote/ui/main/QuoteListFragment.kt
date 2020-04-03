package com.future.quote.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseFragment
import com.fsh.common.ext.OnItemTouchEventListener
import com.fsh.common.ext.RecyclerViewItemClickListener
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.Omits
import com.fsh.common.websocket.FWebSocket
import com.future.quote.R
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.QuoteListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

class QuoteListFragment : BaseFragment(), IContentFragment {

    private var adapter: QuoteItemAdapter = QuoteItemAdapter()
    private var insList: ArrayList<InstrumentInfo> = ArrayList()
    private var insMap: HashMap<String, Pair<Int, InstrumentInfo>> = HashMap(50)
    private lateinit var viewModel: QuoteListViewModel
    private val disposables: CompositeDisposable = CompositeDisposable()
    override fun layoutRes(): Int = R.layout.fragment_quote_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDatas()
    }

    private fun initViews() {
        quote_list.itemAnimator = DefaultItemAnimator()
        quote_list.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VISIBLE))
        quote_list.addOnItemTouchListener(RecyclerViewItemClickListener(quote_list,
            object : OnItemTouchEventListener {
                override fun onClick(position: Int) {
                    subscribeQuote()
                }

                override fun onLongClick(position: Int) {}
            }))
        quote_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    subscribeQuote()
                }
            }
        })
        quote_list.adapter = adapter
    }

    private fun initDatas() {
        viewModel = viewModelOf<QuoteListViewModel>().value
        //WebSocket连接状态
        viewModel.socketStatusEvent.observe(this, Observer {
            Log.d("QuoteListFragment", "received socket status event it")
            if (it == FWebSocket.STATUS_CONNECTED) {
                subscribeQuote()
            }
        })
        //行情数据
        disposables.add(viewModel.quoteDataEvent.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val pair = insMap[it.instrument_id]
                if (pair != null && quote_list.scrollState == RecyclerView.SCROLL_STATE_IDLE && !quote_list.isComputingLayout) {
                    adapter.notifyItemRangeChanged(pair.first, 1)
                    Log.d(
                        "QuoteListFragment",
                        "notify item changed ${pair.first}-${pair.second}"
                    )
                }

            })
    }

    private fun subscribeQuote() {
        val layoutMangaer: LinearLayoutManager = quote_list.layoutManager as LinearLayoutManager
        val startPosition = layoutMangaer.findFirstCompletelyVisibleItemPosition()
        val endPosition = layoutMangaer.findLastCompletelyVisibleItemPosition() + 1
        var indIdList: String = ""
        insList.subList(startPosition, endPosition).map { ins -> "${ins.id}," }
            .forEach {
                indIdList = indIdList.plus(it)
            }
        viewModel.subscribeQuote(indIdList)
    }

    override fun onSwitchExchange(id: String) {
        insList.clear()
        insMap.clear()
        insList.addAll(QuoteInfoMgr.mgr.getExchange(id).getInstruments())
        insList.forEachIndexed { index, ins ->
            insMap[ins.id] = Pair(index, ins)
        }
        adapter.notifyDataSetChanged()
        if(!viewModel.connectSocket()){
            subscribeQuote()
        }
    }

    inner class QuoteItemAdapter : RecyclerView.Adapter<QuoteItem>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteItem {
            return QuoteItem(
                LayoutInflater.from(requireContext()).inflate(
                    R.layout.layout_item_quote,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = insList.size

        override fun onBindViewHolder(holder: QuoteItem, position: Int) {
            val ins = insList[position]
            holder.item.tv_ins_name.text = ins.name
            holder.item.tv_ins_short_id.text = ins.shortInsId
            //TODO RecyclerView的行情显示有紊乱
            val quoteEntity = QuoteInfoMgr.mgr.getQuoteEntity(ins.id) ?: return
            holder.item.tv_ins_price.text = quoteEntity.last_price
            holder.item.tv_ins_pre_settlement.text = quoteEntity.pre_settlement
            holder.item.tv_ins_updown.text = quoteEntity.updown
            holder.item.tv_ins_updown_ratio.text = quoteEntity.updown_ratio
            holder.item.tv_ins_vol.text = quoteEntity.amount
            holder.item.tv_ins_pre_io.text = quoteEntity.pre_open_interest
        }

    }
}

class QuoteItem(var item: View) : RecyclerView.ViewHolder(item)