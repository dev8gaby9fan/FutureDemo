package com.future.trade.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.Omits
import com.future.trade.R
import com.future.trade.bean.RspTradingAccountField
import com.future.trade.model.TransactionInputHelper
import com.future.trade.model.TransactionViewModel
import com.future.trade.ui.account.TradingAccountActivity
import com.future.trade.ui.login.TradeLoginActivity
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
import com.future.trade.widget.keyboard.FutureKeyboard
import com.future.trade.widget.order.OrderButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.lang.IllegalArgumentException

@Route(path=ARouterPath.Page.PAGE_TRADE_MAIN)
class TransactionFragment :BaseLazyFragment(){


    private lateinit var pagerAdapter: CommonFragmentPagerAdapter
    private lateinit var fragmentList:List<BaseRecordFragment<*,*>>
    private lateinit var transactionInputHelper: TransactionInputHelper
    private lateinit var altOrderInsert:OrderInsertNoticeDialog
    private var orderIns:InstrumentInfo? = null
    var viewModel: TransactionViewModel? = null
    private val disposable:CompositeDisposable = CompositeDisposable()
    override fun layoutRes(): Int = R.layout.fragment_transaction

    override fun onVisible() {
        val tradeService = ARouterUtils.getTradeService()
        //没有登录就跳转到登录界面
        if(!tradeService.isTradingLogin()){
            startActivity(Intent(context, TradeLoginActivity::class.java))
        }
    }

    override fun lazyLoading() {
        Log.d("LazyFragment","TransactionFragment lazyLoading")
        transactionInputHelper = TransactionInputHelper(tv_order_price_input,tv_volume_input,btn_buy,btn_sell,btn_close)
        initViews()
        initViewEvents()
        initData()
    }

    fun initViews(){
        viewModel = viewModelOf<TransactionViewModel>().value.apply {
            //查一下资金
            reqQryTradingAccount()
            tradingAccountLiveData.observe(this@TransactionFragment, Observer {
                updateAccountDetails(it)
            })
        }
        val titleList = resources.getStringArray(R.array.tab_trans).toMutableList()
        fragmentList = getRecordFragmentList()
        pagerAdapter = CommonFragmentPagerAdapter(childFragmentManager,fragmentList,titleList)
        view_pager.adapter = pagerAdapter
        nav_layer_trans.let {
            it.setupWithViewPager(view_pager)
            it.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    view_pager.currentItem = tab?.position!!
                }
            })
            for(index in fragmentList.indices){
                it.getTabAt(index)?.text = pagerAdapter.getPageTitle(index)
            }
        }
        layout_account.setOnClickListener {
            startActivity(Intent(context,TradingAccountActivity::class.java))
        }
    }

    private fun initViewEvents(){
        altOrderInsert = OrderInsertNoticeDialog()
        rl_order_price.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Price)
        }
        rl_volume.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Volume)
        }
        val orderButtonClickListener = View.OnClickListener{
            if(it is OrderButton){
                try{
                    val filed = it.performOrderInsert(transactionInputHelper.getOrderVolume())
                    altOrderInsert.listener = object : OrderInsertNoticeDialog.OrderInsertNoticeViewListener{
                        override fun onInsertClick() {
                            viewModel?.reqOrderInsert(filed)
                        }
                    }
                    altOrderInsert.showDialog(childFragmentManager,filed.toOrderString())
                }catch (e:IllegalArgumentException){
                    Snackbar.make(it,e.message!!,Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }

            }
        }
        btn_buy.setOnClickListener(orderButtonClickListener)
        btn_sell.setOnClickListener(orderButtonClickListener)
        btn_close.setOnClickListener(orderButtonClickListener)
        future_keyboard.setFutureKeyboardListener(transactionInputHelper)
    }

    private fun showInputKeyboard(type: FutureKeyboard.KeyboardType){
        val ins = transactionInputHelper.getTradeInstrument()
        if(ins == null){
            Snackbar.make(tv_account_info,"请选择交易合约",Snackbar.LENGTH_SHORT).show()
            return
        }
        btn_buy.setTransactionInfo(transactionInputHelper.getTradeInstrument(),transactionInputHelper.getOrderPriceType())
        btn_sell.setTransactionInfo(transactionInputHelper.getTradeInstrument(),transactionInputHelper.getOrderPriceType())
        btn_close.setTransactionInfo(transactionInputHelper.getTradeInstrument(),transactionInputHelper.getOrderPriceType())
        transactionInputHelper.changeInputType(type)
        future_keyboard.show(type)
    }

    private fun updateAccountDetails(account:RspTradingAccountField){
        tv_balance.text = account.balance.toString()
        tv_avaliable.text = account.avaliable.toString()
        tv_profit.text = account.closeProfit.toString()
        tv_account_info.text = getString(R.string.tv_account_info,account.accountID,account.brokerID)
    }

    private fun getRecordFragmentList():List<BaseRecordFragment<*,*>> = arrayListOf(
        PositionRecordFragment.newInstance(),WithDrawRecordFragment.newInstance(),
        OrderRecordFragment.newInstance(),TradeRecordFragment.newInstance()
    )

    private fun initData(){
        val tradeService = ARouterUtils.getTradeService()
        tradeService.getTradeInsLiveData().observe(this, Observer {
            switchOrderIns(it)
        })
        disposable.add(viewModel!!.quoteData.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleQuoteUpdate(it)
            })
    }

    private fun switchOrderIns(ins:InstrumentInfo){
        val quoteService = ARouterUtils.getQuoteService()
        var needToBindIns = ins
        //主力合约，就找到它真实的合约
        if(ins.isMainIns){
            val instrument = quoteService.getInstrumentById(ins.mainInsId)
            if(instrument != null){
                needToBindIns = instrument
            }
        }
        orderIns = needToBindIns
        transactionInputHelper.setTradeInstrument(needToBindIns)
        viewModel?.subscribeQuote(needToBindIns.id)
        tv_order_ins_name.text = needToBindIns.name

        val quoteEntity = quoteService.getQuoteByInstrument(needToBindIns.id)
        if(quoteEntity != null){
            handleQuoteUpdate(quoteEntity)
        }
    }

    private fun handleQuoteUpdate(quoteEntity: QuoteEntity){
        if(orderIns?.id != quoteEntity.instrument_id){
            return
        }

        transactionInputHelper.onQuoteUpdate(quoteEntity)

        setQuoteText(tv_last,quoteEntity.last_price)
        setQuoteText(tv_now_volume,quoteEntity.open_interest)
        setQuoteText(tv_ask_price,quoteEntity.ask_price1)
        setQuoteText(tv_ask_volume,quoteEntity.ask_volume1)
        setQuoteText(tv_bid_price,quoteEntity.bid_price1)
        setQuoteText(tv_bid_volume,quoteEntity.bid_volume1)

        tv_last.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
        tv_ask_price.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
        tv_bid_price.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
    }

    private fun setQuoteText(textView:TextView,quoteProperty:String?){
        if(textView.text == quoteProperty){
            return
        }
        textView.text = if(Omits.isOmit(quoteProperty)) Omits.OmitPrice else quoteProperty
    }
}