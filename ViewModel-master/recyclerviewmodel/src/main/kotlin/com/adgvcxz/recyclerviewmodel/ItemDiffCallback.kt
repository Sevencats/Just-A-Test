package com.adgvcxz.recyclerviewmodel

import androidx.recyclerview.widget.DiffUtil
import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/7.
 */

//ItemDiffCallback -> 局部刷新类
class ItemDiffCallback(private val oldItems: List<RecyclerItemViewModel<out IModel>>,
                       private val newItems: List<RecyclerItemViewModel<out IModel>>) : DiffUtil.Callback() {

    // return true -> The same item  两个位置的对象是否是同一个item
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].currentModel() == newItems[newItemPosition].currentModel()

    //旧数据的size 返回原始列表的size
    override fun getOldListSize(): Int = oldItems.size

    //新数据的size  返回新列表的size
    override fun getNewListSize(): Int = newItems.size

    //决定两个item的数据是否相同，只有当areItemsTheSame()返回true时才调用
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]


}
