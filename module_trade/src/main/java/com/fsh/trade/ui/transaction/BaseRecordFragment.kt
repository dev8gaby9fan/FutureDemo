package com.fsh.trade.ui.transaction

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseFragment
import com.fsh.trade.R
import com.fsh.trade.model.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_transaction_record.*
import java.lang.IllegalArgumentException

/**
 * T是item绑定的数据类型
 * VH item的ViewHolder对象类型
 */
abstract class BaseRecordFragment<T, VH : RecyclerView.ViewHolder> : BaseFragment() {
    private var isLoaded: Boolean = false
    private var recordList: List<T> = ArrayList()
    protected var viewModel:TransactionViewModel? = null
    private lateinit var recordAdapter:RecordListAdapter
    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            lazyLoading()
            isLoaded = true
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_transaction_record

    override fun onDetach() {
        super.onDetach()
        isLoaded = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(parentFragment == null || parentFragment !is TransactionFragment){
            throw IllegalArgumentException("${javaClass.simpleName} parent is null or not TransactionFragment")
        }
        viewModel = (parentFragment as TransactionFragment).viewModel
        recordAdapter = RecordListAdapter()
        list_record.layoutManager = LinearLayoutManager(context)
        list_record.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
        list_record.adapter = recordAdapter
    }

    abstract fun lazyLoading()
    abstract fun createItemViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindItemViewHolder(holder: VH,position: Int)
    inner class RecordListAdapter : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            createItemViewHolder(parent, viewType)

        override fun getItemCount(): Int = recordList.size

        override fun onBindViewHolder(holder: VH, position: Int) = onBindItemViewHolder(holder,position)
    }
}