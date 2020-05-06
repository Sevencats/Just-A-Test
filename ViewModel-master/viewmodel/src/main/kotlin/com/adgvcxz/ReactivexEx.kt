package com.adgvcxz

import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */


// 用 CompositeDisposable 可以将 Disposable 统一管理 (防止内存泄漏)
@Suppress("UNCHECKED_CAST")
fun <T, R : T> Observable<T>.bindTo(observer: Subject<in R>): Disposable = this.subscribe {
    observer.onNext(it as R)
}

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}

fun List<Disposable>.addTo(disposables: CompositeDisposable) {
    disposables.addAll(*this.toTypedArray())
}

interface IBind<M : IModel, T> {
    fun bind(value: T)
    fun data(model: M): T
    fun filter(observable: Observable<T>): Observable<T>
}

class Bind<M : IModel, T>(
        val data: M.() -> T,
        val action: T.() -> Unit,
        val filter: (Observable<T>.() -> Observable<T>)? = null
) : IBind<M, T> {
    override fun filter(observable: Observable<T>): Observable<T> {
        return observable.flatMap {
            filter?.invoke(Observable.just(it)) ?: Observable.just(it)
        }
    }

    override fun data(model: M): T {
//        return  data.invoke(model)
        return data.invoke(model)
    }

    override fun bind(value: T) {
        action.invoke(value)
    }
}


fun <M : IModel, T> ViewModelBuilder<M>.add( // ViewModelBuilder的扩展函数add
        data: M.() -> T,
        action: T.() -> Unit,
        filter: (Observable<T>.() -> Observable<T>)? = null
) {
    section<T> {
        filter {
            distinctUntilChanged()
        } //判定的是Observable发射的当前数据项和前一个数据项是否相同,确保相邻元素不重复出现
        item {
            value {
                data(this)
            }
            filter {
                filter?.invoke(this) ?: this
            }
            behavior {
                action.invoke(it)
            }
        }
    }
}

fun <M : IModel> IViewModel<M>.toBind( // IViewModel的扩展函数toBind
        disposables: CompositeDisposable,
        init: ViewModelBuilder<M>.() -> Unit
) {
    val builder = ViewModelBuilder<M>()
    builder.init()
    builder.build(this).addTo(disposables)
}

fun <M : IModel> IViewModel<M>.toEventBind( // IViewModel的扩展函数toEventBind
        disposables: CompositeDisposable,
        init: EventBuilder.() -> Any
) {
    val builder = EventBuilder()
    builder.init()
    builder.build(this.action).addTo(disposables)
}

fun <M : IModel, T> ViewModelBuilder<M>.add( //ViewModelBuilder的扩展函数add
        data: M.() -> T,
        action: Consumer<T>,
        filter: (Observable<T>.() -> Observable<T>)? = null
) {
    section<T> {
        filter {
            distinctUntilChanged()
        } //判定的是Observable发射的当前数据项和前一个数据项是否相同,确保相邻元素不重复出现
        item {
            value {
                data(this)
            }
            filter {
                filter?.invoke(this) ?: this
            }
            behavior = action
        }
    }
}

fun <T, V : View> EventBuilder.add( //EventBuilder的扩展函数add

        observable: V.() -> Observable<T>,
        widget: V,
        action: T.() -> IEvent
) {
    section<T, V> {
        observable {
            observable()
        }
        item {
            view = widget
            event { action.invoke(this) }
        }
    }
}

fun <T, V : View> EventBuilder.addAction( //EventBuilder的扩展函数addAction
        observable: V.() -> Observable<T>,
        widget: V,
        action: () -> Unit
) {
    section<T, V> {
        observable {
            observable()
        }
        actionItem {
            view = widget
            action {
                action.invoke()
            }
        }
    }
}

fun <T, V : View> EventBuilder.add( //EventBuilder的扩展函数add
        observable: V.() -> Observable<T>,
        vararg list: Pair<V, () -> IEvent>
) {
    section<T, V> {
        observable {
            observable()
        }
        for (data in list) {
            item {
                view = data.first
                event {
                    data.second.invoke()
                }
            }
        }
    }
}

fun <T, V : View> EventBuilder.addAction( //EventBuilder的扩展函数addAction
        observable: V.() -> Observable<T>,
        vararg list: Pair<V, () -> Any>
) {
    section<T, V> {
        observable {
            observable()
        }
        for (data in list) {
            actionItem {
                view = data.first
                action {
                    data.second.invoke() }
            }
        }
    }
}

