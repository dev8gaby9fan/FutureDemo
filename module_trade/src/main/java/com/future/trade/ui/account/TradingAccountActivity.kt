package com.future.trade.ui.account

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.fsh.common.base.BaseActivity
import com.fsh.common.ext.viewModelOf
import com.fsh.common.util.NumberUtils
import com.future.trade.R
import com.future.trade.model.TransactionViewModel
import kotlinx.android.synthetic.main.activity_trading_account.*
import kotlinx.android.synthetic.main.activity_trading_account.tv_available
import kotlinx.android.synthetic.main.activity_trading_account.tv_balance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TradingAccountActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_trading_account
    override fun getStatusBarColorRes(): Int = R.color.colorPrimaryDark
    private var viewModel:TransactionViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initData()
    }

    private fun initViews(){
        setSupportActionBar(title_toolbar)
        supportActionBar.apply {
            this?.setHomeButtonEnabled(true)
            this?.setHomeAsUpIndicator(R.drawable.ic_back)
            this?.setDefaultDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun initData(){
        viewModel = viewModelOf<TransactionViewModel>().value.apply {
            tradingAccountLiveData.observe(this@TradingAccountActivity, Observer {
                dismissLoading()
                tv_currency_id.text = it.currencyID
                tv_balance.text = NumberUtils.formatNum(it.balance.toString(),"0.001")
                tv_with_draw_quota.text = NumberUtils.formatNum(it.withDrawQuota.toString(),"0.001")
                tv_available.text = NumberUtils.formatNum(it.avaliable.toString(),"0.001")
                tv_close_profit.text = NumberUtils.formatNum(it.closeProfit.toString(),"0.001")
                tv_pos_profit.text = NumberUtils.formatNum(it.positionProfit.toString(),"0.001")
                tv_margin.text = NumberUtils.formatNum(it.currMargin.toString(),"0.001")
                tv_frozen_margin.text = NumberUtils.formatNum(it.frozenMargin.toString(),"0.001")
                tv_with_draw_money.text = NumberUtils.formatNum(it.withDraw.toString(),"0.001")
                tv_deposit.text = NumberUtils.formatNum(it.deposit.toString(),"0.001")
                tv_commission.text = NumberUtils.formatNum(it.commission.toString(),"0.001")
                tv_frozen_commission.text = NumberUtils.formatNum(it.frozenCommission.toString(),"0.001")
                tv_pre_fund_mortgage_in.text = NumberUtils.formatNum(it.preFundMortgageIn.toString(),"0.001")
                tv_pre_fund_mortgage_out.text = NumberUtils.formatNum(it.preFundMortgageOut.toString(),"0.001")
                tv_fund_mortgage_in.text = NumberUtils.formatNum(it.fundMortgageIn.toString(),"0.001")
                tv_fund_mortgage_out.text = NumberUtils.formatNum(it.fundMortgageOut.toString(),"0.001")
                tv_fund_mortgage_available.text = NumberUtils.formatNum(it.fundMortgageAvailable.toString(),"0.001")
                tv_mortgageable_fund.text = NumberUtils.formatNum(it.mortgageableFund.toString(),"0.001")
                tv_spec_product_margin.text = NumberUtils.formatNum(it.specProductMargin.toString(),"0.001")
                tv_spec_product_frozen_margin.text = NumberUtils.formatNum(it.specProductFrozenMargin.toString(),"0.001")
                tv_spec_product_commission.text = NumberUtils.formatNum(it.specProductCommission.toString(),"0.001")
                tv_spec_product_frozen_commission.text = NumberUtils.formatNum(it.specProductFrozenCommission.toString(),"0.001")
                tv_spec_product_position_profit.text = NumberUtils.formatNum(it.specProductPositionProfit.toString(),"0.001")
                tv_spec_product_close_profit.text = NumberUtils.formatNum(it.specProductCloseProfit.toString(),"0.001")
                nav_title.text = getString(R.string.tv_account_info,it.accountID,it.brokerID)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trading_account,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_refresh -> {
                qryTradingAccount()
                true
            }
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun qryTradingAccount(){
        setLoadingDialogMessage("加载数据中")
        showLoadingDialog()
        GlobalScope.launch {
            delay(1000)
            viewModel?.reqQryTradingAccount()
        }
    }
}
