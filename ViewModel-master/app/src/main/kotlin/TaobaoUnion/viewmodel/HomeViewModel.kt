package TaobaoUnion.viewmodel

import TaobaoUnion.model.domain.Categories
import TaobaoUnion.utils.api
import android.util.Log
import com.adgvcxz.AFViewModel
import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class HomeViewModel : AFViewModel<HomeViewModel.CategoriesModel>() {

    override val initModel: CategoriesModel = CategoriesModel()

    enum class Event : IEvent {
        StartEvent, EndEvent
    }

    enum class Mutation : IMutation {
        StartTimer, StopTimer, PauseTimer, Timing
    }

    data class W(val data: List<Categories.Data>) : IMutation

    enum class TimerStatus {
        Completed, Timing, Pause
    }

    class CategoriesModel : IModel {
        var categories = emptyList<Categories.Data>()
        var state = TimerStatus.Completed
    }

    override fun mutate(event: IEvent): Observable<IMutation> {

        Log.d("mutate", "mutation  $event")
        when (event) {
            Event.StartEvent -> {
                val api = api
                val task = api.getCategories()
                return task.toRx().map {
                    W(it.data) }
            }
            Event.EndEvent -> return Observable.just(Mutation.StopTimer).filter {
                this.currentModel().state == TimerStatus.Timing
            }.map { it }

        }
        return Observable.empty()
    }

    override fun scan(model: CategoriesModel, mutation: IMutation): CategoriesModel {

        Log.d("scan","$mutation")

        when (mutation) {
            Mutation.StartTimer -> return model.also {
                it.state = TimerStatus.Timing
            }
            Mutation.Timing -> return model.also {
                it.state = TimerStatus.Timing
            }
            Mutation.PauseTimer -> return model.also {
                it.state = TimerStatus.Pause
            }
            Mutation.StopTimer -> return model.also {
                it.state = TimerStatus.Completed
            }
            is W -> return model.also{
                it.categories = mutation.data
            }
        }
        return model
    }
}

fun <T> Call<T>.toRx() =
        Observable.create<T> {
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    //加载错误
                    Log.d("Internet","Error")
                    it.onError(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val code = response.code()
                    if (code == HttpURLConnection.HTTP_OK) {
                        //请求成功
                        val categories = response.body()!!
                        Log.d("LihC","数据 : $categories")
                        it.onNext(categories)
                        it.onComplete()
                    }
                }
            })
        }
