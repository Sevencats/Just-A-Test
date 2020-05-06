package com.adgvcxz.recyclerviewmodel

import androidx.recyclerview.widget.DiffUtil
import com.adgvcxz.IModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * zhaowei
 * Created by zhaowei on 2017/5/12.
 */

fun Observable<List<RecyclerItemViewModel<out IModel>>>.bindTo(adapter: RecyclerAdapter): Disposable {
    return this.observeOn(Schedulers.computation()) // Schedulers.computation() -> 计算任务、事件循环或者回调处理
            .scan(Pair<List<RecyclerItemViewModel<out IModel>>,
                    DiffUtil.DiffResult?>(adapter.viewModel.currentModel().items, null)) { (first), list ->
                val diff = ItemDiffCallback(first, list)
                val result = DiffUtil.calculateDiff(diff, true) //局部刷新
                list to result
            }
            .skip(1) //跳过第一个
            .map { it.second!! } // 取第二个
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter)
}


fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

fun RecyclerAdapter.itemClicks(): Observable<Int> {
    if (action == null) {
        action = PublishSubject.create<Int>().toSerialized() //PublishSubject 线程安全 被订阅之后再发送数据
    }
    return action!!
}