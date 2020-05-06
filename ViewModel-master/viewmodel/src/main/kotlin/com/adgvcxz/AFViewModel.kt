package com.adgvcxz

//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.OnLifecycleEvent
//import android.arch.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

//AFViewModel
abstract class AFViewModel<M : IModel> : /*: ViewModel(),*/ IViewModel<M>() {

    abstract val initModel: M

    private var _currentModel: M? = null

    override val model: Observable<M> by lazy {
        Log.d("zhaow","AFViewModel")
        this.action
                .compose {
                    transformEvent(it) } //立即执行 -> event
                .flatMap {
                    Log.d("zhaow","AFViewModel $it")
                    this.mutate(it) } // 转换 -> Observable.empty()
                .compose {
                    transformMutation(it) } // 立即执行 -> mutation
                .scan(initModel) { model, mutation -> scan(model, mutation) } //...model
                .doOnError {
                    it.printStackTrace() } //若发生错误 打印错误信息
                .retry() //重试
                .observeOn(AndroidSchedulers.mainThread()) //下一个操作符在主线程
                .doOnNext {
                    _currentModel = it }
                .replay(1) //为新的subscriber重放他之前所收到的上游数据 以后的订阅者仍然可以获得先前发出的事件
                .refCount() // 至少有一个订阅者才发送数据 在有订阅者之前连接将不会开始
    }

    fun currentModel(): M = _currentModel ?: initModel

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//        this.action.onNext(AFLifeCircleEvent.Create)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun onResume() {
//        this.action.onNext(AFLifeCircleEvent.Resume)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart() {
//        this.action.onNext(AFLifeCircleEvent.Start)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun onPause() {
//        this.action.onNext(AFLifeCircleEvent.Pause)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop() {
//        this.action.onNext(AFLifeCircleEvent.Stop)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        this.action.onNext(AFLifeCircleEvent.Destroy)
//    }
}
