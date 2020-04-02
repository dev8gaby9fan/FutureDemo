package com.fsh.trade.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.InstrumentInfo
import com.fsh.trade.R
import com.fsh.trade.bean.RspTradingAccountField
import com.fsh.trade.model.TransactionInputHelper
import com.fsh.trade.model.TransactionViewModel
import com.fsh.trade.ui.account.TradingAccountActivity
import com.fsh.trade.widget.keyboard.FutureKeyboard
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment :BaseFragment(){
    private lateinit var pagerAdapter: CommonFragmentPagerAdapter
    private lateinit var fragmentList:List<BaseRecordFragment<*,*>>
    private lateinit var transactionInputHelper: TransactionInputHelper
    var viewModel: TransactionViewModel? = null

    override fun layoutRes(): Int = R.layout.fragment_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionInputHelper = TransactionInputHelper(tv_order_price_input,tv_volume_input)
        transactionInputHelper.setTradeInstrument(InstrumentInfo("测试合约","IF2004","CFFEX","IF"))
        initViews()
        initViewEvents()
        initKeyBoard()
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
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

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
        rl_order_price.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Price)
        }
        rl_volume.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Volume)
        }

        btn_buy.setOnClickListener {

        }

        btn_sell.setOnClickListener {

        }

        btn_close.setOnClickListener {

        }
    }

    private fun showInputKeyboard(type: FutureKeyboard.KeyboardType){
//        val ins = transactionInputHelper.getTradeInstrument()
//        if(ins == null){
//            Snackbar.make(tv_account_info,"请选择交易合约",Snackbar.LENGTH_SHORT).show()
//            return
//        }
        future_keyboard.show(type)
    }

    private fun initKeyBoard(){

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