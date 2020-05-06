package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * zhaowei
 * Created by zhaowei on 2018/5/3.
 */
class ViewModelBuilder<M: IModel> { //ViewModelBuilder

    private val sectionList = arrayListOf<ViewModelSection<M, Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <S> section(init: ViewModelSection<M, S>.() -> Unit) {
        val section = ViewModelSection<M, S>()
        section.init()
        sectionList.add(section as ViewModelSection<M, Any>)
    }

    fun build(viewModel: IViewModel<M>): List<Disposable> {
        return sectionList.map {
            it.build(viewModel)
        }.reduce { acc, list -> list + acc } // 累加
    }
}

class ViewModelSection<M : IModel, S> { //ViewModelSection

    var filter: (Observable<S>.() -> Observable<S>)? = null //filter -> observable

    private val itemList = arrayListOf<ViewModelItem<M, S, Any>>()

    fun filter(init: Observable<S>.() -> Observable<S>) {
        filter = init
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> mapItem(init: ViewModelItem<M, S, R>.() -> Unit) {
        val item = ViewModelItem<M, S, R>()
        item.init()
        itemList.add(item as ViewModelItem<M, S, Any>)
    }

    @Suppress("UNCHECKED_CAST")
    fun item(init: ViewModelItem<M, S, S>.() -> Unit) {
        val item = ViewModelItem<M, S, S>()
        item.init()
        itemList.add(item as ViewModelItem<M, S, Any>)
    }

    fun build(viewModel: IViewModel<M>): List<Disposable> {
        return itemList.map { item -> //遍历
            viewModel.model.map {
                item.value.invoke(it) }
                    .compose {
                        filter?.invoke(it) ?: it }
                    .compose {
                        item.filter?.invoke(it) ?: it }
                    .map {
                        item.map?.invoke(it) ?: it } // value -> observable
                    .subscribe {
                        item.behavior.accept(it) }
        }
    }

    class ViewModelItem<M : IModel, S, R> {

        lateinit var value: (M.() -> S)
        lateinit var behavior: (Consumer<in R>) // behavior -> consumer
        var filter: (Observable<S>.() -> Observable<S>)? = null
        var map: (S.() -> R)? = null //

        fun behavior(init: (R) -> Unit) {
            behavior = Consumer {
                init.invoke(it) } // behavior -> consumer
        }

        fun behavior(init: Consumer<in R>) {
            behavior = init
        }

        fun value (init: M.() -> S) {
            value = init
        }

        fun map(init: S.() -> R) {
            map = init
        }

        fun filter(init: Observable<S>.() -> Observable<S>) {
            filter = init
        }
    }
}


fun <M: IModel> IViewModel<M>.toBuilder(init: ViewModelBuilder<M>.() -> Unit): List<Disposable> {
    val builder = ViewModelBuilder<M>()
    builder.init() // builder被初始化  init(builder)
    return builder.build(this)
}
