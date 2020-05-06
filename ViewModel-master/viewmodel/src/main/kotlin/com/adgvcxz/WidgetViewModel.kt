package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * zhaowei
 * Created by zhaowei on 2017/6/3.
 */
abstract class WidgetViewModel<M : IModel> : IViewModel<M>() { //基本实现方式同AFViewModel

    //initialization initModel maybe have some parameters, so there is no abstract method initModel()
    abstract val initModel: M

    private var _currentModel: M? = null

    override val model: Observable<M> by lazy {
        this.action
                .compose {
                    transformEvent(it) } //立即执行 -> 转换成为event
                .flatMap {
                    this.mutate(it) }  //转换为IMutation
                .compose {
                    transformMutation(it) }//转换为mutation
                .scan(initModel) { model, mutation -> scan(model, mutation) } //...model
                .doOnError { it.printStackTrace() } //打印错误信息
                .retry() //重试
                .observeOn(AndroidSchedulers.mainThread()) //指定下一个操作在主线程
                .doOnNext {
                    _currentModel = it } //被订阅之前进行赋值转换
                .replay(1)//以后的订阅者仍然可以获得先前的事件 为新的subscriber重放他之前所收到的上游数据
                .refCount() //在有订阅者之前，连接将不会开始 ，至少有一个订阅者才开始发送数据
    }

    fun currentModel(): M = _currentModel ?: initModel // currentModel or initModel
//    fun initModel():T = currentModel()

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//        this.action.onNext(WidgetLifeCircleEvent.Attach)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        this.action.onNext(WidgetLifeCircleEvent.Detach)
//    }
}
