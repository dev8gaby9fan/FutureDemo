package com.future.trade.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ArouterPath
import com.fsh.common.model.InstrumentInfo
import com.future.trade.R
import com.future.trade.bean.RspTradingAccountField
import com.future.trade.model.TransactionInputHelper
import com.future.trade.model.TransactionViewModel
import com.future.trade.ui.account.TradingAccountActivity
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
import com.future.trade.widget.keyboard.FutureKeyboard
import com.future.trade.widget.keyboard.SimpleFutureKeyboardListener
import com.future.trade.widget.order.OrderButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.lang.IllegalArgumentException

@Route(path=ArouterPath.PAGE_TRADE_MAIN)
class TransactionFragment :BaseFragment(){
    private lateinit var pagerAdapter: CommonFragmentPagerAdapter
    private lateinit var fragmentList:List<BaseRecordFragment<*,*>>
    private lateinit var transactionInputHelper: TransactionInputHelper
    private lateinit var altOrderInsert:OrderInsertNoticeDialog
    private var orderIns:InstrumentInfo? = null
    var viewModel: TransactionViewModel? = null

    override fun layoutRes(): Int = R.layout.fragment_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderIns = InstrumentInfo("测试合约","IF2004","CFFEX","IF")
        transactionInputHelper = TransactionInputHelper(tv_order_price_input,tv_volume_input)
        transactionInputHelper.setTradeInstrument(orderIns!!)
        initViews()
        initViewEvents()
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
                            altOrderInsert.dismiss()
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

}