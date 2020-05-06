package TaobaoUnion.viewmodel

import TaobaoUnion.model.domain.UatmTbkItem
import TaobaoUnion.utils.UrlUtils
import TaobaoUnion.utils.api
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.adgvcxz.*
import com.adgvcxz.recyclerviewmodel.*
import com.adgvcxz.viewmodel.sample.R
import com.adgvcxz.viewmodel.sample.RxBus
import com.bumptech.glide.Glide
import io.reactivex.Observable


/**
 * time:2020.4.30
 * author:LihC
 */

class SelectedPageContentViewModel(val id: Int) : RecyclerViewModel() {
    override val initModel: RecyclerModel = RecyclerModel(null, true, isAnim = true)

    override fun request(refresh: Boolean): Observable<IMutation> {
        val targetUrl = UrlUtils.getTypContentUrl(id)
        val task = api.getSelectedPageContent(targetUrl)
        return task.toRx().map {
            if (refresh) {
                SetData(it.data.tbk_uatm_favorites_item_get_response.results.uatm_tbk_item.map {
                    SelectedPageContentTextItemViewModel(it)
                })
            } else {
                AppendData(it.data.tbk_uatm_favorites_item_get_response.results.uatm_tbk_item.map {
                    SelectedPageContentTextItemViewModel(it)
                })
            }
        }
    }
}

class SelectedPageContentTextItemView : IView<SelectedPageContentTextItemView.TextItemViewHolder, SelectedPageContentTextItemViewModel> {

    override val layoutId: Int = R.layout.item_selected_page_content

    override fun initView(view: View, parent: ViewGroup): TextItemViewHolder = TextItemViewHolder(view)

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: TextItemViewHolder, viewModel: SelectedPageContentTextItemViewModel, position: Int) {
        viewModel.toBind(viewHolder.disposables) {
            add({ cover }, { Glide.with(viewHolder.coverImageView).load(this).into(viewHolder.coverImageView) })
            add({ title }, { viewHolder.titleTv.text = this })
            add({ offPrice }, {
                if (this.isNotEmpty()) {
                    viewHolder.offPriceTv.visibility = View.VISIBLE
                    viewHolder.offPriceTv.text = this
                } else {
                    viewHolder.offPriceTv.visibility = View.GONE
                }
            })
            add({ (couponClickUrl) }, {
                if (this.isNotEmpty()) {
                    viewHolder.originalPriceTv.text = "原价:${viewModel.currentModel().finalPrice} 元"
                } else {
                    viewHolder.originalPriceTv.text = "噢啦，已经没有优惠卷了"
                    viewHolder.buyBtnTv.visibility = View.GONE
                }
            })

        }
    }

    class TextItemViewHolder(view: View) : ItemViewHolder(view) {
        val coverImageView: ImageView = view.findViewById(R.id.selected_cover)
        val offPriceTv: TextView = view.findViewById(R.id.selected_off_price)
        val titleTv: TextView = view.findViewById(R.id.selected_title)
        val buyBtnTv: TextView = view.findViewById(R.id.selected_buy_btn)
        val originalPriceTv: TextView = view.findViewById(R.id.selected_original_price)
    }

}

class SelectedPageContentTextItemViewModel(private val model_: UatmTbkItem) : RecyclerItemViewModel<SelectedPageContentTextItemViewModel.SelectedPageContentModel>() {
    //
//    class ValueChangeMutation(val value: List<UatmTbkItem>) : IMutation
//    class ValueChangeEvent(val value: List<UatmTbkItem>) : IEvent
    class SelectedPageContentModel(var cover: String, var title: String, var offPrice: String,
                                   var couponClickUrl: String, var finalPrice: String
    ) : IModel {
        //可以给一个状态值
//        var SelectedPageContentData = emptyList<UatmTbkItem>()
    }

    override val initModel: SelectedPageContentModel = SelectedPageContentModel(model_.pict_url, model_.title, model_.coupon_info
            ?: "", model_.coupon_click_url ?: model_.click_url, model_.zk_final_price ?: "")

//    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
//        val value = RxBus.instance.toObservable(ValueChangeEvent::class.java).map {
//            ValueChangeMutation(it.value)
//        }
//        return Observable.merge(value,mutation)
//    }

//    override fun scan(model: SelectedPageContentModel, mutation: IMutation): SelectedPageContentModel {
//        when(mutation){
//            is ValueChangeMutation -> {
//               model.SelectedPageContentData = mutation.value
//            }
//        }
//        return model
//    }
}