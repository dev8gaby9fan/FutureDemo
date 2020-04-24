package com.future.quote.ui.search

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.fsh.common.base.BaseActivity
import com.fsh.common.ext.viewModelOf
import com.fsh.common.model.ARouterPath
import com.fsh.common.model.InstrumentInfo
import com.future.quote.R
import com.future.quote.service.QuoteInfoMgr
import com.future.quote.viewmodel.SearchInstrumentViewModel
import kotlinx.android.synthetic.main.quote_activity_instrument_search.*
import kotlinx.android.synthetic.main.quote_layout_item_instrument.view.*
import okhttp3.internal.toHexString

@Route(path = ARouterPath.Page.PAGE_INS_SEARCH)
class InstrumentSearchActivity : BaseActivity() {
    private var searchView:SearchView? = null
    private var searchAutoComplete:SearchView.SearchAutoComplete? = null
    private var viewModel:SearchInstrumentViewModel? = null

    companion object{
        const val SELECT_INS_ID = "SELECT_INS_ID"
    }

    override fun layoutRes(): Int = R.layout.quote_activity_instrument_search

    override fun getStatusBarColorRes(): Int = R.color.colorPrimaryDark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        viewModel = viewModelOf<SearchInstrumentViewModel>().value.apply {
            searchInsLiveData.observe(this@InstrumentSearchActivity, Observer {
                updateSearchResultView(it)
            })
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        list_search_result.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
    }

    override fun onBackPressed() {
        if (searchAutoComplete != null && searchAutoComplete!!.isShown) {
            try {
                searchAutoComplete?.setText("")
                val method = searchView?.javaClass?.getDeclaredMethod("onCloseClicked")
                method?.isAccessible = true
                method?.invoke(searchView)
            } catch (e: Exception) {

            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.quote_search_ins,menu)
        val searchMenu = menu?.findItem(R.id.menu_search)
        searchView = searchMenu?.actionView as SearchView
        val searchAutoCompleteId = searchView!!.resources.getIdentifier("android:id/search_src_text",null,null)
        searchAutoComplete = searchView?.findViewById(searchAutoCompleteId)
        searchView?.queryHint = getString(R.string.quote_search_hint)
        searchAutoComplete?.setHintTextColor(getColor(R.color.color_text_gray))
        searchAutoComplete?.setTextColor(getColor(R.color.white))
        searchAutoComplete?.textSize = resources.getDimension(R.dimen.abc_dp_14)
        //设置查询触发的最少字符数
        searchAutoComplete?.threshold = 1
        //设置搜索框有字时显示清除按钮
        searchView?.onActionViewExpanded()
        searchView?.isIconified = true

        //搜索框的距离离NavigationButton的距离更近一点
        val searchEditFrameId = searchView!!.resources.getIdentifier("$packageName:id/search_edit_frame",null,null)
        val searchEditFrame = searchView?.findViewById(searchEditFrameId) as LinearLayout?
        val params = searchEditFrame?.layoutParams as ViewGroup.MarginLayoutParams?
        params?.leftMargin = 0
        params?.rightMargin = 0
        searchEditFrame?.layoutParams = params
        //右边的搜索按钮设置图片
        val searchBtnId = searchView!!.resources.getIdentifier("$packageName:id/search_button",null,null)
        val searchBtn = searchView?.findViewById<ImageView>(searchBtnId)
        searchBtn?.setImageResource(R.drawable.ic_search)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean  = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel?.searchInstrument(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateSearchResultView(list:List<InstrumentInfo>){
        var adapter:SearchInsAdapter? = list_search_result.adapter as SearchInsAdapter?
        if(adapter == null){
            adapter = SearchInsAdapter(list)
            list_search_result.adapter = adapter
        }
        adapter.insList = list
        adapter.notifyDataSetChanged()
    }

    inner class SearchInsAdapter(var insList:List<InstrumentInfo>) : RecyclerView.Adapter<SearchItemVH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemVH =
            SearchItemVH(LayoutInflater.from(parent.context).inflate(R.layout.quote_layout_item_instrument,parent,false))

        override fun getItemCount(): Int = insList.size

        override fun onBindViewHolder(holder: SearchItemVH, position: Int) {
            val ins = insList[position]
            val insSpannable = SpannableString(ins.name+"("+ins.id+")")
            insSpannable.setSpan(ForegroundColorSpan(holder.itemView.resources.getColor(R.color.dark_light)),0,ins.name.length,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            insSpannable.setSpan(AbsoluteSizeSpan(holder.itemView.resources.getDimensionPixelSize(R.dimen.abc_sp_16)),0,ins.name.length,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            insSpannable.setSpan(ForegroundColorSpan(holder.itemView.resources.getColor(R.color.color_text_gray)),ins.name.length,insSpannable.length,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            insSpannable.setSpan(AbsoluteSizeSpan(holder.itemView.resources.getDimensionPixelSize(R.dimen.abc_sp_14)),ins.name.length,insSpannable.length,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            holder.itemView.tv_ins_name.text = insSpannable
            holder.itemView.tv_ins_exchange.text = QuoteInfoMgr.mgr.getExchange(ins.eid).name
            holder.itemView.setOnClickListener {
                intent.putExtra(SELECT_INS_ID,ins.id)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    class SearchItemVH(item:View) : RecyclerView.ViewHolder(item)
}
