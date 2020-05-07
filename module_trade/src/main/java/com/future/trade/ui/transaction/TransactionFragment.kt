package com.future.trade.ui.transaction

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.fsh.common.base.BaseLazyFragment
import com.fsh.common.base.CommonFragmentPagerAdapter
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.fsh.common.model.QuoteEntity
import com.fsh.common.util.ARouterUtils
import com.fsh.common.util.NumberUtils
import com.fsh.common.util.Omits
import com.future.trade.R
import com.future.trade.bean.IOrderInsertField
import com.future.trade.bean.RspTradingAccountField
import com.future.trade.bean.position.Position
import com.future.trade.model.TransactionInputHelper
import com.future.trade.model.TransactionViewModel
import com.future.trade.ui.account.TradingAccountActivity
import com.future.trade.ui.login.TradeLoginActivity
import com.future.trade.widget.dialog.OrderInsertNoticeDialog
import com.future.trade.widget.keyboard.FutureKeyboard
import com.future.trade.widget.order.OrderButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.lang.IllegalArgumentException

@Route(path=ARouterPath.Page.PAGE_TRADE_MAIN)
class TransactionFragment :BaseLazyFragment(),View.OnClickListener{
    private lateinit var pagerAdapter: CommonFragmentPagerAdapter
    private lateinit var fragmentList:List<BaseRecordFragment<*,*>>
    private lateinit var transactionInputHelper: TransactionInputHelper
    private lateinit var altOrderInsert:OrderInsertNoticeDialog
    private val REQ_SEARCH_INS = 100
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

