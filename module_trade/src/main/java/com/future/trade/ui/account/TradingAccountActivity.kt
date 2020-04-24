package com.future.trade.ui.account

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.fsh.common.base.BaseActivity
import com.fsh.common.ext.viewModelOf
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
                tv_balance.text = it.balance.toString()
                tv_with_draw_quota.text = it.withDrawQuota.toString()
                tv_available.text = it.avaliable.toString()
                tv_close_profit.text = it.closeProfit.toString()
                tv_pos_profit.text = it.positionProfit.toString()
                tv_margin.text = it.currMargin.toString()
                tv_frozen_margin.text = it.frozenMargin.toString()
                tv_with_draw_money.text = it.withDraw.toString()
                tv_deposit.text = it.deposit.toString()
                tv_commission.text = it.commission.toString()
                tv_frozen_commission.text = it.frozenCommission.toString()
                tv_pre_fund_mortgage_in.text = it.preFundMortgageIn.toString()
                tv_pre_fund_mortgage_out.text = it.preFundMortgageOut.toString()
                tv_fund_mortgage_in.text = it.fundMortgageIn.toString()
                tv_fund_mortgage_out.text = it.fundMortgageOut.toString()
                tv_fund_mortgage_available.text = it.fundMortgageAvailable.toString()
                tv_mortgageable_fund.text = it.mortgageableFund.toString()
                tv_spec_product_margin.text = it.specProductMargin.toString()
                tv_spec_product_frozen_margin.text = it.specProductFrozenMargin.toString()
                tv_spec_product_commission.text = it.specProductCommission.toString()
                tv_spec_product_frozen_commission.text = it.specProductFrozenCommission.toString()
                tv_spec_product_position_profit.text = it.specProductPositionProfit.toString()
                tv_spec_product_close_profit.text = it.specProductCloseProfit.toString()
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
