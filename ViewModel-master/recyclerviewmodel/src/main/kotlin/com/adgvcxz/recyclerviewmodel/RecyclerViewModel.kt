package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.WidgetViewModel
import io.reactivex.Observable


/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */

class RecyclerModel(values: List<RecyclerItemViewModel<out IModel>>? = null,
                    val hasLoadingItem: Boolean = false,
                    var isAnim: Boolean = false) : IModel {

    var isRefresh: Boolean = false //是否刷新
    var items: List<RecyclerItemViewModel<out IModel>> = arrayListOf()
    var loadingViewModel: LoadingItemViewModel? = null

    init {
        values?.let {
            items = values
        }

        if (hasLoadingItem) { //判断是否加载
            loadingViewModel = LoadingItemViewModel()
        }

        loadingViewModel?.let {
            if (!items.isEmpty()) {
                items += it
            }
        }

        items.forEach {
            it.disposable = it.model.subscribe()
        }
    }

    val isLoading: Boolean
        get() {
            val state = loadingViewModel?.currentModel()?.state
            if (state != null) {
                return state == LoadingItemViewModel.State.Loading
            }
            return false
        }

}

sealed class RecyclerViewEvent : IEvent
object Refresh : RecyclerViewEvent()
object LoadMore : RecyclerViewEvent()


sealed class RecyclerViewMutation : IMutation
data class SetRefresh(val value: Boolean) : RecyclerViewMutation()
data class UpdateData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewMutation()
object LoadFailure : RecyclerViewMutation()


sealed class RecyclerViewEventMutation : IEvent, IMutation
object RemoveLoadingItem : RecyclerViewEventMutation()
data class SetAnim(val value: Boolean) : RecyclerViewEventMutation()
//添加数据
data class AppendData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()

//插入数据
data class InsertData(val index: Int, val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()

//替换数据
data class ReplaceData(val index: List<Int>, val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()

//删除数据
data class RemoveData(val index: List<Int>) : RecyclerViewEventMutation()

//设置数据
data class SetData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()


abstract class RecyclerViewModel : WidgetViewModel<RecyclerModel>() {

    internal var changed: ((RecyclerModel, IMutation) -> Unit)? = null

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            is Refresh -> { //刷新
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty() // 返回一个空的Observable
                }
                val start = Observable.just(SetRefresh(true))
                val end = Observable.just(SetRefresh(false))
                return Observable.concat(start, // 合并三个事件并有序发送
                        request(true).map {
                            when (it) {
                                is UpdateData -> SetData(it.data)
                                else -> it
                            }
                        },
                        end)
            }
            is LoadMore -> { //加载更多
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty() //不发射任何数据
                }

                currentModel().loadingViewModel?.action?.onNext(
                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Loading))

                return request(false)
                        .map {
                            when (it) {
                                //添加数据
                                is UpdateData -> AppendData(it.data)
                                else -> it
                            }
                        }
                        .doOnNext {
                            when (it) {
                                //加载失败
                                is LoadFailure -> currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Failure))
                                //增加数据
                                is AppendData -> currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Success))
                            }
                        }
            }

            is RecyclerViewEventMutation -> return Observable.just(event)
        }
        return super.mutate(event)
    }

    //scan
    override fun scan(model: RecyclerModel, mutation: IMutation): RecyclerModel {
        when (mutation) {
            is SetRefresh -> { //设置刷新
                model.isRefresh = mutation.value
            }
            is SetAnim -> model.isAnim = mutation.value
            is SetData -> { //设置数据
                model.items.forEach {
                    //解除订阅
                    it.dispose()
                }

                model.items = mutation.data
                if (model.hasLoadingItem) {

                    if (model.loadingViewModel == null) {
                        model.loadingViewModel = LoadingItemViewModel()
                    }
                    model.loadingViewModel?.let {
                        if (model.items.isNotEmpty()) {
                            model.items += it
                        }
                    }
                }
                model.items.forEach {
                    it.disposable = it.model.subscribe()
                }
            }
            is AppendData -> { //添加数据
                if (model.items.isNotEmpty() && model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                }
                model.items += mutation.data
                mutation.data.forEach {
                    it.disposable = it.model.subscribe()
                }

                model.loadingViewModel?.let {
                    model.items += it
                }
            }
            is ReplaceData -> { //更改数据
                model.items = model.items.mapIndexed { index, viewModel ->
                    if (mutation.index.contains(index)) {
                        viewModel.dispose()
                        mutation.data[mutation.index.indexOf(index)].also {
                            it.disposable = it.model.subscribe()
                        }
                    } else {
                        viewModel
                    }
                }
            }
            is InsertData -> { // subList -> [0,mutation.index)  插入数据
                model.items = model.items.subList(0, mutation.index) + mutation.data + model.items.subList(
                        mutation.index,
                        model.items.size
                )
            }
            is RemoveData -> { //删除数据
                model.items = model.items.filterIndexed { index, viewModel ->
                    //
                    val exist = mutation.index.contains(index)
                    if (exist) {
                        viewModel.dispose()
                    }
                    !exist
                }
            }
            is RemoveLoadingItem -> {
                if (model.items.isNotEmpty() && model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                    model.loadingViewModel?.dispose()
                    model.loadingViewModel = null
                }
            }
        }
        changed?.invoke(model, mutation)
        return model
    }

    open fun request(refresh: Boolean): Observable<IMutation> = Observable.empty()

    val count: Int
        get() = currentModel().items.size
}