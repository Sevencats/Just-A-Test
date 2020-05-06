package com.adgvcxz.recyclerviewmodel

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */

// RecyclerView.ViewHolder

open class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    //管理订阅
    val disposables: CompositeDisposable by lazy { CompositeDisposable() }

}
