package com.future.quote.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.ext.OnItemTouchEventListener
import com.fsh.common.ext.RecyclerViewItemClickListener
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.provider.MainService
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
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

class QuoteListFragment : BaseLazyFragment(), IContentFragment {
    private var adapter: QuoteItemAdapter = QuoteItemAdapter()
    private var insList: ArrayList<InstrumentInfo> = ArrayList()
    private var insMap: HashMap<String, Pair<Int, InstrumentInfo>> = HashMap(50)
    private lateinit var viewModel: QuoteListViewModel
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var isCanUpdateDate:Boolean = true
    override fun layoutRes(): Int = R.layout.fragment_quote_list

    override fun lazyLoading() {
        initViews()
        initDatas()
    }

    override fun onVisible() {
        isCanUpdateDate = true
    }

    override fun onPause() {
        super.onPause()
        isCanUpdateDate = false
        subscribeQuote(false)
    }

    private fun initViews() {
        quote_list.itemAnimator = null
        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VISIBLE)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.bg_common_divider))
        quote_list.addItemDecoration(dividerItemDecoration)
        quote_list.addOnItemTouchListener(RecyclerViewItemClickListener(quote_list,
            object : OnItemTouchEventListener {
                override fun onClick(position: Int) = handleItemClick(position)

                override fun onLongClick(position: Int) {}
            }))
        quote_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isCanUpdateDate = newState == RecyclerView.SCROLL_STATE_IDLE
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    subscribeQuote(true)
                }
            }
        })
        quote_list.adapter = adapter
    }
    //行情列表Item点击处理
    private fun handleItemClick(pos:Int){
        val tradeService = ARouterUtils.getTradeService()
        tradeService.setTradeIns(insList[pos])
        //没有登录就跳转到登录界面
        if(!tradeService.isTradingLogin()){
            ARouter.getInstance()
                .build(ARouterPath.Page.PAGE_TRADE_LOGIN)
                .navigation(context)
        }else{
            //切换到交易界面
            ARouterUtils.getMainService()
                .switchTabPage(MainService.PAGE_TRADE)
        }
    }

    private fun initDatas() {
        viewModel = viewModelOf<QuoteListViewModel>().value
        //WebSocket连接状态
        viewModel.socketStatusEvent.observe(this, Observer {
            if (it == FWebSocket.STATUS_CONNECTED) {
                subscribeQuote(true)
            }
        })
        //行情数据
        disposables.add(viewModel.quoteDataEvent.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //列表滚动中，不刷新列表数据
                if(!isCanUpdateDate){
                    return@subscribe
                }
                val pair = insMap[it.instrument_id]
                if (pair != null && quote_list.scrollState == RecyclerView.SCROLL_STATE_IDLE && !quote_list.isComputingLayout) {
                    adapter.notifyItemRangeChanged(pair.first, 1)
                }

            })
    }

    private fun subscribeQuote(isSub:Boolean) {
        val layoutManager: LinearLayoutManager = quote_list.layoutManager as LinearLayoutManager
        val startPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val endPosition = layoutManager.findLastCompletelyVisibleItemPosition() + 1
        if(startPosition < 0){
            Log.d("QuoteListFragment","from index $startPosition less zero,can't get start position")
            return
        }
        val indIdList: String =  insList.subList(startPosition, endPosition)
            .joinToString(","){it.id}
        if(isSub){
            viewModel.subscribeQuote(indIdList)
        }else{
            viewModel.unSubscribeQuote(indIdList)
        }
    }

    override fun onSwitchExchange(id: String) {
        insList.clear()
        insMap.clear()
        insList.addAll(QuoteInfoMgr.mgr.getExchange(id).getInstruments())
        insList.forEachIndexed { index, ins ->
            insMap[ins.id] = Pair(index, ins)
        }
        adapter.notifyDataSetChanged()
        //如果是WebSocket连接成功，就直接将RecyclerView滚动到第一条
        if(!viewModel.needConnectSocket()){
            quote_list.postDelayed({subscribeQuote(true)},300)
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
            holder.item.tv_ins_short_id.text = ins.ctpInstrumentId
            val quoteEntity = QuoteInfoMgr.mgr.getQuoteEntity(ins.id)
            setQuoteTextToTextView(holder.item.tv_ins_price,quoteEntity?.last_price,ins.priceTick)
            setQuoteTextToTextView(holder.item.tv_ins_pre_settlement,quoteEntity?.pre_settlement,ins.priceTick)
            setQuoteTextToTextView(holder.item.tv_ins_updown,quoteEntity?.updown,ins.priceTick)
            setQuoteTextToTextView(holder.item.tv_ins_updown_ratio,quoteEntity?.updown_ratio,ins.priceTick,false)
            setQuoteTextToTextView(holder.item.tv_ins_vol,quoteEntity?.amount,"1")
            setQuoteTextToTextView(holder.item.tv_ins_pre_io,quoteEntity?.pre_open_interest,ins.priceTick)
            val quoteTextColor = quoteEntity?.quoteTextColor ?: R.color.quote_white
            holder.item.tv_ins_price.setTextColor(resources.getColor(quoteTextColor))
            holder.item.tv_ins_updown.setTextColor(resources.getColor(quoteTextColor))
            holder.item.tv_ins_updown_ratio.setTextColor(resources.getColor(quoteTextColor))
        }

        private fun setQuoteTextToTextView(textView:TextView,quoteProperty:String?,priceTick:String?,format:Boolean = true){
            //行情没有发生变化，不刷新TextView
            if(textView.text == quoteProperty){
                return
            }
            if(!Omits.isOmit(quoteProperty)){
                if(format){
                    textView.text = NumberUtils.formatNum(quoteProperty,priceTick)
                }else{
                    textView.text = quoteProperty
                }
            }else{
                textView.text = Omits.OmitPrice
            }
        }
    }
}

class QuoteItem(var item: View) : RecyclerView.ViewHolder(item)