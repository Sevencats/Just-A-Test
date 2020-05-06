package com.adgvcxz

//import android.arch.lifecycle.LifecycleObserver
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

abstract class IViewModel<M : IModel>/*: LifecycleObserver*/ { //定义IViewModel抽象类 作为基类
    // action -> PublishSubject 线程安全 被订阅之后再发送数据
    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    //model -> 被观察者
    abstract val model: Observable<M>

    //scan ->
    open fun scan(model: M, mutation: IMutation): M = model

    //mutate -> 转换操作
    open fun mutate(event: IEvent): Observable<IMutation> = Observable.empty()

    //transformMutation ->
    open fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> = mutation

    //transformEvent ->
    open fun transformEvent(event: Observable<IEvent>): Observable<IEvent> = event
}
