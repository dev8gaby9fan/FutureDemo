package com.future.trade.ui.transaction

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsh.common.base.BaseFragment
import com.fsh.common.base.BaseLazyFragment
import com.future.trade.R
import com.future.trade.model.TransactionViewModel
import com.future.trade.util.DiffComparable
import kotlinx.android.synthetic.main.fragment_transaction_record.*
import java.lang.IllegalArgumentException

/**
 * T是item绑定的数据类型
 * VH item的ViewHolder对象类型
 */
abstract class BaseRecordFragment<T : DiffComparable<T>, VH : RecyclerView.ViewHolder> : BaseLazyFragment() {
    protected var recordList: List<T> = ArrayList()
    protected var viewModel: TransactionViewModel? = null
    protected lateinit var recordAdapter: RecordListAdapter

    override fun layoutRes(): Int = R.layout.fragment_transaction_record


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (parentFragment == null || parentFragment !is TransactionFragment) {
            throw IllegalArgumentException("${javaClass.simpleName} parent is null or not TransactionFragment")
        }
        viewModel = (parentFragment as TransactionFragment).viewModel
        recordAdapter = RecordListAdapter()
        list_record.layoutManager = LinearLayoutManager(context)
        list_record.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        list_record.adapter = recordAdapter
    }

    fun updateDataList(list: List<T>?) {
        if (list != null) {
            val oldList = recordList
            val calculateDiff = DiffUtil.calculateDiff(RecordDiffCallback(oldList, list))
            calculateDiff.dispatchUpdatesTo(recordAdapter)
            recordList = list
        }
    }

    fun getItem(pos: Int): T? = if (pos !in recordList.indices) {
        null
    } else recordList[pos]

    abstract fun createItemViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindItemViewHolder(holder: VH, position: Int)
    abstract fun onBindHeadViewHolder(holder: VH)
    abstract fun createHeadViewHolder(parent: ViewGroup):VH
    open fun onItemViewHolderDetachedFromWindow(holder: VH){}
    inner class RecordListAdapter : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            if(viewType == 0){
                createHeadViewHolder(parent)
            }else{
                createItemViewHolder(parent, viewType)
            }


        override fun getItemCount(): Int = recordList.size+1

        override fun onBindViewHolder(holder: VH, position: Int) =
            if(position == 0){
                onBindHeadViewHolder(holder)
            }else{
                onBindItemViewHolder(holder, position-1)
            }


        override fun onViewDetachedFromWindow(holder: VH) =
            onItemViewHolderDetachedFromWindow(holder)

        override fun getItemViewType(position: Int): Int {
            return if(position == 0) 0
            else 1
        }
    }



    private class RecordDiffCallback<T : DiffComparable<T>>(
        val oldList: List<T>,
        val newList: List<T>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].compare(newList[newItemPosition])
        }

    }
}