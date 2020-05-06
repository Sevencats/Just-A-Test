package TaobaoUnion.viewmodel

import TaobaoUnion.model.Api
import TaobaoUnion.model.domain.Data2
import TaobaoUnion.utils.api
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.adgvcxz.*
import com.adgvcxz.recyclerviewmodel.*
import com.adgvcxz.viewmodel.sample.R
import com.adgvcxz.viewmodel.sample.RxBus
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable

class SelectedPageViewModel : RecyclerViewModel() {

    override val initModel: RecyclerModel = RecyclerModel(null, hasLoadingItem = false, isAnim = true)
    //请求数据
    override fun request(refresh: Boolean): Observable<IMutation> {
        val task = api.getSelectedPageCategories()
        return task.toRx().map {
            if (refresh)
                SetData(it.data.map {
                    SelectedPageTextItemViewModel(it)
                }) else AppendData(
                    it.data.map {
                        SelectedPageTextItemViewModel(it)
                    })
        }
    }

}

class SelectedPageTextItemView : IView<SelectedPageTextItemView.TextItemViewHolder, SelectedPageTextItemViewModel> {

    override val layoutId: Int = R.layout.item_selected_page_left

    class TextItemViewHolder(view: View) : ItemViewHolder(view) {
        val itemTextView: TextView = view.findViewById(R.id.left_category_tv)
    }

    override fun initView(view: View, parent: ViewGroup): TextItemViewHolder = TextItemViewHolder(view)

    override fun bind(viewHolder: TextItemViewHolder, viewModel: SelectedPageTextItemViewModel, position: Int) {
        viewModel.toBind(viewHolder.disposables) {
            add({ favoritesTitle }, { viewHolder.itemTextView.text = this })

            //1.
//            add({ isChecked }, {
//                if (this) {
//                    viewHolder.itemTextView.setBackgroundColor(Color.parseColor("#EFEEEE"))//灰色
//                } else {
//                    viewHolder.itemTextView.setBackgroundColor(Color.parseColor("#ffffff"))//白色
//                }
//            })

        }

        //1.
//        viewModel.toEventBind(viewHolder.disposables){
//            add({clicks()},viewHolder.itemTextView to {
//                ClickItem(viewModel.currentModel().favoritesId)

//            })
//        }

    }

}

//1.
//data class ClickItem(val id:Int):IEvent,IMutation

class SelectedPageTextItemViewModel(private val model_: Data2) : RecyclerItemViewModel<SelectedPageTextItemViewModel.SelectedPageCategoryModel>() {

    class ValueChangeMutation(val value: List<Data2>) : IMutation

    class ValueChangeEvent(val value: List<Data2>) : IEvent

    class SelectedPageCategoryModel(val favoritesTitle: String, val favoritesId: Int) : IModel {
        var selectedPageCategory = emptyList<Data2>()
        var isChecked = false
    }

    override val initModel: SelectedPageCategoryModel = SelectedPageCategoryModel(model_.favorites_title, model_.favorites_id)

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {

        val value = RxBus.instance.toObservable(ValueChangeEvent::class.java)
                .map { ValueChangeMutation(it.value) }

        return Observable.merge(value, mutation)
    }

    override fun scan(model: SelectedPageCategoryModel, mutation: IMutation): SelectedPageCategoryModel {
        when (mutation) {
            is ValueChangeMutation -> model.selectedPageCategory = mutation.value
//            is ClickItem -> model.isChecked = mutation.id==currentModel().favoritesId//1.
        }
        return model
    }

    //1.

//    override fun mutate(event: IEvent): Observable<IMutation> {
//        if (event is ClickItem){
//            return Observable.just(event)
//        }
//        return super.mutate(event)
//    }
}

