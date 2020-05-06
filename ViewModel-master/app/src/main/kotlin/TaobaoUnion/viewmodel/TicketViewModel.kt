package TaobaoUnion.viewmodel

import TaobaoUnion.model.Api

import TaobaoUnion.model.domain.TicketRequestItem
import TaobaoUnion.model.domain.TicketResult
import TaobaoUnion.utils.UrlUtils
import TaobaoUnion.utils.api
import com.adgvcxz.AFViewModel
import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import io.reactivex.Observable


class TicketViewModel(private val url: String, private val title: String) : AFViewModel<TicketViewModel.Model>() {

    override val initModel: Model = Model()

    enum class Event : IEvent {
        StartEvent, EndEvent, TicketCodeEvent
    }

    enum class TimerStatus {
        Completed, Timing, Pause, Finish, Skip
    }

    enum class Mutation : IMutation {
        StartTimer, StopTimer, PauseTimer, Timing
    }

    data class TicketResultModel(val data: TicketResult) : IMutation

    class Model : IModel {
        var ticketCode: String = ""
        var state = TimerStatus.Completed
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            Event.StartEvent -> {
                val targetUrl = UrlUtils.getCoverPath(url)
                val ticketRequestItem = TicketRequestItem(title, targetUrl)
                val api: Api = api
                val task = api.getTicketByUrl(ticketRequestItem)
                return task.toRx().map { TicketResultModel(it) }
            }

            Event.TicketCodeEvent -> return Observable.just(Mutation.StartTimer).map { it }

            Event.EndEvent -> return Observable.just(Mutation.StopTimer).map { it }
        }
        return Observable.empty()
    }

    override fun scan(model: Model, mutation: IMutation): Model {

        when (mutation) {
            Mutation.StartTimer -> return model.also {
                it.state = TimerStatus.Skip
            }
            Mutation.Timing -> return model.also {
                it.state = TimerStatus.Timing
            }
            Mutation.PauseTimer -> return model.also {
                it.state = TimerStatus.Pause
            }
            Mutation.StopTimer -> return model.also {
                it.state = TimerStatus.Finish
            }
            is TicketResultModel -> return model.also {
                it.ticketCode = mutation.data.data.tbk_tpwd_create_response.data.model
                it.state = TimerStatus.Timing
            }
        }
        return model
    }

}