    private fun initViews(){
        viewModel = viewModelOf<TransactionViewModel>().value.apply {
            //查一下资金
            reqQryTradingAccount()
            tradingAccountLiveData.observe(this@TransactionFragment, Observer {
                updateAccountDetails(it)
            })
            logoutData.observe(this@TransactionFragment, Observer {
                //跳转到登录界面
                startActivity(Intent(context,TradeLoginActivity::class.java))
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
        val mActivity = (activity as AppCompatActivity)
        mActivity.setSupportActionBar(tool_bar)
        mActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mActivity.supportActionBar?.setDisplayShowHomeEnabled(false)
        Log.d("TransactionFragment","actionBar --> ${mActivity.supportActionBar}")
        setHasOptionsMenu(true)
    }

    private fun initViewEvents(){
        altOrderInsert = OrderInsertNoticeDialog()
        rl_order_price.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Price)
        }
        rl_volume.setOnClickListener {
            showInputKeyboard(FutureKeyboard.KeyboardType.Volume)
        }
        btn_buy.setOnClickListener(this)
        btn_sell.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        rl_instrument.setOnClickListener(this)
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
        tv_balance.text = NumberUtils.formatNum(account.balance.toString(),"0.01")
        tv_available.text = NumberUtils.formatNum(account.avaliable.toString(),"0.01")
        tv_profit.text = NumberUtils.formatNum(account.closeProfit.toString(),"0.01")
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

    private fun switchOrderIns(ins:InstrumentInfo,pos:Position?=null){
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
        transactionInputHelper.setTradeInstrument(needToBindIns,pos)
        viewModel?.subscribeQuote(needToBindIns.id)
        tv_order_ins_name.text = needToBindIns.name

        val quoteEntity = quoteService.getQuoteByInstrument(needToBindIns.id)
        if(quoteEntity != null){
            handleQuoteUpdate(quoteEntity)
        }
        //取消选中的item
        (fragmentList[0] as PositionRecordFragment).onSwitchTransactionInstrument(needToBindIns)
    }

    private fun handleQuoteUpdate(quoteEntity: QuoteEntity){
        if(orderIns?.id != quoteEntity.instrument_id){
            return
        }

        transactionInputHelper.onQuoteUpdate(quoteEntity)

        setQuoteText(tv_last,quoteEntity.last_price,orderIns!!.priceTick!!)
        setQuoteText(tv_now_volume,quoteEntity.open_interest)
        setQuoteText(tv_ask_price,quoteEntity.ask_price1,orderIns!!.priceTick!!)
        setQuoteText(tv_ask_volume,quoteEntity.ask_volume1)
        setQuoteText(tv_bid_price,quoteEntity.bid_price1,orderIns!!.priceTick!!)
        setQuoteText(tv_bid_volume,quoteEntity.bid_volume1)

        tv_last.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
        tv_ask_price.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
        tv_bid_price.setTextColor(resources.getColor(quoteEntity.quoteTextColor))
    }

    private fun setQuoteText(textView:TextView,quoteProperty:String?,format:String = "1"){
        if(textView.text == quoteProperty){
            return
        }
        if(Omits.isOmit(quoteProperty)){
            textView.text = Omits.OmitPrice
        }
        textView.text = NumberUtils.formatNum(quoteProperty,format)
    }

    fun needSetInitPosition():Boolean{
        return orderIns == null
    }

    fun onPositionItemClick(pos:Position){
        val instrument = ARouterUtils.getQuoteService()
            .getInstrumentById(pos.getExchangeId() + "." + pos.getInstrumentId())
        if(instrument != null){
            switchOrderIns(instrument,pos)
        }
    }

    override fun onClick(v: View?) {
        if(v is OrderButton){
            try{
                val filed = v.performOrderInsert(transactionInputHelper.getOrderVolume())
                if(filed.size == 1){
                    showOrderNoticeDialog(filed)
                }else{
                    showSelectMutableOrderInsertField(filed)
                }
            }catch (e:IllegalArgumentException){
                Snackbar.make(v,e.message!!,Snackbar.LENGTH_SHORT).show()
            }
            return
        }
        when(v?.id){
            R.id.rl_instrument-> toSearchInstrument()
        }
    }

    private fun showSelectMutableOrderInsertField(fields:List<IOrderInsertField>){
        val orderNames = ArrayList(fields.map { it.toOrderString() }
            .toMutableList())
        val selectedList = ArrayList<IOrderInsertField>()
        val array = arrayOfNulls<String>(fields.size)
        orderNames.toArray(array)
        MaterialAlertDialogBuilder(context)
            .setTitle("请选择")
            .setMultiChoiceItems(array, BooleanArray(fields.size)) { _, which, isChecked ->
                if(isChecked){
                    selectedList.add(fields[which])
                }else{
                    selectedList.remove(fields[which])
                }
            }
            .setPositiveButton("确定") { dialog, _ ->
                if(selectedList.isEmpty()){
                    Snackbar.make(requireView(),"请选择需要操作哪些选项",Snackbar.LENGTH_SHORT)
                    return@setPositiveButton
                }
                dialog.dismiss()
                Log.d("TransactionFragment","选择了哪些？${selectedList.size}")
                showOrderNoticeDialog(selectedList)
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss()}
            .show()
    }

    private fun showOrderNoticeDialog(orderFields:List<IOrderInsertField>){
        altOrderInsert.listener = object : OrderInsertNoticeDialog.OrderInsertNoticeViewListener{
                        override fun onInsertClick() {
                           for(order in orderFields){
                               viewModel?.reqOrderInsert(order)
                           }
                        }
                    }
        val message = orderFields.map { it.toOrderString() }
            .toList().joinToString("\r\n")
        altOrderInsert.showDialog(childFragmentManager,message)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_transaction,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_check_out-> showLogoutDialog()
            R.id.action_search_ins-> toSearchInstrument()
        }
        return true
    }

    private fun showLogoutDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.trade_system_notice)
            .setMessage(R.string.trade_check_out_msg)
            .setNegativeButton(R.string.tv_cancel){dialog,_->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.tv_ensure){ dialog,_->
                viewModel?.reqUserLogoug()
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }

    private fun toSearchInstrument(){
        ARouter.getInstance()
            .build(ARouterPath.Page.PAGE_INS_SEARCH)
            .navigation(activity,REQ_SEARCH_INS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQ_SEARCH_INS && resultCode == Activity.RESULT_OK){
            val insId = data?.getStringExtra("SELECT_INS_ID")
            val instrument =
                ARouterUtils.getQuoteService().getInstrumentById(insId ?: Omits.OmitString)
            if(instrument != null){
                switchOrderIns(instrument)
            }
        }
    }
}